import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import { Recipe } from '../../../core/models/recipe.model';
import { RecipeService } from '../../../core/services/recipe.service';
import { UnitSymbolPipe } from '../../../shared/pipes/unit-symbol.pipe';
import { openConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { RecipeFormDialogComponent } from '../recipe-form-dialog/recipe-form-dialog.component';

@Component({
  selector: 'app-recipe-list',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    UnitSymbolPipe,
  ],
  templateUrl: './recipe-list.component.html',
})
export class RecipeListComponent {
  private readonly recipeService = inject(RecipeService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  protected readonly loading = signal(true);
  protected readonly recipes = signal<Recipe[]>([]);

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.recipeService
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (recipes) => this.recipes.set(recipes),
        error: () => {},
      });
  }

  openCreateDialog(): void {
    this.dialog
      .open(RecipeFormDialogComponent, { width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.recipeService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Receta creada', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  viewDetail(recipe: Recipe): void {
    this.router.navigate(['/recipes', recipe.id]);
  }

  delete(recipe: Recipe, event: Event): void {
    event.stopPropagation();
    openConfirmDialog(this.dialog, {
      title: 'Eliminar receta',
      message: `¿Seguro que querés eliminar "${recipe.name}"?`,
    })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.recipeService.delete(recipe.id!).subscribe({
          next: () => {
            this.snackBar.open('Receta eliminada', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }
}
