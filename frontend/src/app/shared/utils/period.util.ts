/**
 * Utilidades para trabajar con períodos mensuales "yyyy-MM" (mismo formato
 * que usa el backend para category/period-scoped FixedCost y Employee).
 */

export function currentPeriod(): string {
  const now = new Date();
  return toPeriod(now.getFullYear(), now.getMonth());
}

export function previousPeriod(period: string): string {
  return shiftPeriod(period, -1);
}

export function nextPeriod(period: string): string {
  return shiftPeriod(period, 1);
}

function shiftPeriod(period: string, deltaMonths: number): string {
  const [year, month] = parsePeriod(period);
  const shifted = new Date(year, month + deltaMonths, 1);
  return toPeriod(shifted.getFullYear(), shifted.getMonth());
}

/** Ej: "2026-07" -> "Julio 2026". */
export function formatPeriodLabel(period: string): string {
  const [year, month] = parsePeriod(period);
  const label = new Intl.DateTimeFormat('es-AR', { month: 'long', year: 'numeric' }).format(new Date(year, month, 1));
  return label.charAt(0).toUpperCase() + label.slice(1);
}

function parsePeriod(period: string): [year: number, monthIndex: number] {
  const [year, month] = period.split('-').map(Number);
  return [year, month - 1];
}

function toPeriod(year: number, monthIndex: number): string {
  return `${year}-${String(monthIndex + 1).padStart(2, '0')}`;
}
