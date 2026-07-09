import { Routes } from '@angular/router';
import { RecipeListComponent } from './recipe-list/recipe-list.component';
import { RecipeDetailComponent } from './recipe-detail/recipe-detail.component';

export const RECIPES_ROUTES: Routes = [
  { path: '', component: RecipeListComponent },
  { path: ':id', component: RecipeDetailComponent },
];
