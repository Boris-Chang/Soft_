import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { LoginComponent } from './modules/login/login.component';
import { PostsComponent } from './modules/posts/posts.component';
import { SurveyComponent } from './modules/survey/survey.component';

const routes: Routes = [
  {
    path: 'login',
  component: LoginComponent
  },
  {
    path: 'home',
    component: DashboardComponent, //canActivate:[AuthGuard]
  },
  {
    path: 'posts',
    component: PostsComponent
  },
  {
    path: 'survey',
    component: SurveyComponent
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: "full"
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
