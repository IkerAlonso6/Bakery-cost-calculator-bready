export interface NavItem {
  path: string;
  label: string;
  icon: string;
}

export const NAV_ITEMS: NavItem[] = [
  { path: '/dashboard', label: 'Dashboard', icon: 'dashboard' },
  { path: '/inputs', label: 'Insumos', icon: 'inventory_2' },
  { path: '/recipes', label: 'Recetas', icon: 'menu_book' },
  { path: '/products', label: 'Productos', icon: 'bakery_dining' },
  { path: '/fixed-costs', label: 'Costos Fijos', icon: 'receipt_long' },
  { path: '/employees', label: 'Empleados', icon: 'groups' },
  { path: '/cost-settings', label: 'Configuración de Costeo', icon: 'settings' },
];
