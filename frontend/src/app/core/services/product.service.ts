import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UpdatePriceRequest } from '../models/common.model';
import { Product, ProductCosting, ProductCreateRequest, UpdateMarginRequest } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/products`;

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.base);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.base}/${id}`);
  }

  create(request: ProductCreateRequest): Observable<Product> {
    return this.http.post<Product>(this.base, request);
  }

  updatePrice(id: number, price: number): Observable<Product> {
    const request: UpdatePriceRequest = { price };
    return this.http.put<Product>(`${this.base}/${id}/price`, request);
  }

  updateMargin(id: number, targetMargin: number | null): Observable<Product> {
    const request: UpdateMarginRequest = { targetMargin };
    return this.http.put<Product>(`${this.base}/${id}/margin`, request);
  }

  /** Endpoint central: desglose de costos, precio sugerido y margen real. 404/409 posibles. */
  getPricing(id: number): Observable<ProductCosting> {
    return this.http.get<ProductCosting>(`${this.base}/${id}/pricing`);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
