import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AdminMonthGridRow, LabourService } from '../services/labour';

@Component({
  selector: 'app-admin-labour-summary',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-labour-summary.html',
  styleUrl: './admin-labour-summary.css'
})
export class AdminLabourSummaryComponent implements OnInit {
  year: number;
  month: number;
  daysInMonth = 0;
  days: number[] = [];
  rows: AdminMonthGridRow[] = [];
  filteredRows: AdminMonthGridRow[] = [];
  searchText = '';
  showTable = false;

  newLabour = { codeNo: '', name: '', occupation: '', aadhar: '', mobile: '', site: '' };
  editingLabourId: number | null = null;
  addingLabour = false;

  constructor(
    private labourService: LabourService,
    private router: Router
  ) {
    const now = new Date();
    this.year = now.getFullYear();
    this.month = now.getMonth() + 1;
  }

  async ngOnInit(): Promise<void> {
    const ok = await this.labourService.promptRoleCredentials('admin');
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
    this.labourService.getAdminMonthGrid(this.year, this.month).subscribe({
      next: grid => {
        this.daysInMonth = grid.daysInMonth;
        this.days = Array.from({ length: this.daysInMonth }, (_, i) => i + 1);
        this.rows = grid.labours;
        this.applyFilter();
      },
      error: err => {
        console.error('Error loading admin month grid', err);
        alert(`Error loading admin month grid (${err?.status || 'NA'}): ${err?.error?.message || err?.message || 'Unknown error'}`);
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

  addLabour(): void {
    if (this.addingLabour) {
      return;
    }

    const payload = {
      codeNo: (this.newLabour.codeNo || '').trim(),
      name: (this.newLabour.name || '').trim(),
      occupation: (this.newLabour.occupation || '').trim(),
      aadhar: (this.newLabour.aadhar || '').trim(),
      mobile: (this.newLabour.mobile || '').trim(),
      site: (this.newLabour.site || '').trim()
    };

    const code = payload.codeNo.toLowerCase();
    if (!code) {
      alert('Labour ID is required');
      return;
    }
    if (!payload.name) {
      alert('Labour name is required');
      return;
    }
    if (this.rows.some(r => (r.codeNo || '').trim().toLowerCase() === code)) {
      alert('Labour ID must be unique');
      return;
    }
    if (!this.validMobile(payload.mobile) || !this.validAadhar(payload.aadhar)) {
      alert('Mobile must be 10 digits and Aadhar must be 12 digits');
      return;
    }
    this.addingLabour = true;
    this.labourService.createAdminLabour(payload).pipe(
      finalize(() => {
        this.addingLabour = false;
      })
    ).subscribe({
      next: () => {
        this.newLabour = { codeNo: '', name: '', occupation: '', aadhar: '', mobile: '', site: '' };
        this.showTable = true;
        this.loadMonth();
      },
      error: err => {
        console.error('Error adding labour', err);
        alert(err?.error?.message || 'Error adding labour');
      }
    });
  }

  startEdit(row: AdminMonthGridRow): void {
    this.editingLabourId = row.labourId;
  }

  saveEdit(row: AdminMonthGridRow): void {
    const code = (row.codeNo || '').trim().toLowerCase();
    if (!code) {
      alert('Labour ID is required');
      return;
    }
    if (this.rows.some(r => r.labourId !== row.labourId && (r.codeNo || '').trim().toLowerCase() === code)) {
      alert('Labour ID must be unique');
      return;
    }
    if (!this.validMobile(row.mobile) || !this.validAadhar(row.aadhar)) {
      alert('Mobile must be 10 digits and Aadhar must be 12 digits');
      return;
    }
    this.labourService.updateAdminLabour(row.labourId, {
      codeNo: row.codeNo,
      name: row.name,
      occupation: row.occupation,
      aadhar: row.aadhar,
      mobile: row.mobile,
      site: row.site
    }).subscribe({
      next: () => {
        this.editingLabourId = null;
        this.applyFilter();
      },
      error: err => {
        console.error('Error updating labour', err);
        alert(err?.error?.message || 'Error updating labour');
      }
    });
  }

  deleteLabour(row: AdminMonthGridRow): void {
    if (!confirm(`Delete labour ${row.codeNo} - ${row.name}?`)) return;
    this.labourService.deleteAdminLabour(row.labourId).subscribe({
      next: () => this.loadMonth(),
      error: err => {
        console.error('Error deleting labour', err);
        alert('Error deleting labour');
      }
    });
  }

  saveRate(row: AdminMonthGridRow): void {
    this.labourService.updatePerDayMoney(row.labourId, this.year, this.month, Number(row.perDayMoney) || 0).subscribe({
      error: err => {
        console.error('Error saving per-day money', err);
        alert('Error saving per-day money');
      }
    });
  }

  saveMonthlyMoney(row: AdminMonthGridRow): void {
    this.labourService.updateAdminMonthlyMoney(
      row.labourId, this.year, this.month,
      Number(row.kharcha1) || 0,
      Number(row.kharcha2) || 0,
      Number(row.kharcha3) || 0,
      Number(row.bhada) || 0
    ).subscribe({
      error: err => {
        console.error('Error saving monthly money', err);
        alert('Error saving monthly values');
      }
    });
  }

  totalHajari(row: AdminMonthGridRow): number {
    return Object.values(row.shifts || {}).reduce((acc, v) => acc + (Number(v) || 0), 0);
  }

  totalPay(row: AdminMonthGridRow): number {
    return this.totalHajari(row) * (Number(row.perDayMoney) || 0);
  }

  totalKharcha(row: AdminMonthGridRow): number {
    return (Number(row.kharcha1) || 0) + (Number(row.kharcha2) || 0) + (Number(row.kharcha3) || 0);
  }

  netPay(row: AdminMonthGridRow): number {
    return this.totalPay(row) - this.totalKharcha(row) + (Number(row.bhada) || 0);
  }

  downloadExcel(): void {
    this.labourService.exportAdminMonthGrid(this.year, this.month, this.searchText).subscribe({
      next: blob => this.downloadBlob(blob, `admin-${this.year}-${String(this.month).padStart(2, '0')}.xlsx`),
      error: () => alert('Error downloading excel')
    });
  }

  downloadPdf(): void {
    this.labourService.exportAdminMonthGridPdf(this.year, this.month, this.searchText).subscribe({
      next: blob => this.downloadBlob(blob, `admin-${this.year}-${String(this.month).padStart(2, '0')}.pdf`),
      error: () => alert('Error downloading pdf')
    });
  }

  private downloadBlob(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  private validMobile(m: string): boolean {
    return !m || /^\d{10}$/.test(m);
  }

  private validAadhar(a: string): boolean {
    return !a || /^\d{12}$/.test(a);
  }
}
