import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from 'src/app/shared/shared.module';

import { MatTableModule } from "@angular/material/table";
import { MatDialogModule } from "@angular/material/dialog";
import { MatListModule } from '@angular/material/list'
import { MatButtonModule } from '@angular/material/button'
import { MatDividerModule } from '@angular/material/divider';
import { MatCardModule } from '@angular/material/card';
import { MatSortModule } from '@angular/material/sort';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';

import { SoulsPageComponent, SoulsTableComponent } from './components';
import { SoulsApiService } from "./services";
import { DialogReportComponent } from './components/dialog-report/dialog-report.component';

@NgModule({
  declarations: [
    SoulsTableComponent,
    SoulsPageComponent,
    DialogReportComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    
    MatTableModule,
    MatDialogModule,
    MatListModule,
    MatButtonModule,
    MatDividerModule,
    MatCardModule,
    MatSortModule,
    MatExpansionModule,
    MatInputModule,
    MatSelectModule,
    FormsModule,
    ReactiveFormsModule,
    MatPaginatorModule
  ],
  providers: [
    SoulsApiService
  ],
  exports: [
    SoulsPageComponent
  ]
})
export class ClassificatorModule { }
