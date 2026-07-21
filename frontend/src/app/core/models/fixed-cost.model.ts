export type FixedCostCategory =
  | 'ALQUILER'
  | 'SERVICIOS'
  | 'MANTENIMIENTO'
  | 'MARKETING'
  | 'IMPUESTOS_SEGUROS'
  | 'OTROS';

export const FIXED_COST_CATEGORIES: { value: FixedCostCategory; label: string }[] = [
  { value: 'ALQUILER', label: 'Alquiler' },
  { value: 'SERVICIOS', label: 'Servicios' },
  { value: 'MANTENIMIENTO', label: 'Mantenimiento' },
  { value: 'MARKETING', label: 'Marketing' },
  { value: 'IMPUESTOS_SEGUROS', label: 'Impuestos y seguros' },
  { value: 'OTROS', label: 'Otros' },
];

export interface FixedCost {
  id: number | null;
  name: string;
  monthlyAmount: number;
  category: FixedCostCategory;
  period: string; // "yyyy-MM", no editable post-creación
}

export type FixedCostCreateRequest = Omit<FixedCost, 'id'>;
