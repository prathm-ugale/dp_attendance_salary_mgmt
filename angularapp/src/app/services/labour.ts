import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

type Role = 'admin' | 'supervisor';
type LabourPayload = { codeNo: string; name: string; occupation?: string; aadhar?: string; mobile?: string; site?: string };

@Injectable({
  providedIn: 'root'
})
export class LabourService {

  private adminAuthHeader: string | null = null;
  private supervisorAuthHeader: string | null = null;

  constructor(private http: HttpClient) {}

  promptRoleCredentials(role: Role): Promise<boolean> {
    return new Promise(resolve => {
      const overlay = document.createElement('div');
      overlay.style.position = 'fixed';
      overlay.style.inset = '0';
      overlay.style.background = 'rgba(0,0,0,0.35)';
      overlay.style.display = 'flex';
      overlay.style.alignItems = 'center';
      overlay.style.justifyContent = 'center';
      overlay.style.zIndex = '9999';

      const box = document.createElement('div');
      box.style.background = '#fff';
      box.style.padding = '16px';
      box.style.borderRadius = '8px';
      box.style.minWidth = '320px';
      box.style.boxShadow = '0 8px 24px rgba(0,0,0,0.2)';

      const title = document.createElement('h3');
      title.textContent = `${role.toUpperCase()} Login`;
      title.style.margin = '0 0 10px 0';

      const userLabel = document.createElement('label');
      userLabel.textContent = 'Username';
      userLabel.style.display = 'block';
      const userInput = document.createElement('input');
      userInput.type = 'text';
      userInput.style.width = '100%';
      userInput.style.margin = '4px 0 10px 0';

      const passLabel = document.createElement('label');
      passLabel.textContent = 'Password';
      passLabel.style.display = 'block';
      const passInput = document.createElement('input');
      passInput.type = 'password';
      passInput.style.width = '100%';
      passInput.style.margin = '4px 0 12px 0';

      const actions = document.createElement('div');
      actions.style.display = 'flex';
      actions.style.justifyContent = 'flex-end';
      actions.style.gap = '8px';

      const cancelBtn = document.createElement('button');
      cancelBtn.type = 'button';
      cancelBtn.textContent = 'Cancel';

      const loginBtn = document.createElement('button');
      loginBtn.type = 'button';
      loginBtn.textContent = 'Login';

      const cleanup = () => {
        overlay.remove();
      };

      const handleLogin = () => {
        const username = userInput.value.trim();
        const password = passInput.value;
        if (!username || !password) {
          alert('Username and password are required');
          return;
        }
        const auth = 'Basic ' + btoa(`${username}:${password}`);
        if (role === 'admin') {
          this.adminAuthHeader = auth;
        } else {
          this.supervisorAuthHeader = auth;
        }
        cleanup();
        resolve(true);
      };

      cancelBtn.onclick = () => {
        cleanup();
        resolve(false);
      };
      loginBtn.onclick = handleLogin;
      passInput.onkeydown = e => {
        if (e.key === 'Enter') {
          handleLogin();
        }
      };

      actions.appendChild(cancelBtn);
      actions.appendChild(loginBtn);
      box.appendChild(title);
      box.appendChild(userLabel);
      box.appendChild(userInput);
      box.appendChild(passLabel);
      box.appendChild(passInput);
      box.appendChild(actions);
      overlay.appendChild(box);
      document.body.appendChild(overlay);
      userInput.focus();
    });
  }

  clearRoleCredentials(role: Role): void {
    if (role === 'admin') {
      this.adminAuthHeader = null;
    } else {
      this.supervisorAuthHeader = null;
    }
  }

  private authHeadersForAdmin(): HttpHeaders {
    if (!this.adminAuthHeader) {
      throw new Error('Admin credentials not set');
    }
    return new HttpHeaders({
      Authorization: this.adminAuthHeader
    });
  }

  private authHeadersForSupervisor(): HttpHeaders {
    if (!this.supervisorAuthHeader) {
      throw new Error('Supervisor credentials not set');
    }
    return new HttpHeaders({
      Authorization: this.supervisorAuthHeader
    });
  }

  updatePerDayMoney(labourId: number, year: number, month: number, perDayMoney: number): Observable<string> {
    return this.http.patch(
      `http://localhost:8080/api/admin/labours/${labourId}/per-day-money`,
      { year, month, perDayMoney },
      {
        headers: this.authHeadersForAdmin(),
        responseType: 'text'
      }
    );
  }

  getSupervisorMonthGrid(year: number, month: number): Observable<SupervisorMonthGrid> {
    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());

