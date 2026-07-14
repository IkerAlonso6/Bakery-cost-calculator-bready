import { Component, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Input as BakeryInput } from '../../../core/models/input.model';
import { RecipeCreateRequest } from '../../../core/models/recipe.model';
import { UNIT_OPTIONS } from '../../../core/models/unit-of-measurement';
import { InputService } from '../../../core/services/input.service';

@Component({
  selector: 'app-recipe-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './recipe-form-dialog.component.html',
  styleUrl: './recipe-form-dialog.component.scss',
})
export class RecipeFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<RecipeFormDialogComponent, RecipeCreateRequest>);
  private readonly inputService = inject(InputService);
  protected readonly unitOptions = UNIT_OPTIONS;
  protected readonly availableInputs = signal<BakeryInput[]>([]);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    yieldQuantity: [1, [Validators.required, Validators.min(0.0001)]],
    yieldUnit: [this.unitOptions[0].value, [Validators.required]],
    ingredients: this.fb.array<FormGroup>([]),
  });

  constructor() {
    this.inputService.getAll().subscribe({
      next: (inputs) => this.availableInputs.set(inputs),
      error: () => {},
    });
  }

  protected get ingredients(): FormArray<FormGroup> {
    return this.form.controls.ingredients;
  }

  addIngredientRow(): void {
    this.ingredients.push(
      this.fb.group({
        inputId: [null as number | null, [Validators.required]],
        quantity: [1, [Validators.required, Validators.min(0.0001)]],
      }),
    );
  }

  removeIngredientRow(index: number): void {
    this.ingredients.removeAt(index);
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.getRawValue();
    this.dialogRef.close({
      name: raw.name,
      yieldQuantity: raw.yieldQuantity,
      yieldUnit: raw.yieldUnit,
      ingredients: raw.ingredients.map((ing) => ({
        inputId: ing['inputId'] as number,
        quantity: ing['quantity'] as number,
      })),
    });
  }
}
