import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import { Input } from '../../../core/models/input.model';
import { InputService } from '../../../core/services/input.service';
import { UnitSymbolPipe } from '../../../shared/pipes/unit-symbol.pipe';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';
import { openConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { InputFormDialogComponent } from '../input-form-dialog/input-form-dialog.component';

@Component({
  selector: 'app-input-list',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    UnitSymbolPipe,
    MoneyPipe,
  ],
  templateUrl: './input-list.component.html',
})
export class InputListComponent {
  private readonly inputService = inject(InputService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly loading = signal(true);
  protected readonly inputs = signal<Input[]>([]);

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.inputService
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (inputs) => this.inputs.set(inputs),
        error: () => {},
      });
  }

  openCreateDialog(): void {
    this.dialog
      .open(InputFormDialogComponent, { data: null, width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.inputService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Insumo creado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  openEditDialog(input: Input): void {
    this.dialog
      .open(InputFormDialogComponent, { data: input, width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.inputService.updatePrice(input.id!, result.price).subscribe({
          next: () => {
            this.snackBar.open('Precio actualizado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  delete(input: Input): void {
    openConfirmDialog(this.dialog, {
      title: 'Eliminar insumo',
      message: `¿Seguro que querés eliminar "${input.name}"?`,
    })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.inputService.delete(input.id!).subscribe({
          next: () => {
            this.snackBar.open('Insumo eliminado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }
}
