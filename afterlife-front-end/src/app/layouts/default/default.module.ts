import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ClassificatorModule } from "../../modules/classificator/classificator.module";

import { DefaultComponent } from './default.component';
import { DashboardComponent } from 'src/app/modules/dashboard/dashboard.component';
import { PostsComponent } from 'src/app/modules/posts/posts.component';

import { SharedModule } from 'src/app/shared/shared.module';

import { MatSidenavModule } from '@angular/material/sidenav';
import { MatDividerModule } from '@angular/material/divider';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { AngularFileUploaderModule } from "angular-file-uploader";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { VacancyModule } from 'src/app/modules/vacancy/vacancy.module';
import { QualityModule } from 'src/app/modules/quality/quality.module';
import { LoginComponent } from 'src/app/modules/login/login.component';
import { SurveyComponent } from 'src/app/modules/survey/survey.component';
import { DialogComponent } from 'src/app/modules/survey/dialog/dialog.component';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule, MAT_SNACK_BAR_DEFAULT_OPTIONS, MAT_SNACK_BAR_DEFAULT_OPTIONS_FACTORY } from '@angular/material/snack-bar';


@NgModule({
  declarations: [
    DefaultComponent,
    DashboardComponent,
    PostsComponent,
    LoginComponent,
    SurveyComponent,
    DialogComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    SharedModule,
    ClassificatorModule,
    VacancyModule,
    QualityModule,

    MatButtonModule,
    MatListModule,
    MatInputModule,
    MatSidenavModule,
    MatDividerModule,
    MatPaginatorModule,
    MatTableModule,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatFormFieldModule,
    MatSnackBarModule,
    AngularFileUploaderModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserModule,
    HttpClientModule
  ],
  providers: [
    {provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: {duration: 2500}}
  ]
})
export class DefaultModule {
}
