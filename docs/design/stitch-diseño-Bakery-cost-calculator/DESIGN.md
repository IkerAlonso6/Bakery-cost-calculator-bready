---
name: Artisan Harvest
colors:
  surface: '#f8f9ff'
  surface-dim: '#ccdbf3'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e6eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d5e3fc'
  on-surface: '#0d1c2e'
  on-surface-variant: '#54433a'
  inverse-surface: '#233144'
  inverse-on-surface: '#eaf1ff'
  outline: '#877369'
  outline-variant: '#dac2b6'
  surface-tint: '#934b19'
  primary: '#6c2f00'
  on-primary: '#ffffff'
  primary-container: '#8b4513'
  on-primary-container: '#ffc29f'
  inverse-primary: '#ffb68c'
  secondary: '#735c00'
  on-secondary: '#ffffff'
  secondary-container: '#fed65b'
  on-secondary-container: '#745c00'
  tertiary: '#424223'
  on-tertiary: '#ffffff'
  tertiary-container: '#5a5a38'
  on-tertiary-container: '#d3d1a7'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdbc9'
  primary-fixed-dim: '#ffb68c'
  on-primary-fixed: '#321200'
  on-primary-fixed-variant: '#753401'
  secondary-fixed: '#ffe088'
  secondary-fixed-dim: '#e9c349'
  on-secondary-fixed: '#241a00'
  on-secondary-fixed-variant: '#574500'
  tertiary-fixed: '#e6e5b9'
  tertiary-fixed-dim: '#cac99f'
  on-tertiary-fixed: '#1d1d03'
  on-tertiary-fixed-variant: '#484828'
  background: '#f8f9ff'
  on-background: '#0d1c2e'
  surface-variant: '#d5e3fc'
  profit-green: '#2D6A4F'
  loss-red: '#BC4749'
  indicator-mint: '#D8F3DC'
  surface-cream: '#FAF9F6'
typography:
  headline-lg:
    fontFamily: Literata
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Literata
    fontSize: 26px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Literata
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-sm:
    fontFamily: Literata
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Hanken Grotesk
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-sm:
    fontFamily: Hanken Grotesk
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  data-mono:
    fontFamily: Hanken Grotesk
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
    letterSpacing: 0.02em
  label-md:
    fontFamily: Hanken Grotesk
    fontSize: 13px
    fontWeight: '700'
    lineHeight: 16px
    letterSpacing: 0.05em
  label-sm:
    fontFamily: Hanken Grotesk
    fontSize: 11px
    fontWeight: '700'
    lineHeight: 14px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  unit: 8px
  container-max-width: 1280px
  gutter: 24px
  margin-mobile: 16px
  margin-desktop: 32px
---

## Brand & Style

The design system is crafted to evoke the warmth of a sun-drenched morning bakery and the meticulous precision of a master pastry chef. It balances "Modern Cafe" aesthetics with "Financial Utility," ensuring that complex cost calculations feel as inviting as a freshly baked loaf.

The visual direction follows a **Corporate / Modern** framework infused with **Tactile** elements. It utilizes a card-based architecture to organize recipes, ingredients, and margins into digestible units. The atmosphere is professional yet friendly, prioritizing high legibility for numbers and data without sacrificing the artisanal soul of the brand.

**Key Brand Attributes:**
- **Approachable Precision:** Professional tools that don't feel intimidating or cold.
- **Warm Reliability:** Trustworthy financial data delivered through a soft, organic lens.
- **Clarity & Efficiency:** Minimizing cognitive load in busy kitchen or office environments.

## Colors

The palette is rooted in the natural tones of the baking trade. **Warm Wood** (Primary) provides grounding and authority, used for primary actions and navigational structures. **Gold/Wheat** (Secondary) serves as the highlight color for interactive states and important callouts.

