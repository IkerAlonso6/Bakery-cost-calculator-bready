import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Employee, EmployeeCreateRequest } from '../models/employee.model';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/employees`;

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.base);
  }

  /** L: total de sueldos del mes. El backend devuelve un número plano. */
  getTotal(): Observable<number> {
    return this.http
      .get(`${this.base}/total`, { responseType: 'text' })
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

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
