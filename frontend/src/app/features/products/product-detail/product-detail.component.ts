import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { catchError, finalize, of } from 'rxjs';
import { Product, ProductCosting } from '../../../core/models/product.model';
import { ProductService } from '../../../core/services/product.service';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';
import { currentPeriod, formatPeriodLabel, nextPeriod, previousPeriod } from '../../../shared/utils/period.util';

@Component({
  selector: 'app-product-detail',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MoneyPipe,
  ],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss',
})
export class ProductDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly productService = inject(ProductService);
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);

  private readonly productId = Number(this.route.snapshot.paramMap.get('id'));

  protected readonly loading = signal(true);
  protected readonly product = signal<Product | null>(null);
  protected readonly pricing = signal<ProductCosting | null>(null);
  protected readonly costingUnavailable = signal(false);
  protected readonly savingPrice = signal(false);
  protected readonly savingMargin = signal(false);
  protected readonly period = signal(currentPeriod());
  protected readonly periodLabel = computed(() => formatPeriodLabel(this.period()));
  protected readonly fallbackNotice = computed(() => {
    const p = this.pricing();
    if (!p?.usedFallbackPeriod) return null;
    return `Usando datos de ${formatPeriodLabel(p.resolvedPeriod)} porque ${this.periodLabel()} no tiene gastos cargados.`;
  });

  protected readonly priceForm = this.fb.nonNullable.group({
    price: [0, [Validators.required, Validators.min(0)]],
  });

  protected readonly marginForm = this.fb.nonNullable.group({
    useGlobalMargin: [true],
    targetMarginPercent: [{ value: 0, disabled: true }, [Validators.min(0), Validators.max(99.99)]],
  });

  constructor() {
    this.marginForm.controls.useGlobalMargin.valueChanges.subscribe((useGlobal) => {
      if (useGlobal) {
        this.marginForm.controls.targetMarginPercent.disable();
      } else {
        this.marginForm.controls.targetMarginPercent.enable();
      }
    });
    this.loadAll();
  }

  private loadAll(): void {
    this.loading.set(true);
    this.productService
      .getById(this.productId)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (product) => {
          this.product.set(product);
          this.priceForm.setValue({ price: product.price ?? 0 });
          this.marginForm.setValue({
            useGlobalMargin: product.targetMargin === null,
            targetMarginPercent: product.targetMargin !== null ? Math.round(product.targetMargin * 10000) / 100 : 0,
          });
          this.loadPricing();
        },
        error: () => {},
      });
  }

  private loadPricing(): void {
    this.costingUnavailable.set(false);
    this.productService
      .getPricing(this.productId, this.period())
      .pipe(
        catchError((err: HttpErrorResponse) => {
          if (err.status === 409) {
            this.costingUnavailable.set(true);
            return of(null);
          }
          throw err;
        }),
      )
      .subscribe({
        next: (pricing) => {
          if (pricing) this.pricing.set(pricing);
        },
        error: () => {},
      });
  }

  previousMonth(): void {
    this.period.set(previousPeriod(this.period()));
    this.loadPricing();
  }

  nextMonth(): void {
    this.period.set(nextPeriod(this.period()));
    this.loadPricing();
  }

  savePrice(): void {
    if (this.priceForm.invalid) {
      this.priceForm.markAllAsTouched();
      return;
    }
    this.savingPrice.set(true);
    this.productService
      .updatePrice(this.productId, this.priceForm.getRawValue().price)
      .pipe(finalize(() => this.savingPrice.set(false)))
      .subscribe({
        next: (product) => {
          this.product.set(product);
          this.snackBar.open('Precio actualizado', 'Cerrar', { duration: 3000 });
          this.loadPricing();
        },
        error: () => {},
      });
  }

  saveMargin(): void {
    const value = this.marginForm.getRawValue();
    const targetMargin = value.useGlobalMargin ? null : value.targetMarginPercent / 100;
    this.savingMargin.set(true);
    this.productService
      .updateMargin(this.productId, targetMargin)
      .pipe(finalize(() => this.savingMargin.set(false)))
      .subscribe({
        next: (product) => {
          this.product.set(product);
          this.snackBar.open('Margen actualizado', 'Cerrar', { duration: 3000 });
          this.loadPricing();
        },
        error: () => {},
      });
  }

  goToCostSettings(): void {
    this.router.navigate(['/cost-settings']);
  }

  goBack(): void {
    this.router.navigate(['/products']);
  }
}