**Cream** acts as the foundation of the UI, replacing stark whites with a softer, more organic background. **Slate Gray** (Neutral) is used exclusively for text and secondary interface elements to ensure maximum readability for pricing and inventory data.

**Functional Colors:**
- **Mint Green / Profit Green:** Used for positive financial trends, profit margins, and successful status badges.
- **Loss Red:** Reserved for critical alerts where costs exceed sale prices or inventory is depleted.

## Typography

This design system uses a dual-type strategy. **Literata** (Serif) is used for headlines to bring a "storybook" or "artisanal menu" feel to the application, providing warmth and character. 

**Hanken Grotesk** (Sans-Serif) handles all functional data, body text, and labels. It was chosen for its exceptional clarity in numerical rendering—vital for a cost calculator—and its modern, sharp profile which balances the traditional feel of the serif.

All numerical data should use the `data-mono` style or a weight of 600+ to ensure profit/loss figures are immediately identifiable.

## Layout & Spacing

The layout utilizes a **fixed-width grid** for desktop (12 columns) and a **fluid grid** for mobile (4 columns). The rhythm is based on an 8px base unit.

**Layout Rules:**
- **Grid:** 12-column layout with 24px gutters.
- **Cards:** Content is grouped into logical cards. On desktop, financial summaries span 3-4 columns, while recipe lists span the full width.
- **White Space:** Generous padding within cards (24px to 32px) ensures the UI feels "airy" and approachable, preventing the financial data from feeling cramped.
- **Mobile Reflow:** Cards stack vertically. Forms transition from two-column layouts to single-column layouts for ease of input in kitchen environments.

## Elevation & Depth

Visual hierarchy is established through **Tonal Layers** and **Ambient Shadows**. 

The background uses the `surface-cream` color, while active cards use a pure `#FFFFFF`. To separate these surfaces, a soft, diffused shadow is applied (Y: 4px, Blur: 12px, Opacity: 0.05, Color: `primary-color-hex`). 

Interactive elements like "Calculate" buttons or "Add Ingredient" cards use a slightly higher elevation on hover to indicate tactility. Borders are used sparingly, primarily as low-contrast dividers (`1px solid` with 10% opacity of the primary color) rather than structural containers.

## Shapes

The design system adopts a **Rounded** shape language to reinforce the "friendly cafe" persona.

- **Standard Cards & Inputs:** 0.5rem (8px) corner radius.
- **Large Container Elements:** 1rem (16px) corner radius for main dashboard sections.
- **Badges & Chips:** 1.5rem (24px) for a full pill-shaped appearance, emphasizing their status as discrete metadata.

Sharp corners are avoided entirely to maintain the approachable, modern aesthetic.

## Components

### Buttons
- **Primary:** Solid `primary_color_hex` (Warm Wood) with white text. High-contrast, 0.5rem rounded corners.
- **Secondary:** Outlined `secondary_color_hex` (Gold) for "Edit" or "Cancel" actions.
- **Status Buttons:** Use `profit-green` for "Finalize Cost" or "Publish Product."

### Form Elements
- **Input Fields:** 0.5rem rounded, with a subtle Cream background. On focus, the border transitions to Gold.
- **Unit Selectors:** Dropdowns should clearly distinguish between "kg", "g", and "unit" using bold labels.

### Cards
- The central component of the UI. Cards should feature a 1px soft border and the standard `rounded-lg` radius. Content inside should be padded by at least 24px.

### Status Badges & Unit Chips
- **Profit Badge:** Pill-shaped, background `indicator-mint`, text `profit-green`.
- **Loss Badge:** Pill-shaped, background tinted red, text `loss-red`.
- **Unit Badge (e.g., "Kg", "Unit"):** Small, neutral gray background with `label-sm` typography to provide context without distracting from the main figures.

### Lists & Data Tables
- Rows should have ample vertical padding (16px) and subtle dividers. Alternating row colors (Cream and White) are recommended for long ingredient lists to assist with tracking.