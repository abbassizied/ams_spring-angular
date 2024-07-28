import { Routes } from '@angular/router';
import { ListProviderComponent } from './list-provider/list-provider.component';
import { AddProviderComponent } from './add-provider/add-provider.component';
import { UpdateProviderComponent } from './update-provider/update-provider.component';
import { AddArticleComponent } from './add-article/add-article.component';
import { ListArticleComponent } from './list-article/list-article.component';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { AuthGuardService } from './services/auth-guard.service';

export const routes: Routes = [
{ path: "", pathMatch: "full", redirectTo: "app-navbar" },
{ path: "listProvider", component: ListProviderComponent, canActivate:[AuthGuardService] },
{ path: "addProvider", component: AddProviderComponent, canActivate:[AuthGuardService] },
{ path: "updateProvider/:id", component: UpdateProviderComponent },
{ path: "addArticle", component: AddArticleComponent, canActivate:[AuthGuardService] },
{ path: "listArticle", component: ListArticleComponent, canActivate:[AuthGuardService] },
{ path: 'login', component: LoginComponent },
{ path: 'logout', component: LogoutComponent, canActivate:[AuthGuardService] },
];
