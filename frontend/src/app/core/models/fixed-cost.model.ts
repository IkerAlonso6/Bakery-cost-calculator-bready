export interface FixedCost {
  id: number | null;
  name: string;
  monthlyAmount: number;
}

export type FixedCostCreateRequest = Omit<FixedCost, 'id'>;
