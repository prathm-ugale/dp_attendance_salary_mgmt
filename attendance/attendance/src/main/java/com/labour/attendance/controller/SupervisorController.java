package com.labour.attendance.controller;

import com.labour.attendance.dto.MonthlyMoneyUpdateRequest;
import com.labour.attendance.dto.SupervisorMonthCellUpdateRequest;
import com.labour.attendance.dto.SupervisorMonthGridDTO;
import com.labour.attendance.dto.SupervisorMonthGridRowDTO;
import com.labour.attendance.entity.Attendance;
import com.labour.attendance.entity.Labour;
import com.labour.attendance.entity.LabourMonthlyData;
import com.labour.attendance.repository.AttendanceRepository;
import com.labour.attendance.repository.LabourRepository;
import com.labour.attendance.repository.LabourMonthlyDataRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/supervisor")
// @CrossOrigin(origins = "http://localhost:4200")
public class SupervisorController {

    @Autowired
    private LabourRepository labourRepo;

    @Autowired
    private AttendanceRepository attendanceRepo;

    @Autowired
    private LabourMonthlyDataRepository monthlyDataRepo;

    // 2) Save attendance row for a labour and date (existing API)
    @PostMapping("/attendance/{labourId}")
    public Attendance saveAttendance(
            @PathVariable Long labourId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Double shiftsWorked,
            @RequestParam(defaultValue = "false") boolean lossOfPay,
            @RequestParam(defaultValue = "0") Double kharcha1,
            @RequestParam(defaultValue = "0") Double kharcha2,
            @RequestParam(defaultValue = "0") Double kharcha3,
            @RequestParam(defaultValue = "0") Double bhada
    ) {
        Labour labour = labourRepo.findById(labourId).orElseThrow();

        Optional<Attendance> existing = attendanceRepo.findByLabourAndDate(labour, date);
        Attendance att = existing.orElseGet(Attendance::new);

        att.setLabour(labour);
        att.setDate(date);
        att.setShiftsWorked(shiftsWorked);
        att.setLossOfPay(lossOfPay);
        att.setKharcha1(kharcha1);
        att.setKharcha2(kharcha2);
        att.setKharcha3(kharcha3);
        att.setBhada(bhada);

        return attendanceRepo.save(att);
    }

    // 3) Month grid: list all labours with shifts + kharcha per day for given month
    @GetMapping("/month-grid")
    public SupervisorMonthGridDTO getMonthGrid(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(name = "q", required = false) String q
    ) {
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();

        // 1) load all labours
        List<Labour> labours = filterLabours(labourRepo.findAll(), q);

        // 2) load all attendance for that month
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Attendance> attendanceList =
                attendanceRepo.findByDateBetween(start, end);
        List<LabourMonthlyData> monthlyDataList = monthlyDataRepo.findByYearAndMonth(year, month);

        // index attendance by (labourId, day)
        Map<String, Double> shiftsByKey = new HashMap<>();
        Map<Long, LabourMonthlyData> monthlyDataByLabourId = monthlyDataList.stream()
                .collect(Collectors.toMap(
                        m -> m.getLabour().getId(),
                        m -> m,
                        (left, right) -> right.getId() != null && left.getId() != null && right.getId() > left.getId() ? right : left
                ));
        for (Attendance a : attendanceList) {
            Long labourId = a.getLabour().getId();
            int day = a.getDate().getDayOfMonth();
            String key = labourId + ":" + day;
            shiftsByKey.put(key, a.getShiftsWorked());
        }

        // 3) build DTO rows
        List<SupervisorMonthGridRowDTO> rows = labours.stream()
                .map(l -> {
                    Map<Integer, Double> shifts = new HashMap<>();
                    LabourMonthlyData md = monthlyDataByLabourId.get(l.getId());
                    for (int d = 1; d <= daysInMonth; d++) {
                        String key = l.getId() + ":" + d;
                        shifts.put(d, shiftsByKey.getOrDefault(key, 0.0));
                    }

                    return new SupervisorMonthGridRowDTO(
                            l.getId(),
                            l.getCodeNo(),
                            l.getName(),
                            l.getOccupation(),
                            l.getSite(),
                            shifts,
                            md == null ? 0.0 : (md.getKharcha1() == null ? 0.0 : md.getKharcha1()),
                            md == null ? 0.0 : (md.getKharcha2() == null ? 0.0 : md.getKharcha2()),
                            md == null ? 0.0 : (md.getKharcha3() == null ? 0.0 : md.getKharcha3()),
                            md == null ? 0.0 : (md.getBhada() == null ? 0.0 : md.getBhada())
                    );
                })
                .collect(Collectors.toList());

        return new SupervisorMonthGridDTO(year, month, daysInMonth, rows);
    }

