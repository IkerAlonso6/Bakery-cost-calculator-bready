import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Recipe } from '../../../core/models/recipe.model';
import { RecipeService } from '../../../core/services/recipe.service';

export interface ProductFormResult {
  name: string;
  recipeId: number;
}

@Component({
  selector: 'app-product-form-dialog',
  imports: [ReactiveFormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  templateUrl: './product-form-dialog.component.html',
  styleUrl: './product-form-dialog.component.scss',
})
export class ProductFormDialogComponent {
  private readonly fb = inject(FormBuilder);
  private readonly recipeService = inject(RecipeService);
  private readonly dialogRef = inject(MatDialogRef<ProductFormDialogComponent, ProductFormResult>);

  protected readonly recipes = signal<Recipe[]>([]);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required]],
    recipeId: [null as number | null, [Validators.required]],
  });

  constructor() {
    this.recipeService.getAll().subscribe({
      next: (recipes) => this.recipes.set(recipes),
      error: () => {},
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const raw = this.form.getRawValue();
    this.dialogRef.close({ name: raw.name, recipeId: raw.recipeId! });
  }
}
