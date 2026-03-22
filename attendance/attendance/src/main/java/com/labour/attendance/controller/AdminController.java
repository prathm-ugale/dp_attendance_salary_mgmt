package com.labour.attendance.controller;

import com.labour.attendance.dto.AttendanceSummaryDTO;
import com.labour.attendance.dto.AdminMonthCellUpdateRequest;
import com.labour.attendance.dto.AdminMonthGridDTO;
import com.labour.attendance.dto.AdminMonthGridRowDTO;
import com.labour.attendance.dto.LabourUpsertRequest;
import com.labour.attendance.dto.MonthlyMoneyUpdateRequest;
import com.labour.attendance.entity.Attendance;
import com.labour.attendance.entity.Labour;
import com.labour.attendance.repository.AttendanceRepository;
import com.labour.attendance.dto.AdminAttendanceRowDTO;
import com.labour.attendance.repository.LabourRepository;
import com.labour.attendance.repository.RateRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.labour.attendance.dto.PerDayMoneyUpdateRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.transaction.annotation.Transactional;
import com.labour.attendance.entity.LabourMonthlyData;
import com.labour.attendance.repository.LabourMonthlyDataRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AttendanceRepository attendanceRepo;
    private final LabourRepository labourRepo;
    private final LabourMonthlyDataRepository monthlyDataRepo;
    private final RateRepository rateRepo;

    public AdminController(
            AttendanceRepository attendanceRepo,
            LabourRepository labourRepo,
            LabourMonthlyDataRepository monthlyDataRepo,
            RateRepository rateRepo
    ) {
        this.attendanceRepo = attendanceRepo;
        this.labourRepo = labourRepo;
        this.monthlyDataRepo = monthlyDataRepo;
        this.rateRepo = rateRepo;
    }

    @PostMapping("/labours")
    public Map<String, Object> createLabour(@RequestBody LabourUpsertRequest req) {
        validateLabour(req);
        if (labourRepo.existsByCodeNoIgnoreCase(req.getCodeNo().trim())) {
            throw new IllegalArgumentException("Labour ID already exists");
        }
        Labour labour = new Labour();
        labour.setCodeNo(req.getCodeNo().trim());
        labour.setName(req.getName().trim());
        labour.setOccupation(req.getOccupation());
        labour.setAadhar(req.getAadhar());
        labour.setMobile(req.getMobile());
        labour.setSite(req.getSite());
        Labour saved = labourRepo.save(labour);
        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("codeNo", saved.getCodeNo());
        return response;
    }

    @PutMapping("/labours/{labourId}")
    public Map<String, Object> updateLabour(@PathVariable Long labourId, @RequestBody LabourUpsertRequest req) {
        validateLabour(req);
        Labour labour = labourRepo.findById(labourId).orElseThrow();
        if (labourRepo.existsByCodeNoIgnoreCaseAndIdNot(req.getCodeNo().trim(), labourId)) {
            throw new IllegalArgumentException("Labour ID already exists");
        }
        labour.setCodeNo(req.getCodeNo().trim());
        labour.setName(req.getName().trim());
        labour.setOccupation(req.getOccupation());
        labour.setAadhar(req.getAadhar());
        labour.setMobile(req.getMobile());
        labour.setSite(req.getSite());
        Labour saved = labourRepo.save(labour);
        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("codeNo", saved.getCodeNo());
        return response;
    }

    @Transactional
    @DeleteMapping("/labours/{labourId}")
    public void deleteLabour(@PathVariable Long labourId) {
        Labour labour = labourRepo.findById(labourId).orElseThrow();
        attendanceRepo.deleteByLabour(labour);
        monthlyDataRepo.deleteByLabour(labour);
        rateRepo.deleteByLabour(labour);
        labourRepo.delete(labour);
    }

    @GetMapping("/attendance-summary")
    public List<AttendanceSummaryDTO> getSummary(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return attendanceRepo.findSummaryByDate(date);
    }

    @PatchMapping("/labours/{labourId}/per-day-money")
    public void updatePerDayMoney(@PathVariable Long labourId,
                                  @RequestBody PerDayMoneyUpdateRequest request) {
        validateYearMonth(request.getYear(), request.getMonth());
        Labour labour = labourRepo.findById(labourId).orElseThrow();
        LabourMonthlyData monthlyData = getOrCreateMonthlyData(labour, request.getYear(), request.getMonth());
        monthlyData.setPerDayMoney(request.getPerDayMoney() == null ? 0.0 : request.getPerDayMoney());
        monthlyDataRepo.save(monthlyData);
    }

    @GetMapping("/attendance-all")
    public List<AdminAttendanceRowDTO> getAllAttendance() {
        return attendanceRepo.findAllForAdmin();
    }

    @GetMapping("/month-grid")
    public AdminMonthGridDTO getMonthGrid(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(name = "q", required = false) String q
    ) {
        YearMonth ym = YearMonth.of(year, month);
        int daysInMonth = ym.lengthOfMonth();

        List<Labour> labours = filterLabours(labourRepo.findAll(), q);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        List<Attendance> attendanceList = attendanceRepo.findByDateBetween(start, end);
        List<LabourMonthlyData> monthlyDataList = monthlyDataRepo.findByYearAndMonth(year, month);

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
            shiftsByKey.put(key, a.getShiftsWorked() == null ? 0.0 : a.getShiftsWorked());
        }

        List<AdminMonthGridRowDTO> rows = labours.stream()
                .map(l -> {
                    Map<Integer, Double> shifts = new HashMap<>();
                    LabourMonthlyData md = monthlyDataByLabourId.get(l.getId());

                    for (int d = 1; d <= daysInMonth; d++) {
                        String key = l.getId() + ":" + d;
                        shifts.put(d, shiftsByKey.getOrDefault(key, 0.0));
                    }

                    return new AdminMonthGridRowDTO(
                            l.getId(),
                            l.getCodeNo(),
                            l.getName(),
                            l.getOccupation(),
                            l.getAadhar(),
                            l.getMobile(),
                            l.getSite(),
                            md == null ? 0.0 : (md.getPerDayMoney() == null ? 0.0 : md.getPerDayMoney()),
                            shifts,
                            md == null ? 0.0 : (md.getKharcha1() == null ? 0.0 : md.getKharcha1()),
                            md == null ? 0.0 : (md.getKharcha2() == null ? 0.0 : md.getKharcha2()),
                            md == null ? 0.0 : (md.getKharcha3() == null ? 0.0 : md.getKharcha3()),
                            md == null ? 0.0 : (md.getBhada() == null ? 0.0 : md.getBhada())
                    );
                })
                .collect(Collectors.toList());

        return new AdminMonthGridDTO(year, month, daysInMonth, rows);
    }

    @GetMapping("/month-grid/export")
    public ResponseEntity<byte[]> exportMonthGrid(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(name = "q", required = false) String q
    ) throws IOException {
        AdminMonthGridDTO grid = getMonthGrid(year, month, q);
        byte[] bytes = buildAdminExcel(grid);
        String filename = "admin-" + year + "-" + String.format("%02d", month) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/month-grid/export-pdf")
    public ResponseEntity<byte[]> exportMonthGridPdf(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(name = "q", required = false) String q
    ) throws IOException {
        AdminMonthGridDTO grid = getMonthGrid(year, month, q);
        byte[] bytes = buildAdminPdf(grid);
        String filename = "admin-" + year + "-" + String.format("%02d", month) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    @PostMapping("/month-grid/cell")
    public Attendance updateMonthCell(@RequestBody AdminMonthCellUpdateRequest req) {
        LocalDate date = LocalDate.of(req.getYear(), req.getMonth(), req.getDay());
        Labour labour = labourRepo.findById(req.getLabourId()).orElseThrow();

        Optional<Attendance> existing = attendanceRepo.findByLabourAndDate(labour, date);
        Attendance att = existing.orElseGet(Attendance::new);

        att.setLabour(labour);
        att.setDate(date);
        if (att.getShiftsWorked() == null) {
            att.setShiftsWorked(0.0);
        }
        if (att.getLossOfPay() == null) {
            att.setLossOfPay(false);
        }
        att.setKharcha1(req.getKharcha1() == null ? 0.0 : req.getKharcha1());
        att.setKharcha2(req.getKharcha2() == null ? 0.0 : req.getKharcha2());
        att.setKharcha3(req.getKharcha3() == null ? 0.0 : req.getKharcha3());
        att.setBhada(req.getBhada() == null ? 0.0 : req.getBhada());

        return attendanceRepo.save(att);
    }

    @PostMapping("/month-grid/row-money")
    public Labour updateMonthlyMoney(@RequestBody MonthlyMoneyUpdateRequest req) {
        validateYearMonth(req.getYear(), req.getMonth());
        Labour labour = labourRepo.findById(req.getLabourId()).orElseThrow();
        LabourMonthlyData monthlyData = getOrCreateMonthlyData(labour, req.getYear(), req.getMonth());
        monthlyData.setKharcha1(req.getKharcha1() == null ? 0.0 : req.getKharcha1());
        monthlyData.setKharcha2(req.getKharcha2() == null ? 0.0 : req.getKharcha2());
        monthlyData.setKharcha3(req.getKharcha3() == null ? 0.0 : req.getKharcha3());
        monthlyData.setBhada(req.getBhada() == null ? 0.0 : req.getBhada());
        monthlyDataRepo.save(monthlyData);
        return labour;
    }

    private LabourMonthlyData getOrCreateMonthlyData(Labour labour, Integer year, Integer month) {
        return monthlyDataRepo.findByLabourAndYearAndMonth(labour, year, month)
                .orElseGet(() -> {
                    LabourMonthlyData md = new LabourMonthlyData();
                    md.setLabour(labour);
                    md.setYear(year);
                    md.setMonth(month);
                    return md;
                });
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

    private byte[] buildAdminExcel(AdminMonthGridDTO grid) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Admin " + grid.getYear() + "-" + grid.getMonth());
            int col = 0;
            Row header = sheet.createRow(0);
            header.createCell(col++).setCellValue("ID");
            header.createCell(col++).setCellValue("Name");
            header.createCell(col++).setCellValue("Occupation");
            header.createCell(col++).setCellValue("Aadhar");
            header.createCell(col++).setCellValue("Mobile");
            header.createCell(col++).setCellValue("Site");
            header.createCell(col++).setCellValue("Per day money");
            for (int d = 1; d <= grid.getDaysInMonth(); d++) {
                header.createCell(col++).setCellValue(String.valueOf(d));
            }
            header.createCell(col++).setCellValue("Total hajari");
            header.createCell(col++).setCellValue("Total pay");
            header.createCell(col++).setCellValue("K1");
            header.createCell(col++).setCellValue("K2");
            header.createCell(col++).setCellValue("K3");
            header.createCell(col++).setCellValue("Total kharcha");
            header.createCell(col++).setCellValue("Bhada");
            header.createCell(col++).setCellValue("Net pay");

            int rowNum = 1;
            for (AdminMonthGridRowDTO r : grid.getLabours()) {
                Row row = sheet.createRow(rowNum++);
                int c = 0;
                row.createCell(c++).setCellValue(r.getCodeNo() == null ? "" : r.getCodeNo());
                row.createCell(c++).setCellValue(r.getName() == null ? "" : r.getName());
                row.createCell(c++).setCellValue(r.getOccupation() == null ? "" : r.getOccupation());
                row.createCell(c++).setCellValue(r.getAadhar() == null ? "" : r.getAadhar());
                row.createCell(c++).setCellValue(r.getMobile() == null ? "" : r.getMobile());
                row.createCell(c++).setCellValue(r.getSite() == null ? "" : r.getSite());
                double perDay = nvl(r.getPerDayMoney());
                row.createCell(c++).setCellValue(perDay);
                double totalShifts = 0.0;
                for (int d = 1; d <= grid.getDaysInMonth(); d++) {
                    double shifts = nvl(r.getShifts().get(d));
                    totalShifts += shifts;
                    row.createCell(c++).setCellValue(shifts);
                }
                double k1 = nvl(r.getKharcha1());
                double k2 = nvl(r.getKharcha2());
                double k3 = nvl(r.getKharcha3());
                double bhada = nvl(r.getBhada());
                double totalKharcha = k1 + k2 + k3;
                double pay = totalShifts * perDay;
                double net = pay - totalKharcha + bhada;
                row.createCell(c++).setCellValue(totalShifts);
                row.createCell(c++).setCellValue(pay);
                row.createCell(c++).setCellValue(k1);
                row.createCell(c++).setCellValue(k2);
                row.createCell(c++).setCellValue(k3);
                row.createCell(c++).setCellValue(totalKharcha);
                row.createCell(c++).setCellValue(bhada);
                row.createCell(c++).setCellValue(net);
            }

            int totalCols = 7 + grid.getDaysInMonth() + 8;
            for (int i = 0; i < totalCols; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private byte[] buildAdminPdf(AdminMonthGridDTO grid) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            document.setPageSize(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Admin Report - " + grid.getYear() + "-" + String.format("%02d", grid.getMonth())));
            document.add(new Paragraph(" "));
            int dayCols = grid.getDaysInMonth();
            PdfPTable table = new PdfPTable(7 + dayCols + 8);
            table.setWidthPercentage(100f);
            addHeader(table, "ID");
            addHeader(table, "Name");
            addHeader(table, "Occupation");
            addHeader(table, "Aadhar");
            addHeader(table, "Mobile");
            addHeader(table, "Site");
            addHeader(table, "Per day");
            for (int d = 1; d <= dayCols; d++) addHeader(table, String.valueOf(d));
            addHeader(table, "Total Hajari");
            addHeader(table, "Total Pay");
            addHeader(table, "K1");
            addHeader(table, "K2");
            addHeader(table, "K3");
            addHeader(table, "Total Kharcha");
            addHeader(table, "Bhada");
            addHeader(table, "Net Pay");

            for (AdminMonthGridRowDTO r : grid.getLabours()) {
                addCell(table, r.getCodeNo());
                addCell(table, r.getName());
                addCell(table, r.getOccupation());
                addCell(table, r.getAadhar());
                addCell(table, r.getMobile());
                addCell(table, r.getSite());
                double perDay = nvl(r.getPerDayMoney());
                addCell(table, String.valueOf(perDay));
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
                double totalPay = totalShifts * perDay;
                double netPay = totalPay - totalKharcha + bhada;
                addCell(table, String.valueOf(totalShifts));
                addCell(table, String.valueOf(totalPay));
                addCell(table, String.valueOf(k1));
                addCell(table, String.valueOf(k2));
                addCell(table, String.valueOf(k3));
                addCell(table, String.valueOf(totalKharcha));
                addCell(table, String.valueOf(bhada));
                addCell(table, String.valueOf(netPay));
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

    private void validateLabour(LabourUpsertRequest req) {
        if (req.getCodeNo() == null || req.getCodeNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Labour ID is required");
        }
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Labour name is required");
        }
        if (req.getMobile() != null && !req.getMobile().isBlank() && !req.getMobile().matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile must be exactly 10 digits");
        }
        if (req.getAadhar() != null && !req.getAadhar().isBlank() && !req.getAadhar().matches("\\d{12}")) {
            throw new IllegalArgumentException("Aadhar must be exactly 12 digits");
        }
    }

    private void addHeader(PdfPTable table, String value) {
        PdfPCell cell = new PdfPCell(new Paragraph(value, new Font(Font.HELVETICA, 9, Font.BOLD)));
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String value) {
        table.addCell(new PdfPCell(new Paragraph(value == null ? "" : value, new Font(Font.HELVETICA, 8))));
    }
}
