import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AddIngredientRequest, Recipe, RecipeCreateRequest } from '../models/recipe.model';

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiUrl}/recipes`;

  getAll(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.base);
  }

  getById(id: number): Observable<Recipe> {
    return this.http.get<Recipe>(`${this.base}/${id}`);
  }

  create(request: RecipeCreateRequest): Observable<Recipe> {
    return this.http.post<Recipe>(this.base, request);
  }

  addIngredient(recipeId: number, request: AddIngredientRequest): Observable<Recipe> {
    return this.http.post<Recipe>(`${this.base}/${recipeId}/ingredients`, request);
  }

  removeIngredient(recipeId: number, ingredientId: number): Observable<Recipe> {
    return this.http.delete<Recipe>(`${this.base}/${recipeId}/ingredients/${ingredientId}`);
  }

  /** Costo de materiales por unidad de rendimiento. El backend devuelve un número plano. */
  getCost(id: number): Observable<number> {
    return this.http
      .get(`${this.base}/${id}/cost`, { responseType: 'text' })
      .pipe(map((text) => parseFloat(text)));
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
