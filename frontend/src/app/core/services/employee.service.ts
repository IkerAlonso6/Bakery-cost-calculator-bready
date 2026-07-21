import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Employee, EmployeeCreateRequest } from '../models/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/employees`;

  /** Empleados del período dado (por defecto, el mes actual). */
  getAll(period?: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.base, { params: periodParams(period) });
  }

  /** L: total de sueldos del período. El backend devuelve un número plano. */
  getTotal(period?: string): Observable<number> {
    return this.http
      .get(`${this.base}/total`, { params: periodParams(period), responseType: 'text' })
      .pipe(map((text) => parseFloat(text)));
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.base}/${id}`);
  }

  create(request: EmployeeCreateRequest): Observable<Employee> {
    return this.http.post<Employee>(this.base, request);
  }

  update(id: number, request: EmployeeCreateRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.base}/${id}`, request);
  }

  /** Copia los empleados de fromPeriod hacia toPeriod (omite nombres ya existentes en toPeriod). */
  duplicatePreviousPeriod(fromPeriod: string, toPeriod: string): Observable<Employee[]> {
    return this.http.post<Employee[]>(`${this.base}/duplicate-previous-period`, { fromPeriod, toPeriod });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

function periodParams(period?: string): HttpParams | undefined {
  return period ? new HttpParams().set('period', period) : undefined;
}
