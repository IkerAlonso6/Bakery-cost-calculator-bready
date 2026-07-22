import { Component, computed, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { map } from 'rxjs';
import { NAV_ITEMS } from '../nav-items';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-shell',
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
  ],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  private readonly breakpointObserver = inject(BreakpointObserver);
  private readonly authService = inject(AuthService);
  protected readonly navItems = NAV_ITEMS;
  protected readonly user = this.authService.currentUser;
  protected readonly avatarUrl = this.authService.avatarUrl;

  protected readonly initials = computed(() => {
    const name = this.user()?.displayName ?? '';
    return name.trim().charAt(0).toUpperCase() || '?';
  });

  protected readonly isHandset = toSignal(
    this.breakpointObserver.observe(Breakpoints.Handset).pipe(map((result) => result.matches)),
    { initialValue: false },
  );

  /**
   * Estado del sidenav. mat-sidenav se cierra solo con Escape o el backdrop
   * (aun en modo "side"), así que necesita [opened]/(openedChange) explícito
   * -- NO el atajo [(opened)]="sidenavOpened", que le pasa la función del
   * signal en vez de su valor y nunca sincroniza el cierre -- para poder
   * reabrirlo después. Se resetea al valor por defecto de cada modo al
   * cruzar el breakpoint, sin pisar un toggle manual mientras el modo no
   * cambia.
   */
  protected readonly sidenavOpened = signal(!this.isHandset());

  constructor() {
    effect(() => this.sidenavOpened.set(!this.isHandset()));
  }

  logout(): void {
    this.authService.logout();
  }
}
