import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formatea un importe monetario con símbolo de moneda.
 * Ej: 1234.5 -> "$ 1.234,50". Acepta null/undefined -> "—".
 * El símbolo por defecto es "$"; se puede pasar otro (p. ej. desde cost-settings).
 */
@Pipe({ name: 'money' })
export class MoneyPipe implements PipeTransform {
  transform(value: number | null | undefined, symbol = '$'): string {
    if (value == null || Number.isNaN(value)) {
      return '—';
    }
    const formatted = new Intl.NumberFormat('es-AR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
    return `${symbol} ${formatted}`;
  }
}
