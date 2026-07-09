import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UpdatePriceRequest } from '../models/common.model';
import { Input, InputCreateRequest } from '../models/input.model';

@Injectable({ providedIn: 'root' })
export class InputService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/inputs`;

  getAll(): Observable<Input[]> {
    return this.http.get<Input[]>(this.base);
  }

  getById(id: number): Observable<Input> {
    return this.http.get<Input>(`${this.base}/${id}`);
  }

  create(request: InputCreateRequest): Observable<Input> {
    return this.http.post<Input>(this.base, request);
  }

  updatePrice(id: number, price: number): Observable<Input> {
    const request: UpdatePriceRequest = { price };
    return this.http.put<Input>(`${this.base}/${id}/price`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
