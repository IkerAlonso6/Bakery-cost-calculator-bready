import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { finalize } from 'rxjs';
import { EMPLOYEE_CATEGORIES, Employee, EmployeeCategory } from '../../../core/models/employee.model';
import { EmployeeService } from '../../../core/services/employee.service';
import { openConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { EmployeeFormDialogComponent } from '../employee-form-dialog/employee-form-dialog.component';
import { MoneyPipe } from '../../../shared/pipes/money.pipe';
import { currentPeriod, formatPeriodLabel, nextPeriod, previousPeriod } from '../../../shared/utils/period.util';

@Component({
  selector: 'app-employee-list',
  imports: [
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatDialogModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatTooltipModule,
    MoneyPipe,
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
  protected readonly period = signal(currentPeriod());
  protected readonly periodLabel = computed(() => formatPeriodLabel(this.period()));

  protected readonly categories = EMPLOYEE_CATEGORIES;
  protected readonly selectedCategory = signal<EmployeeCategory | 'ALL'>('ALL');
  protected readonly filteredEmployees = computed(() => {
    const category = this.selectedCategory();
    const employees = this.employees();
    return category === 'ALL' ? employees : employees.filter((e) => e.category === category);
  });

  protected readonly displayedColumns = ['name', 'category', 'monthlySalary', 'monthlyHours', 'costPerHour', 'actions'];

  constructor() {
    this.load();
  }

  private load(): void {
    this.loading.set(true);
    const period = this.period();
    this.employeeService
      .getAll(period)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (employees) => this.employees.set(employees),
        error: () => {},
      });
    this.employeeService.getTotal(period).subscribe({
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

  categoryLabel(category: EmployeeCategory): string {
    return this.categories.find((c) => c.value === category)?.label ?? category;
  }

  openCreateDialog(): void {
    this.dialog
      .open(EmployeeFormDialogComponent, { data: { employee: null, period: this.period() }, width: '420px' })
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
      .open(EmployeeFormDialogComponent, { data: { employee, period: employee.period }, width: '420px' })
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

  duplicatePreviousMonth(): void {
    const from = previousPeriod(this.period());
    const to = this.period();
    openConfirmDialog(this.dialog, {
      title: 'Duplicar mes anterior',
      message: `Se copiarán los empleados de ${formatPeriodLabel(from)} a ${formatPeriodLabel(to)} (los que ya existan en ${formatPeriodLabel(to)} con el mismo nombre no se duplican). ¿Continuar?`,
      confirmLabel: 'Duplicar',
    })
      .afterClosed()
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.employeeService.duplicatePreviousPeriod(from, to).subscribe({
          next: (created) => {
            const message = created.length > 0 ? `${created.length} empleado(s) copiado(s)` : 'No había nada nuevo para copiar';
            this.snackBar.open(message, 'Cerrar', { duration: 3000 });
            this.load();
          },
          error: () => {},
        });
      });
  }
}
