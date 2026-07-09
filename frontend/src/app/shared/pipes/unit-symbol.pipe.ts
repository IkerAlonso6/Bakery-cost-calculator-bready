import { Pipe, PipeTransform } from '@angular/core';
import { UNIT_SYMBOLS, UnitOfMeasurement } from '../../core/models/unit-of-measurement';

@Pipe({ name: 'unitSymbol' })
export class UnitSymbolPipe implements PipeTransform {
  transform(value: UnitOfMeasurement): string {
    return UNIT_SYMBOLS[value] ?? value;
  }
}
