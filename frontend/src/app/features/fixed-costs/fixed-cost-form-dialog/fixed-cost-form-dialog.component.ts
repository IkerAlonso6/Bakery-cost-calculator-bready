import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FixedCost, FixedCostCreateRequest } from '../../../core/models/fixed-cost.model';

@Component({
  selector: 'app-fixed-cost-form-dialog',
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule],
  templateUrl: './fixed-cost-form-dialog.component.html',
  styleUrl: './fixed-cost-form-dialog.component.scss',
})
export class FixedCostFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<FixedCostFormDialogComponent, FixedCostCreateRequest>);
  protected readonly data = inject<FixedCost | null>(MAT_DIALOG_DATA);

  protected readonly isEdit = this.data !== null;

  protected readonly form = this.fb.nonNullable.group({
    name: [this.data?.name ?? '', [Validators.required]],
    monthlyAmount: [this.data?.monthlyAmount ?? 0, [Validators.required, Validators.min(0)]],
  });

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.getRawValue());
  }
}
