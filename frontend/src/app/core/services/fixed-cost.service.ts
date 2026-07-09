import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FixedCost, FixedCostCreateRequest } from '../models/fixed-cost.model';

@Injectable({ providedIn: 'root' })
export class FixedCostService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/fixed-costs`;

  getAll(): Observable<FixedCost[]> {
    return this.http.get<FixedCost[]>(this.base);
  }

  /** F: total de costos fijos del mes. El backend devuelve un número plano. */
  getTotal(): Observable<number> {
    return this.http
      .get(`${this.base}/total`, { responseType: 'text' })
      .pipe(map((text) => parseFloat(text)));
  }

  getById(id: number): Observable<FixedCost> {
    return this.http.get<FixedCost>(`${this.base}/${id}`);
  }

  create(request: FixedCostCreateRequest): Observable<FixedCost> {
    return this.http.post<FixedCost>(this.base, request);
  }

  update(id: number, request: FixedCostCreateRequest): Observable<FixedCost> {
    return this.http.put<FixedCost>(`${this.base}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
