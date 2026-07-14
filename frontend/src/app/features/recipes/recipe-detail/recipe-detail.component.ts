import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { finalize, forkJoin } from 'rxjs';
import { Input as BakeryInput } from '../../../core/models/input.model';
import { Recipe } from '../../../core/models/recipe.model';
import { InputService } from '../../../core/services/input.service';
import { RecipeService } from '../../../core/services/recipe.service';
import { UnitSymbolPipe } from '../../../shared/pipes/unit-symbol.pipe';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';

@Component({
  selector: 'app-recipe-detail',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTableModule,
    UnitSymbolPipe,
    MoneyPipe,
  ],
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.scss',
})
export class RecipeDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly recipeService = inject(RecipeService);
  private readonly inputService = inject(InputService);
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);

  private readonly recipeId = Number(this.route.snapshot.paramMap.get('id'));

  protected readonly loading = signal(true);
  protected readonly adding = signal(false);
  protected readonly recipe = signal<Recipe | null>(null);
  protected readonly cost = signal<number | null>(null);
  protected readonly availableInputs = signal<BakeryInput[]>([]);
  protected readonly displayedColumns = ['inputName', 'quantity', 'cost', 'actions'];

  protected readonly form = this.fb.nonNullable.group({
    inputId: [null as number | null, [Validators.required]],
    quantity: [0, [Validators.required, Validators.min(0.0001)]],
  });

  constructor() {
    this.loadAll();
  }

  private loadAll(): void {
    this.loading.set(true);
    forkJoin({
      recipe: this.recipeService.getById(this.recipeId),
      cost: this.recipeService.getCost(this.recipeId),
      inputs: this.inputService.getAll(),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ recipe, cost, inputs }) => {
          this.recipe.set(recipe);
          this.cost.set(cost);
          this.availableInputs.set(inputs);
        },
        error: () => {},
      });
  }

  addIngredient(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.adding.set(true);
    this.recipeService
      .addIngredient(this.recipeId, { inputId: value.inputId!, quantity: value.quantity })
      .pipe(finalize(() => this.adding.set(false)))
      .subscribe({
        next: () => {
          this.snackBar.open('Ingrediente agregado', 'Cerrar', { duration: 3000 });
          this.form.reset({ inputId: null, quantity: 0 });
          this.loadAll();
        },
        error: () => {},
      });
  }

  removeIngredient(ingredientId: number | null): void {
    if (ingredientId == null) {
      return;
    }
    this.recipeService.removeIngredient(this.recipeId, ingredientId).subscribe({
      next: () => {
        this.snackBar.open('Ingrediente quitado', 'Cerrar', { duration: 3000 });
        this.loadAll();
      },
      error: () => {},
    });
  }

  goBack(): void {
    this.router.navigate(['/recipes']);
  }
}
