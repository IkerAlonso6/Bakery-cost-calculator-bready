import { UnitOfMeasurement } from './unit-of-measurement';

export interface Input {
  id: number | null;
  name: string;
  unitOfMeasure: UnitOfMeasurement;
  price: number;
}

export type InputCreateRequest = Omit<Input, 'id'>;
