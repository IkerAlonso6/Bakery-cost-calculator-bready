import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { catchError, finalize, forkJoin, of } from 'rxjs';
import { Product, ProductCosting } from '../../../core/models/product.model';
import { ProductService } from '../../../core/services/product.service';
import { openConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { ProductFormDialogComponent } from '../product-form-dialog/product-form-dialog.component';

interface ProductCard {
  product: Product;
  costing: ProductCosting | null;
}

@Component({
  selector: 'app-product-list',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
  ],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss',
})
export class ProductListComponent {
  private readonly productService = inject(ProductService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);

  protected readonly loading = signal(true);
  protected readonly cards = signal<ProductCard[]>([]);

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.productService
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (products) => {
          this.cards.set(products.map((product) => ({ product, costing: null })));
          this.loadCostings(products);
        },
        error: () => {},
      });
  }

  private loadCostings(products: Product[]): void {
    if (products.length === 0) return;
    forkJoin(
      products.map((product) =>
        this.productService.getPricing(product.id!).pipe(catchError(() => of(null))),
      ),
    ).subscribe((costings) => {
      this.cards.set(products.map((product, i) => ({ product, costing: costings[i] })));
    });
  }

  marginBadgeClass(costing: ProductCosting | null): string {
    if (!costing) return 'badge-neutral';
    const margin = costing.realMargin ?? costing.appliedMargin;
    if (margin >= 0.35) return 'badge-profit';
    if (margin >= 0.15) return 'badge-neutral';
    return 'badge-loss';
  }

  marginBadgeIcon(costing: ProductCosting | null): string {
    if (!costing) return 'help_outline';
    const margin = costing.realMargin ?? costing.appliedMargin;
    if (margin >= 0.35) return 'trending_up';
    if (margin >= 0.15) return 'trending_flat';
    return 'trending_down';
  }

  openCreateDialog(): void {
    this.dialog
      .open(ProductFormDialogComponent, { width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.productService.create({ name: result.name, recipeId: result.recipeId, price: null, targetMargin: null }).subscribe({
          next: () => {
            this.snackBar.open('Producto creado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  viewDetail(product: Product): void {
    this.router.navigate(['/products', product.id]);
  }

  delete(product: Product, event: Event): void {
    event.stopPropagation();
    openConfirmDialog(this.dialog, {
      title: 'Eliminar producto',
      message: `¿Seguro que querés eliminar "${product.name}"?`,
    })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.productService.delete(product.id!).subscribe({
          next: () => {
            this.snackBar.open('Producto eliminado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }
}
