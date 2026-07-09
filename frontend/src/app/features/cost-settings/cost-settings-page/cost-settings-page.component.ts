import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, finalize, of } from 'rxjs';
import { CostSettingsService } from '../../../core/services/cost-settings.service';

@Component({
  selector: 'app-cost-settings-page',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './cost-settings-page.component.html',
  styleUrl: './cost-settings-page.component.scss',
})
export class CostSettingsPageComponent {
  private readonly costSettingsService = inject(CostSettingsService);
  private readonly fb = inject(FormBuilder);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly notConfiguredYet = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    defaultTargetMarginPercent: [0, [Validators.required, Validators.min(0), Validators.max(99.99)]],
    monthlyMaterialBase: [0, [Validators.required, Validators.min(0.01)]],
    currency: ['ARS', [Validators.required]],
  });

  constructor() {
    this.costSettingsService
      .get()
      .pipe(
        catchError((err: HttpErrorResponse) => {
          if (err.status === 409) {
            this.notConfiguredYet.set(true);
            return of(null);
          }
          throw err;
        }),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (settings) => {
          if (settings) {
            this.form.setValue({
              defaultTargetMarginPercent: Math.round(settings.defaultTargetMargin * 10000) / 100,
              monthlyMaterialBase: settings.monthlyMaterialBase,
              currency: settings.currency,
            });
          }
        },
        error: () => {},
      });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.saving.set(true);
    this.costSettingsService
      .update({
        defaultTargetMargin: value.defaultTargetMarginPercent / 100,
        monthlyMaterialBase: value.monthlyMaterialBase,
        currency: value.currency,
      })
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: (settings) => {
          this.notConfiguredYet.set(false);
          this.form.setValue({
            defaultTargetMarginPercent: Math.round(settings.defaultTargetMargin * 10000) / 100,
            monthlyMaterialBase: settings.monthlyMaterialBase,
            currency: settings.currency,
          });
          this.snackBar.open('Configuración de costeo guardada', 'Cerrar', { duration: 3000 });
        },
        error: () => {},
      });
  }
}
