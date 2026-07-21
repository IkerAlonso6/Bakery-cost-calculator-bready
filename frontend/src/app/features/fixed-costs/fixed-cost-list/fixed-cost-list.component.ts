import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import { FIXED_COST_CATEGORIES, FixedCost, FixedCostCategory } from '../../../core/models/fixed-cost.model';
import { FixedCostService } from '../../../core/services/fixed-cost.service';
import { openConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { FixedCostFormDialogComponent } from '../fixed-cost-form-dialog/fixed-cost-form-dialog.component';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';
import { currentPeriod, formatPeriodLabel, nextPeriod, previousPeriod } from '../../../shared/utils/period.util';

@Component({
  selector: 'app-fixed-cost-list',
  imports: [
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MoneyPipe,
  ],
  templateUrl: './fixed-cost-list.component.html',
})
export class FixedCostListComponent {
  private readonly fixedCostService = inject(FixedCostService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly loading = signal(true);
  protected readonly fixedCosts = signal<FixedCost[]>([]);
  protected readonly total = signal<number | null>(null);
  protected readonly period = signal(currentPeriod());
  protected readonly periodLabel = computed(() => formatPeriodLabel(this.period()));

  protected readonly categories = FIXED_COST_CATEGORIES;
  protected readonly selectedCategory = signal<FixedCostCategory | 'ALL'>('ALL');
  protected readonly filteredFixedCosts = computed(() => {
    const category = this.selectedCategory();
    const fixedCosts = this.fixedCosts();
    return category === 'ALL' ? fixedCosts : fixedCosts.filter((f) => f.category === category);
  });

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    const period = this.period();
    this.fixedCostService
      .getAll(period)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (fixedCosts) => this.fixedCosts.set(fixedCosts),
        error: () => {},
      });
    this.fixedCostService.getTotal(period).subscribe({
      next: (total) => this.total.set(total),
      error: () => {},
    });
  }

  previousMonth(): void {
    this.period.set(previousPeriod(this.period()));
    this.load();
  }

  nextMonth(): void {
    this.period.set(nextPeriod(this.period()));
    this.load();
  }

  categoryLabel(category: FixedCostCategory): string {
    return this.categories.find((c) => c.value === category)?.label ?? category;
  }

  openCreateDialog(): void {
    this.dialog
      .open(FixedCostFormDialogComponent, { data: { fixedCost: null, period: this.period() }, width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.fixedCostService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Costo fijo creado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  openEditDialog(fixedCost: FixedCost): void {
    this.dialog
      .open(FixedCostFormDialogComponent, { data: { fixedCost, period: fixedCost.period }, width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.fixedCostService.update(fixedCost.id!, result).subscribe({
          next: () => {
            this.snackBar.open('Costo fijo actualizado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  delete(fixedCost: FixedCost): void {
    openConfirmDialog(this.dialog, {
      title: 'Eliminar costo fijo',
      message: `¿Seguro que querés eliminar "${fixedCost.name}"?`,
    })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.fixedCostService.delete(fixedCost.id!).subscribe({
          next: () => {
            this.snackBar.open('Costo fijo eliminado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  duplicatePreviousMonth(): void {
    const from = previousPeriod(this.period());
    const to = this.period();
    openConfirmDialog(this.dialog, {
      title: 'Duplicar mes anterior',
      message: `Se copiarán los costos fijos de ${formatPeriodLabel(from)} a ${formatPeriodLabel(to)} (los que ya existan en ${formatPeriodLabel(to)} con el mismo nombre no se duplican). ¿Continuar?`,
      confirmLabel: 'Duplicar',
    })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.fixedCostService.duplicatePreviousPeriod(from, to).subscribe({
          next: (created) => {
            const message = created.length > 0 ? `${created.length} costo(s) fijo(s) copiado(s)` : 'No había nada nuevo para copiar';
            this.snackBar.open(message, 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }
}
