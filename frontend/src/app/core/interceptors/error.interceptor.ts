import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { ErrorResponse } from '../models/error-response.model';

/**
 * 409 en estos endpoints es un estado de negocio esperado (cost-settings sin
 * configurar todavía), no un error genérico: cada componente que los llama
 * lo maneja con su propia UX (banner + link a /cost-settings), así que acá
 * no se muestra un snackbar duplicado.
 */
function isExpectedConflict(status: number, url: string): boolean {
  return status === 409 && (url.includes('/cost-settings') || url.includes('/pricing'));
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (!isExpectedConflict(err.status, req.url)) {
        const body = err.error as ErrorResponse | undefined;
        const message = body?.message ?? 'Error de conexión con el servidor';
        snackBar.open(message, 'Cerrar', { duration: 5000 });
      }
      return throwError(() => err);
    }),
  );
};
