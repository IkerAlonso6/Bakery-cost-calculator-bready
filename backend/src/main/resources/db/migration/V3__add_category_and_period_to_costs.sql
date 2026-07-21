-- =============================================================
-- BakeryCostCalculator (Bready) - V3: categorías + periodización
-- mensual de fixed_costs y employees.
-- =============================================================

-- -------------------------------------------------------------
-- fixed_costs: categoría (enum de aplicación validado con CHECK) + período
-- -------------------------------------------------------------
ALTER TABLE fixed_costs ADD COLUMN category VARCHAR(30);
UPDATE fixed_costs SET category = 'OTROS' WHERE category IS NULL;
ALTER TABLE fixed_costs ALTER COLUMN category SET NOT NULL;
ALTER TABLE fixed_costs ADD CONSTRAINT fixed_costs_category_check
    CHECK (category IN ('ALQUILER', 'SERVICIOS', 'MANTENIMIENTO', 'MARKETING', 'IMPUESTOS_SEGUROS', 'OTROS'));

ALTER TABLE fixed_costs ADD COLUMN period DATE;
-- Backfill: las filas existentes pasan a pertenecer al mes en que corre esta migración,
-- preservando el comportamiento actual (que hoy suma "todas las filas" como si fueran "el mes").
UPDATE fixed_costs SET period = date_trunc('month', CURRENT_DATE)::date WHERE period IS NULL;
ALTER TABLE fixed_costs ALTER COLUMN period SET NOT NULL;
ALTER TABLE fixed_costs ADD CONSTRAINT fixed_costs_period_is_month_start
    CHECK (date_trunc('month', period)::date = period);

-- El mismo nombre puede repetirse mes a mes (ej. "Alquiler" todos los meses),
-- pero no dos veces en el mismo mes para el mismo usuario.
ALTER TABLE fixed_costs DROP CONSTRAINT fixed_costs_name_user_unique;
ALTER TABLE fixed_costs ADD CONSTRAINT fixed_costs_name_user_period_unique UNIQUE (user_id, name, period);

CREATE INDEX idx_fixed_costs_user_period ON fixed_costs (user_id, period);

-- -------------------------------------------------------------
-- employees: categoría (rol/departamento, informativa) + período
-- -------------------------------------------------------------
ALTER TABLE employees ADD COLUMN category VARCHAR(30);
UPDATE employees SET category = 'OTROS' WHERE category IS NULL;
ALTER TABLE employees ALTER COLUMN category SET NOT NULL;
ALTER TABLE employees ADD CONSTRAINT employees_category_check
    CHECK (category IN ('PRODUCCION', 'ADMINISTRACION', 'VENTAS', 'OTROS'));

ALTER TABLE employees ADD COLUMN period DATE;
UPDATE employees SET period = date_trunc('month', CURRENT_DATE)::date WHERE period IS NULL;
ALTER TABLE employees ALTER COLUMN period SET NOT NULL;
ALTER TABLE employees ADD CONSTRAINT employees_period_is_month_start
    CHECK (date_trunc('month', period)::date = period);

-- employees no tenía unique de nombre en V1/V2; con período agregamos
-- (user_id, name, period) para que "duplicar mes anterior" + edición manual
-- no produzca dos filas idénticas para el mismo empleado/mes.
ALTER TABLE employees ADD CONSTRAINT employees_name_user_period_unique UNIQUE (user_id, name, period);

CREATE INDEX idx_employees_user_period ON employees (user_id, period);
