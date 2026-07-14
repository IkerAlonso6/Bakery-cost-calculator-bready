import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { ProfileService } from '../../../core/services/profile.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-profile-page',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './profile-page.component.html',
  styleUrl: './profile-page.component.scss',
})
export class ProfilePageComponent {
  private readonly fb = inject(FormBuilder);
  private readonly profileService = inject(ProfileService);
  private readonly authService = inject(AuthService);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly loading = signal(true);
  protected readonly savingName = signal(false);
  protected readonly uploadingPhoto = signal(false);
  protected readonly user = signal<User | null>(null);
  /** Cambia para forzar la recarga del avatar tras subir una foto nueva. */
  protected readonly photoVersion = signal(Date.now());

  protected readonly form = this.fb.nonNullable.group({
    displayName: ['', [Validators.required]],
  });

  constructor() {
    this.load();
  }

  protected avatarUrl(): string | null {
    const u = this.user();
    return u?.hasPhoto ? this.profileService.photoUrl(this.photoVersion()) : null;
  }

  protected initials(): string {
    const name = this.user()?.displayName ?? '';
    return name.trim().charAt(0).toUpperCase() || '?';
  }

  private load(): void {
    this.loading.set(true);
    this.profileService
      .getProfile()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (user) => {
          this.user.set(user);
          this.form.controls.displayName.setValue(user.displayName);
          this.authService.setUser(user);
        },
        error: () => {},
      });
  }

  saveName(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.savingName.set(true);
    this.profileService
      .updateName(this.form.getRawValue().displayName)
      .pipe(finalize(() => this.savingName.set(false)))
      .subscribe({
        next: (user) => {
          this.user.set(user);
          this.authService.setUser(user);
          this.snackBar.open('Perfil actualizado', 'Cerrar', { duration: 3000 });
        },
        error: () => {},
      });
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }
    this.uploadingPhoto.set(true);
    this.profileService
      .uploadPhoto(file)
      .pipe(finalize(() => this.uploadingPhoto.set(false)))
      .subscribe({
        next: () => {
          const current = this.user();
          if (current) {
            const updated = { ...current, hasPhoto: true };
            this.user.set(updated);
            this.authService.setUser(updated);
          }
          this.photoVersion.set(Date.now());
          this.snackBar.open('Foto actualizada', 'Cerrar', { duration: 3000 });
        },
        error: () => {},
      });
    input.value = '';
  }
}
