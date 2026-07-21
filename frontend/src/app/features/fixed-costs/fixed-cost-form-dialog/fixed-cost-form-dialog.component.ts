import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {
  FIXED_COST_CATEGORIES,
  FixedCost,
  FixedCostCategory,
  FixedCostCreateRequest,
} from '../../../core/models/fixed-cost.model';

/**
 * fixedCost: la fila a editar, o null si es alta nueva.
 * period: mes al que pertenece el nuevo costo fijo (lo decide la lista, no este diálogo).
 */
export interface FixedCostFormDialogData {
  fixedCost: FixedCost | null;
  period: string;
}

@Component({
  selector: 'app-fixed-cost-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './fixed-cost-form-dialog.component.html',
  styleUrl: './fixed-cost-form-dialog.component.scss',
})
export class FixedCostFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<FixedCostFormDialogComponent, FixedCostCreateRequest>);
  protected readonly data = inject<FixedCostFormDialogData>(MAT_DIALOG_DATA);

  protected readonly isEdit = this.data.fixedCost !== null;
  protected readonly categories = FIXED_COST_CATEGORIES;

  protected readonly form = this.fb.nonNullable.group({
    name: [this.data.fixedCost?.name ?? '', [Validators.required]],
    monthlyAmount: [this.data.fixedCost?.monthlyAmount ?? 0, [Validators.required, Validators.min(0)]],
    category: [this.data.fixedCost?.category ?? ('OTROS' as FixedCostCategory), [Validators.required]],
  });

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.getRawValue();
    this.dialogRef.close({
      name: raw.name,
      monthlyAmount: raw.monthlyAmount,
      category: raw.category,
      period: this.data.fixedCost?.period ?? this.data.period,
    });
  }
}
