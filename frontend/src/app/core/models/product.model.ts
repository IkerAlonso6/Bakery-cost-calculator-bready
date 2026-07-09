export interface Product {
  id: number | null;
  name: string;
  recipeId: number;
  recipeName: string; // solo lectura, la completa el backend
  price: number | null;
  targetMargin: number | null; // [0,1); null = usa el margen global del negocio
}

export type ProductCreateRequest = Omit<Product, 'id' | 'recipeName'>;

export interface UpdateMarginRequest {
  targetMargin: number | null;
}

export interface ProductCosting {
  productId: number;
  productName: string;
  materialCost: number;
  laborCost: number;
  fixedCost: number;
  totalCost: number;
  appliedMargin: number;
  suggestedPrice: number;
  price: number | null;
  realMargin: number | null;
  currency: string;
}
