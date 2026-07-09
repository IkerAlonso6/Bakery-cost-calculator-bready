import { Routes } from '@angular/router';
import { ShellComponent } from './core/layout/shell/shell.component';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard.routes').then((m) => m.DASHBOARD_ROUTES),
      },
      {
        path: 'inputs',
        loadChildren: () => import('./features/inputs/inputs.routes').then((m) => m.INPUTS_ROUTES),
      },
      {
        path: 'recipes',
        loadChildren: () => import('./features/recipes/recipes.routes').then((m) => m.RECIPES_ROUTES),
      },
      {
        path: 'products',
        loadChildren: () => import('./features/products/products.routes').then((m) => m.PRODUCTS_ROUTES),
      },
      {
        path: 'fixed-costs',
        loadChildren: () =>
          import('./features/fixed-costs/fixed-costs.routes').then((m) => m.FIXED_COSTS_ROUTES),
      },
      {
        path: 'employees',
        loadChildren: () => import('./features/employees/employees.routes').then((m) => m.EMPLOYEES_ROUTES),
      },
      {
        path: 'cost-settings',
        loadChildren: () =>
          import('./features/cost-settings/cost-settings.routes').then((m) => m.COST_SETTINGS_ROUTES),
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
