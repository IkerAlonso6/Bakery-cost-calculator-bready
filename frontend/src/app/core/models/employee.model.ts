export type EmployeeCategory = 'PRODUCCION' | 'ADMINISTRACION' | 'VENTAS' | 'OTROS';

export const EMPLOYEE_CATEGORIES: { value: EmployeeCategory; label: string }[] = [
  { value: 'PRODUCCION', label: 'Producción' },
  { value: 'ADMINISTRACION', label: 'Administración' },
  { value: 'VENTAS', label: 'Ventas' },
  { value: 'OTROS', label: 'Otros' },
];

export interface Employee {
  id: number | null;
  name: string;
  monthlySalary: number;
  monthlyHours: number | null;
  category: EmployeeCategory;
  period: string; // "yyyy-MM", no editable post-creación
  costPerHour: number | null; // solo lectura, informativa
}

export type EmployeeCreateRequest = Omit<Employee, 'id' | 'costPerHour'>;
