import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Employee, EmployeeCreateRequest } from '../../../core/models/employee.model';

@Component({
  selector: 'app-employee-form-dialog',
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule],
  templateUrl: './employee-form-dialog.component.html',
  styleUrl: './employee-form-dialog.component.scss',
})
export class EmployeeFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<EmployeeFormDialogComponent, EmployeeCreateRequest>);
  protected readonly data = inject<Employee | null>(MAT_DIALOG_DATA);

  protected readonly isEdit = this.data !== null;

  protected readonly form = this.fb.nonNullable.group({
    name: [this.data?.name ?? '', [Validators.required]],
    monthlySalary: [this.data?.monthlySalary ?? 0, [Validators.required, Validators.min(0)]],
    monthlyHours: [this.data?.monthlyHours ?? (null as number | null), [Validators.min(0.0001)]],
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
    });
  }
}
