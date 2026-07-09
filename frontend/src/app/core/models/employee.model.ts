export interface Employee {
  id: number | null;
  name: string;
  monthlySalary: number;
  monthlyHours: number | null;
  costPerHour: number | null; // solo lectura, informativa
}

export type EmployeeCreateRequest = Omit<Employee, 'id' | 'costPerHour'>;
