import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import { Employee } from '../../../core/models/employee.model';
import { EmployeeService } from '../../../core/services/employee.service';
import { openConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { EmployeeFormDialogComponent } from '../employee-form-dialog/employee-form-dialog.component';

@Component({
  selector: 'app-employee-list',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
  ],
  templateUrl: './employee-list.component.html',
})
export class EmployeeListComponent {
  private readonly employeeService = inject(EmployeeService);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly loading = signal(true);
  protected readonly employees = signal<Employee[]>([]);
  protected readonly total = signal<number | null>(null);
  protected readonly displayedColumns = ['name', 'monthlySalary', 'monthlyHours', 'costPerHour', 'actions'];

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    this.employeeService
      .getAll()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (employees) => this.employees.set(employees),
        error: () => {},
      });
    this.employeeService.getTotal().subscribe({
      next: (total) => this.total.set(total),
      error: () => {},
    });
  }

  openCreateDialog(): void {
    this.dialog
      .open(EmployeeFormDialogComponent, { data: null, width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.employeeService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Empleado creado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  openEditDialog(employee: Employee): void {
    this.dialog
      .open(EmployeeFormDialogComponent, { data: employee, width: '420px' })
      .afterClosed()
      .subscribe((result) => {
        if (!result) return;
        this.employeeService.update(employee.id!, result).subscribe({
          next: () => {
            this.snackBar.open('Empleado actualizado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }

  delete(employee: Employee): void {
    openConfirmDialog(this.dialog, {
      title: 'Eliminar empleado',
      message: `¿Seguro que querés eliminar "${employee.name}"?`,
    })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.employeeService.delete(employee.id!).subscribe({
          next: () => {
            this.snackBar.open('Empleado eliminado', 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }
}
