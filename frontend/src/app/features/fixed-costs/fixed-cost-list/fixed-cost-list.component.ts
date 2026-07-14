import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import { FixedCost } from '../../../core/models/fixed-cost.model';
import { FixedCostService } from '../../../core/services/fixed-cost.service';
import { openConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { FixedCostFormDialogComponent } from '../fixed-cost-form-dialog/fixed-cost-form-dialog.component';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';

@Component({
  selector: 'app-fixed-cost-list',
  imports: [
    MatButtonModule,
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

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.fixedCostService
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (fixedCosts) => this.fixedCosts.set(fixedCosts),
        error: () => {},
      });
    this.fixedCostService.getTotal().subscribe({
      next: (total) => this.total.set(total),
      error: () => {},
    });
  }

  openCreateDialog(): void {
    this.dialog
      .open(FixedCostFormDialogComponent, { data: null, width: '420px' })
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
      .open(FixedCostFormDialogComponent, { data: fixedCost, width: '420px' })
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
}
