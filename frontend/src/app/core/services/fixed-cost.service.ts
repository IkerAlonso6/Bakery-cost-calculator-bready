import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FixedCost, FixedCostCreateRequest } from '../models/fixed-cost.model';

@Injectable({ providedIn: 'root' })
export class FixedCostService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/fixed-costs`;

  /** Costos fijos del período dado (por defecto, el mes actual). */
  getAll(period?: string): Observable<FixedCost[]> {
    return this.http.get<FixedCost[]>(this.base, { params: periodParams(period) });
  }

  /** F: total de costos fijos del período. El backend devuelve un número plano. */
  getTotal(period?: string): Observable<number> {
    return this.http
      .get(`${this.base}/total`, { params: periodParams(period), responseType: 'text' })
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

  /** Copia los costos fijos de fromPeriod hacia toPeriod (omite nombres ya existentes en toPeriod). */
  duplicatePreviousPeriod(fromPeriod: string, toPeriod: string): Observable<FixedCost[]> {
    return this.http.post<FixedCost[]>(`${this.base}/duplicate-previous-period`, { fromPeriod, toPeriod });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}

function periodParams(period?: string): HttpParams | undefined {
  return period ? new HttpParams().set('period', period) : undefined;
}
