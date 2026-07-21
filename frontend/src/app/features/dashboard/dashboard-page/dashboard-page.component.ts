import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize, forkJoin } from 'rxjs';
import { EMPLOYEE_CATEGORIES } from '../../../core/models/employee.model';
import { FIXED_COST_CATEGORIES } from '../../../core/models/fixed-cost.model';
import { Product, ProductCosting } from '../../../core/models/product.model';
import { EmployeeService } from '../../../core/services/employee.service';
import { FixedCostService } from '../../../core/services/fixed-cost.service';
import { ProductService } from '../../../core/services/product.service';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';
import { currentPeriod, formatPeriodLabel, nextPeriod, previousPeriod } from '../../../shared/utils/period.util';

interface CategoryTotal {
  category: string;
  label: string;
  total: number;
}

@Component({
  selector: 'app-dashboard-page',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
    MoneyPipe,
  ],
  templateUrl: './dashboard-page.component.html',
  styleUrl: './dashboard-page.component.scss',
})
export class DashboardPageComponent {
  private readonly productService = inject(ProductService);
  private readonly fixedCostService = inject(FixedCostService);
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);
  private readonly moneyPipe = new MoneyPipe();

  protected readonly loading = signal(true);
  protected readonly period = signal(currentPeriod());
  protected readonly periodLabel = computed(() => formatPeriodLabel(this.period()));

  protected readonly fixedTotal = signal<number | null>(null);
  protected readonly employeeTotal = signal<number | null>(null);
  protected readonly fixedCostsByCategory = signal<CategoryTotal[]>([]);
  protected readonly employeesByCategory = signal<CategoryTotal[]>([]);
  protected readonly products = signal<Product[]>([]);
  protected readonly pricings = signal<ProductCosting[]>([]);
  protected readonly costingUnavailable = signal(false);
  protected readonly displayedColumns = ['productName', 'totalCost', 'appliedMargin', 'suggestedPrice', 'price', 'realMargin'];

  /** F y L de un mismo período dashboard se resuelven juntos, así que basta mirar el primer pricing. */
  protected readonly fallbackNotice = computed(() => {
    const first = this.pricings()[0];
    if (!first?.usedFallbackPeriod) return null;
    return `Usando datos de ${formatPeriodLabel(first.resolvedPeriod)} porque ${this.periodLabel()} no tiene gastos cargados.`;
  });

  constructor() {
    this.loadAll();
  }

  private loadAll(): void {
    this.loading.set(true);
    this.costingUnavailable.set(false);
    const period = this.period();
    forkJoin({
      products: this.productService.getAll(),
      fixedCosts: this.fixedCostService.getAll(period),
      employees: this.employeeService.getAll(period),
      fixedTotal: this.fixedCostService.getTotal(period),
      employeeTotal: this.employeeService.getTotal(period),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ products, fixedCosts, employees, fixedTotal, employeeTotal }) => {
          this.products.set(products);
          this.fixedTotal.set(fixedTotal);
          this.employeeTotal.set(employeeTotal);
          this.fixedCostsByCategory.set(
            groupByCategory(fixedCosts, FIXED_COST_CATEGORIES, (f) => f.category, (f) => f.monthlyAmount),
          );
          this.employeesByCategory.set(
            groupByCategory(employees, EMPLOYEE_CATEGORIES, (e) => e.category, (e) => e.monthlySalary),
          );
          this.loadPricings(products, period);
        },
        error: () => {},
      });
  }

  private loadPricings(products: Product[], period: string): void {
    this.pricings.set([]);
    if (products.length === 0) return;
    forkJoin(products.map((p) => this.productService.getPricing(p.id!, period))).subscribe({
      next: (pricings) => this.pricings.set(pricings),
      error: (err: HttpErrorResponse) => {
        if (err.status === 409) this.costingUnavailable.set(true);
      },
    });
  }

  previousMonth(): void {
    this.period.set(previousPeriod(this.period()));
    this.loadAll();
  }

  nextMonth(): void {
    this.period.set(nextPeriod(this.period()));
    this.loadAll();
  }

  costingBreakdownTooltip(p: ProductCosting): string {
    const material = this.moneyPipe.transform(p.materialCost);
    const labor = this.moneyPipe.transform(p.laborCost);
    const fixed = this.moneyPipe.transform(p.fixedCost);
    return `Materiales: ${material} · Mano de obra: ${labor} · Fijos: ${fixed}`;
  }

  goToCostSettings(): void {
    this.router.navigate(['/cost-settings']);
  }
}

function groupByCategory<T, C extends string>(
  items: T[],
  categoryDefs: { value: C; label: string }[],
  getCategory: (item: T) => C,
  getAmount: (item: T) => number,
): CategoryTotal[] {
  const totals = new Map<C, number>();
  for (const item of items) {
    const category = getCategory(item);
    totals.set(category, (totals.get(category) ?? 0) + getAmount(item));
  }
  return categoryDefs
    .filter((def) => totals.has(def.value))
    .map((def) => ({ category: def.value, label: def.label, total: totals.get(def.value)! }));
}
