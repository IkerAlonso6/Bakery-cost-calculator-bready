import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RecipeCreateRequest } from '../../../core/models/recipe.model';
import { UNIT_OPTIONS } from '../../../core/models/unit-of-measurement';

@Component({
  selector: 'app-recipe-form-dialog',
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  templateUrl: './recipe-form-dialog.component.html',
  styleUrl: './recipe-form-dialog.component.scss',
})
export class RecipeFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<RecipeFormDialogComponent, RecipeCreateRequest>);
  protected readonly unitOptions = UNIT_OPTIONS;

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    yieldQuantity: [1, [Validators.required, Validators.min(0.0001)]],
    yieldUnit: [this.unitOptions[0].value, [Validators.required]],
  });

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.getRawValue());
  }
}
