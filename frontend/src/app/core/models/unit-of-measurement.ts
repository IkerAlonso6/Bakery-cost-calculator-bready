export enum UnitOfMeasurement {
  KILOGRAM = 'KILOGRAM',
  GRAM = 'GRAM',
  MILLIGRAM = 'MILLIGRAM',
  LITER = 'LITER',
  MILLILITER = 'MILLILITER',
  UNIT = 'UNIT',
}

export const UNIT_SYMBOLS: Record<UnitOfMeasurement, string> = {
  [UnitOfMeasurement.KILOGRAM]: 'kg',
  [UnitOfMeasurement.GRAM]: 'g',
  [UnitOfMeasurement.MILLIGRAM]: 'mg',
  [UnitOfMeasurement.LITER]: 'l',
  [UnitOfMeasurement.MILLILITER]: 'ml',
  [UnitOfMeasurement.UNIT]: 'u',
};

export const UNIT_OPTIONS = Object.values(UnitOfMeasurement).map((unit) => ({
  value: unit,
  label: `${unit} (${UNIT_SYMBOLS[unit]})`,
}));
