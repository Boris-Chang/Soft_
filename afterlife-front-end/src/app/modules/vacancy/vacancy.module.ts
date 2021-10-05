import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';

import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AngularFileUploaderModule } from 'angular-file-uploader';
import { MatButtonModule } from '@angular/material/button'
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';

import { TabComponent } from '../vacancy/components/tab/tab/tab.component';
import { DialogComponent } from '../vacancy/components/dialog/dialog.component';

@NgModule({
  declarations: [
    TabComponent,
    DialogComponent
    ],
  imports: [
    CommonModule,
    MatTabsModule,
    MatButtonModule,
    MatDividerModule,
    MatFormFieldModule,
    AngularFileUploaderModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserModule,
    MatRadioModule,
    MatSelectModule,
    MatIconModule,
    SharedModule
  ],
  exports: [
    TabComponent,
    DialogComponent
  ]
})
export class VacancyModule { }