    return this.http.get<SupervisorMonthGrid>(
      'http://localhost:8080/api/supervisor/month-grid',
      {
        headers: this.authHeadersForSupervisor(),
        params
      }
    );
  }

  exportSupervisorMonthGridPdf(year: number, month: number, q: string): Observable<Blob> {
    let params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());
    if (q && q.trim() !== '') {
      params = params.set('q', q.trim());
    }
    return this.http.get(
      'http://localhost:8080/api/supervisor/month-grid/export',
      {
        headers: this.authHeadersForSupervisor(),
        params,
        responseType: 'blob'
      }
    );
  }

  updateSupervisorCell(
    labourId: number,
    year: number,
    month: number,
    day: number,
    shiftsWorked: number
  ): Observable<any> {
    const body = {
      labourId,
      year,
      month,
      day,
      shiftsWorked
    };
    return this.http.post<any>(
      'http://localhost:8080/api/supervisor/month-grid/cell',
      body,
      {
        headers: this.authHeadersForSupervisor()
      }
    );
  }

  getAdminMonthGrid(year: number, month: number): Observable<AdminMonthGrid> {
    const params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());

    return this.http.get<AdminMonthGrid>(
      'http://localhost:8080/api/admin/month-grid',
      {
        headers: this.authHeadersForAdmin(),
        params
      }
    );
  }

  exportAdminMonthGrid(year: number, month: number, q: string): Observable<Blob> {
    let params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());
    if (q && q.trim() !== '') {
      params = params.set('q', q.trim());
    }
    return this.http.get(
      'http://localhost:8080/api/admin/month-grid/export',
      {
        headers: this.authHeadersForAdmin(),
        params,
        responseType: 'blob'
      }
    );
  }

  exportAdminMonthGridPdf(year: number, month: number, q: string): Observable<Blob> {
    let params = new HttpParams()
      .set('year', year.toString())
      .set('month', month.toString());
    if (q && q.trim() !== '') {
      params = params.set('q', q.trim());
    }
    return this.http.get(
      'http://localhost:8080/api/admin/month-grid/export-pdf',
      {
        headers: this.authHeadersForAdmin(),
        params,
        responseType: 'blob'
      }
    );
  }

  createAdminLabour(payload: LabourPayload): Observable<any> {
    return this.http.post<any>('http://localhost:8080/api/admin/labours', payload, {
      headers: this.authHeadersForAdmin()
    });
  }

  updateAdminLabour(labourId: number, payload: LabourPayload): Observable<any> {
    return this.http.put<any>(`http://localhost:8080/api/admin/labours/${labourId}`, payload, {
      headers: this.authHeadersForAdmin()
    });
  }

  deleteAdminLabour(labourId: number): Observable<any> {
    return this.http.delete<any>(`http://localhost:8080/api/admin/labours/${labourId}`, {
      headers: this.authHeadersForAdmin()
    });
  }

  updateAdminMonthlyMoney(
    labourId: number,
    year: number,
    month: number,
    kharcha1: number,
    kharcha2: number,
    kharcha3: number,
    bhada: number
  ): Observable<any> {
    return this.http.post<any>(
      'http://localhost:8080/api/admin/month-grid/row-money',
      { labourId, year, month, kharcha1, kharcha2, kharcha3, bhada },
      {
        headers: this.authHeadersForAdmin()
      }
    );
  }

  updateSupervisorMonthlyMoney(
    labourId: number,
    year: number,
    month: number,
    kharcha1: number,
    kharcha2: number,
    kharcha3: number,
    bhada: number
  ): Observable<any> {
    return this.http.post<any>(
      'http://localhost:8080/api/supervisor/month-grid/row-money',
      { labourId, year, month, kharcha1, kharcha2, kharcha3, bhada },
      {
        headers: this.authHeadersForSupervisor()
      }
    );
  }

}

export interface SupervisorMonthGridRow {
  labourId: number;
  codeNo: string;
  name: string;
  occupation: string;
  site: string;
  shifts: { [day: number]: number };
  kharcha1: number;
  kharcha2: number;
  kharcha3: number;
  bhada: number;
}

export interface SupervisorMonthGrid {
  year: number;
  month: number;       // 1..12
  daysInMonth: number; // 28-31
  labours: SupervisorMonthGridRow[];
}

export interface AdminMonthGridRow {
  labourId: number;
  codeNo: string;
  name: string;
  occupation: string;
  aadhar: string;
  mobile: string;
  site: string;
  perDayMoney: number;
  shifts: { [day: number]: number };
  kharcha1: number;
  kharcha2: number;
  kharcha3: number;
  bhada: number;
}

export interface AdminMonthGrid {
  year: number;
  month: number;
  daysInMonth: number;
  labours: AdminMonthGridRow[];
}
