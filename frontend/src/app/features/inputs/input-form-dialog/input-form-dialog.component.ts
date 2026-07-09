import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Input } from '../../../core/models/input.model';
import { UNIT_OPTIONS } from '../../../core/models/unit-of-measurement';

@Component({
  selector: 'app-input-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatTooltipModule,
  ],
  templateUrl: './input-form-dialog.component.html',
  styleUrl: './input-form-dialog.component.scss',
})
export class InputFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<InputFormDialogComponent, Input>);
  protected readonly data = inject<Input | null>(MAT_DIALOG_DATA);

  protected readonly isEdit = this.data !== null;
  protected readonly unitOptions = UNIT_OPTIONS;

  protected readonly form = this.fb.nonNullable.group({
    name: [this.data?.name ?? '', [Validators.required]],
    unitOfMeasure: [this.data?.unitOfMeasure ?? this.unitOptions[0].value, [Validators.required]],
    price: [this.data?.price ?? 0, [Validators.required, Validators.min(0)]],
  });

  constructor() {
    if (this.isEdit) {
      this.form.controls.name.disable();
      this.form.controls.unitOfMeasure.disable();
    }
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.getRawValue();
    this.dialogRef.close({
      id: this.data?.id ?? null,
      name: raw.name,
      unitOfMeasure: raw.unitOfMeasure,
      price: raw.price,
    });
  }
}
