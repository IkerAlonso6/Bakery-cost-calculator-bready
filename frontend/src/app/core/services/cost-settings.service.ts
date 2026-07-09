import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CostSettings } from '../models/cost-settings.model';

@Injectable({ providedIn: 'root' })
export class CostSettingsService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/cost-settings`;

  /** Devuelve 409 si el negocio todavía no configuró el costeo (recurso singleton). */
  get(): Observable<CostSettings> {
    return this.http.get<CostSettings>(this.base);
  }

  update(settings: CostSettings): Observable<CostSettings> {
    return this.http.put<CostSettings>(this.base, settings);
  }
}
