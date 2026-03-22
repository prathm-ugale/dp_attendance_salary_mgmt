import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LabourService, SupervisorMonthGrid, SupervisorMonthGridRow } from '../services/labour';

@Component({
  selector: 'app-supervisor-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supervisor-attendance.html',
  styleUrls: ['./supervisor-attendance.css']
})
export class SupervisorAttendanceComponent implements OnInit {
  year: number;
  month: number;
  daysInMonth = 0;
  days: number[] = [];
  grid: SupervisorMonthGrid | null = null;
  rows: SupervisorMonthGridRow[] = [];
  filteredRows: SupervisorMonthGridRow[] = [];
  searchText = '';
  shiftOptions = [0, 0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5];
  showTable = false;

  constructor(
    private labourService: LabourService,
    private router: Router
  ) {
    const now = new Date();
    this.year = now.getFullYear();
    this.month = now.getMonth() + 1;
  }

  async ngOnInit(): Promise<void> {
    const ok = await this.labourService.promptRoleCredentials('supervisor');
    if (!ok) {
      this.router.navigate(['/']);
      return;
    }
    this.showData();
  }

  showData(): void {
    this.showTable = true;
    this.loadMonth();
  }

  loadMonth(): void {
    this.labourService.getSupervisorMonthGrid(this.year, this.month).subscribe({
      next: grid => {
        this.grid = grid;
        this.daysInMonth = grid.daysInMonth;
        this.days = Array.from({ length: this.daysInMonth }, (_, i) => i + 1);
        this.rows = grid.labours;
        this.applyFilter();
      },
      error: err => {
        console.error('Error loading month grid', err);
        alert(`Error loading month grid (${err?.status || 'NA'}): ${err?.error?.message || err?.message || 'Unknown error'}`);
      }
    });
  }

  onMonthChange(): void {
    if (this.showTable) this.loadMonth();
  }

  applyFilter(): void {
    const q = this.searchText.trim().toLowerCase();
    if (!q) {
      this.filteredRows = this.rows;
      return;
    }
    this.filteredRows = this.rows.filter(r =>
      String(r.labourId || '').includes(q) ||
      (r.codeNo || '').toLowerCase().includes(q) ||
      (r.name || '').toLowerCase().includes(q) ||
      (r.occupation || '').toLowerCase().includes(q)
    );
  }

  onShiftChange(row: SupervisorMonthGridRow, day: number, value: string): void {
    const shifts = Number(value) || 0;
    row.shifts[day] = shifts;
    this.labourService.updateSupervisorCell(row.labourId, this.year, this.month, day, shifts).subscribe({
      error: err => {
        console.error('Error saving shift', err);
        alert('Error saving shift');
      }
    });
  }

  saveMonthlyMoney(row: SupervisorMonthGridRow): void {
    this.labourService.updateSupervisorMonthlyMoney(
      row.labourId, this.year, this.month,
      Number(row.kharcha1) || 0,
      Number(row.kharcha2) || 0,
      Number(row.kharcha3) || 0,
      Number(row.bhada) || 0
    ).subscribe({
      error: err => {
        console.error('Error saving monthly values', err);
        alert('Error saving monthly values');
      }
    });
  }

  totalHajari(row: SupervisorMonthGridRow): number {
    return Object.values(row.shifts || {}).reduce((acc, v) => acc + (Number(v) || 0), 0);
  }

  totalKharcha(row: SupervisorMonthGridRow): number {
    return (Number(row.kharcha1) || 0) + (Number(row.kharcha2) || 0) + (Number(row.kharcha3) || 0);
  }

  downloadPdf(): void {
    this.labourService.exportSupervisorMonthGridPdf(this.year, this.month, this.searchText).subscribe({
      next: blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `supervisor-${this.year}-${String(this.month).padStart(2, '0')}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => alert('Error downloading pdf')
    });
  }
}