    @GetMapping("/month-grid/export")
    public ResponseEntity<byte[]> exportMonthGridPdf(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(name = "q", required = false) String q
    ) throws IOException {
        SupervisorMonthGridDTO grid = getMonthGrid(year, month, q);
        byte[] bytes = buildSupervisorPdf(grid);
        String filename = "supervisor-" + year + "-" + String.format("%02d", month) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    // 4) Update one cell in month grid (auto-save from UI)
    @PostMapping("/month-grid/cell")
    public Attendance updateMonthCell(@RequestBody SupervisorMonthCellUpdateRequest req) {
        LocalDate date = LocalDate.of(req.getYear(), req.getMonth(), req.getDay());
        Labour labour = labourRepo.findById(req.getLabourId()).orElseThrow();

        Optional<Attendance> existing = attendanceRepo.findByLabourAndDate(labour, date);
        Attendance att = existing.orElseGet(Attendance::new);

        att.setLabour(labour);
        att.setDate(date);
        att.setShiftsWorked(req.getShiftsWorked() == null ? 0.0 : req.getShiftsWorked());
        att.setLossOfPay(false);
        if (att.getKharcha1() == null) att.setKharcha1(0.0);
        if (att.getKharcha2() == null) att.setKharcha2(0.0);
        if (att.getKharcha3() == null) att.setKharcha3(0.0);
        if (att.getBhada() == null) att.setBhada(0.0);

        return attendanceRepo.save(att);
    }

    @PostMapping("/month-grid/row-money")
    public Labour updateMonthlyMoney(@RequestBody MonthlyMoneyUpdateRequest req) {
        validateYearMonth(req.getYear(), req.getMonth());
        Labour labour = labourRepo.findById(req.getLabourId()).orElseThrow();
        LabourMonthlyData monthlyData = monthlyDataRepo.findByLabourAndYearAndMonth(labour, req.getYear(), req.getMonth())
                .orElseGet(() -> {
                    LabourMonthlyData md = new LabourMonthlyData();
                    md.setLabour(labour);
                    md.setYear(req.getYear());
                    md.setMonth(req.getMonth());
                    return md;
                });
        monthlyData.setKharcha1(req.getKharcha1() == null ? 0.0 : req.getKharcha1());
        monthlyData.setKharcha2(req.getKharcha2() == null ? 0.0 : req.getKharcha2());
        monthlyData.setKharcha3(req.getKharcha3() == null ? 0.0 : req.getKharcha3());
        monthlyData.setBhada(req.getBhada() == null ? 0.0 : req.getBhada());
        monthlyDataRepo.save(monthlyData);
        return labour;
    }

    private void validateYearMonth(Integer year, Integer month) {
        if (year == null || month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Year/month are required and month must be 1..12");
        }
    }

    private List<Labour> filterLabours(List<Labour> labours, String q) {
        if (q == null || q.isBlank()) {
            return labours;
        }
        String query = q.trim().toLowerCase();
        return labours.stream().filter(l ->
                String.valueOf(l.getId()).contains(query)
                        || (l.getCodeNo() != null && l.getCodeNo().toLowerCase().contains(query))
                        || (l.getName() != null && l.getName().toLowerCase().contains(query))
                        || (l.getOccupation() != null && l.getOccupation().toLowerCase().contains(query))
        ).collect(Collectors.toList());
    }

    private byte[] buildSupervisorPdf(SupervisorMonthGridDTO grid) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setPageSize(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Supervisor Report - " + grid.getYear() + "-" + String.format("%02d", grid.getMonth())));
            document.add(new Paragraph(" "));
            int dayCols = grid.getDaysInMonth();
            PdfPTable table = new PdfPTable(4 + dayCols + 6);
            table.setWidthPercentage(100f);
            addHeader(table, "ID");
            addHeader(table, "Name");
            addHeader(table, "Occupation");
            addHeader(table, "Site");
            for (int d = 1; d <= dayCols; d++) addHeader(table, String.valueOf(d));
            addHeader(table, "Total Hajari");
            addHeader(table, "K1");
            addHeader(table, "K2");
            addHeader(table, "K3");
            addHeader(table, "Total Kharcha");
            addHeader(table, "Bhada");

            for (SupervisorMonthGridRowDTO r : grid.getLabours()) {
                addCell(table, r.getCodeNo());
                addCell(table, r.getName());
                addCell(table, r.getOccupation());
                addCell(table, r.getSite());
                double totalShifts = 0.0;
                for (int d = 1; d <= dayCols; d++) {
                    double s = nvl(r.getShifts().get(d));
                    totalShifts += s;
                    addCell(table, String.valueOf(s));
                }
                double k1 = nvl(r.getKharcha1());
                double k2 = nvl(r.getKharcha2());
                double k3 = nvl(r.getKharcha3());
                double bhada = nvl(r.getBhada());
                double totalKharcha = k1 + k2 + k3;
                addCell(table, String.valueOf(totalShifts));
                addCell(table, String.valueOf(k1));
                addCell(table, String.valueOf(k2));
                addCell(table, String.valueOf(k3));
                addCell(table, String.valueOf(totalKharcha));
                addCell(table, String.valueOf(bhada));
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Failed to generate PDF", e);
        }
    }

    private double nvl(Double value) {
        return value == null ? 0.0 : value;
    }

    private void addHeader(PdfPTable table, String value) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, new Font(Font.HELVETICA, 9, Font.BOLD)));
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String value) {
        table.addCell(new PdfPCell(new Paragraph(value == null ? "" : value, new Font(Font.HELVETICA, 8))));
    }
}
