import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { EMPLOYEE_CATEGORIES, Employee, EmployeeCategory, EmployeeCreateRequest } from '../../../core/models/employee.model';

/**
 * employee: la fila a editar, o null si es alta nueva.
 * period: mes al que pertenece el nuevo empleado (lo decide la lista, no este diálogo).
 */
export interface EmployeeFormDialogData {
  employee: Employee | null;
  period: string;
}

@Component({
  selector: 'app-employee-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './employee-form-dialog.component.html',
  styleUrl: './employee-form-dialog.component.scss',
})
export class EmployeeFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<EmployeeFormDialogComponent, EmployeeCreateRequest>);
  protected readonly data = inject<EmployeeFormDialogData>(MAT_DIALOG_DATA);

  protected readonly isEdit = this.data.employee !== null;
  protected readonly categories = EMPLOYEE_CATEGORIES;

  protected readonly form = this.fb.nonNullable.group({
    name: [this.data.employee?.name ?? '', [Validators.required]],
    monthlySalary: [this.data.employee?.monthlySalary ?? 0, [Validators.required, Validators.min(0)]],
    monthlyHours: [this.data.employee?.monthlyHours ?? (null as number | null), [Validators.min(0.0001)]],
    category: [this.data.employee?.category ?? ('OTROS' as EmployeeCategory), [Validators.required]],
  });

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.getRawValue();
    this.dialogRef.close({
      name: raw.name,
      monthlySalary: raw.monthlySalary,
      monthlyHours: raw.monthlyHours || null,
      category: raw.category,
      period: this.data.employee?.period ?? this.data.period,
    });
  }
}
