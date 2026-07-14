import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { finalize, forkJoin } from 'rxjs';
import { Product, ProductCosting } from '../../../core/models/product.model';
import { EmployeeService } from '../../../core/services/employee.service';
import { FixedCostService } from '../../../core/services/fixed-cost.service';
import { ProductService } from '../../../core/services/product.service';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';

@Component({
  selector: 'app-dashboard-page',
  imports: [MatButtonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule, MatTableModule, MoneyPipe],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss',
})
export class DashboardPageComponent {
  private readonly productService = inject(ProductService);
  private readonly fixedCostService = inject(FixedCostService);
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);

  protected readonly loading = signal(true);
  protected readonly fixedTotal = signal<number | null>(null);
  protected readonly employeeTotal = signal<number | null>(null);
  protected readonly products = signal<Product[]>([]);
  protected readonly pricings = signal<ProductCosting[]>([]);
  protected readonly costingUnavailable = signal(false);
  protected readonly displayedColumns = ['productName', 'totalCost', 'appliedMargin', 'suggestedPrice', 'price', 'realMargin'];

  constructor() {
    this.loadAll();
  }

  private loadAll(): void {
    this.loading.set(true);
    forkJoin({
      products: this.productService.getAll(),
      fixedTotal: this.fixedCostService.getTotal(),
      employeeTotal: this.employeeService.getTotal(),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ products, fixedTotal, employeeTotal }) => {
          this.products.set(products);
          this.fixedTotal.set(fixedTotal);
          this.employeeTotal.set(employeeTotal);
          this.loadPricings(products);
        },
        error: () => {},
      });
  }

  private loadPricings(products: Product[]): void {
    if (products.length === 0) return;
    forkJoin(products.map((p) => this.productService.getPricing(p.id!))).subscribe({
      next: (pricings) => this.pricings.set(pricings),
      error: (err: HttpErrorResponse) => {
        if (err.status === 409) this.costingUnavailable.set(true);
      },
    });
  }

  goToCostSettings(): void {
    this.router.navigate(['/cost-settings']);
  }
}
