import { UnitOfMeasurement } from './unit-of-measurement';

export interface Ingredient {
  id: number | null;
  inputId: number;
  inputName: string; // solo lectura, la completa el backend
  quantity: number;
  cost: number; // solo lectura, la completa el backend
}

export interface Recipe {
  id: number | null;
  name: string;
  yieldQuantity: number;
  yieldUnit: UnitOfMeasurement;
  ingredients: Ingredient[];
}

export interface RecipeIngredientInput {
  inputId: number;
  quantity: number;
}

export interface RecipeCreateRequest {
  name: string;
  yieldQuantity: number;
  yieldUnit: UnitOfMeasurement;
  ingredients: RecipeIngredientInput[];
}

export interface AddIngredientRequest {
  inputId: number;
  quantity: number;
}
