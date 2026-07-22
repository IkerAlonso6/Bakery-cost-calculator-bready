import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/user.model';

const TOKEN_KEY = 'bready.token';
const USER_KEY = 'bready.user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly base = `${environment.apiUrl}/auth`;
  private readonly photoUrl = `${environment.apiUrl}/profile/photo`;

  private readonly userSignal = signal<User | null>(this.readStoredUser());
  readonly currentUser = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.userSignal() !== null && this.getToken() !== null);

  private readonly avatarUrlSignal = signal<string | null>(null);
  /**
   * Object URL de la foto de perfil. Se pide con HttpClient (lleva el Bearer
   * del interceptor) y no se expone como URL directa: un <img src> dispara
   * un GET nativo del navegador sin ese header, y /api/profile/photo
   * requiere autenticación.
   */
  readonly avatarUrl = this.avatarUrlSignal.asReadonly();
  private avatarObjectUrl: string | null = null;

  constructor() {
    this.syncAvatar(this.userSignal()?.hasPhoto ?? false);
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/login`, request)
      .pipe(tap((res) => this.storeSession(res)));
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.base}/register`, request)
      .pipe(tap((res) => this.storeSession(res)));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.userSignal.set(null);
    this.syncAvatar(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  /** Actualiza el usuario en memoria/almacenamiento (p. ej. tras editar el perfil). */
  setUser(user: User): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
    this.userSignal.set(user);
    this.syncAvatar(user.hasPhoto);
  }

  private storeSession(res: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, res.token);
    this.setUser(res.user);
  }

  private readStoredUser(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }

  private syncAvatar(hasPhoto: boolean): void {
    this.releaseAvatarObjectUrl();
    if (!hasPhoto) {
      this.avatarUrlSignal.set(null);
      return;
    }
    this.http.get(this.photoUrl, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        this.avatarObjectUrl = URL.createObjectURL(blob);
        this.avatarUrlSignal.set(this.avatarObjectUrl);
      },
      error: () => this.avatarUrlSignal.set(null),
    });
  }

  private releaseAvatarObjectUrl(): void {
    if (this.avatarObjectUrl) {
      URL.revokeObjectURL(this.avatarObjectUrl);
      this.avatarObjectUrl = null;
    }
  }
}
