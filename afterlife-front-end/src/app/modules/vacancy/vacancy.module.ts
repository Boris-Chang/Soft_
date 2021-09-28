import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { AngularFileUploaderModule } from 'angular-file-uploader';
import { MatButtonModule } from '@angular/material/button'

import { TabComponent } from '../vacancy/components/tab/tab/tab.component';
import { DialogComponent } from '../vacancy/components/dialog/dialog.component';
import { FileUploaderComponent } from './components/file-uploader/file-uploader.component';

@NgModule({
  declarations: [
    TabComponent,
    DialogComponent,
    FileUploaderComponent
    ],
  imports: [
    CommonModule,
    MatTabsModule,
    MatButtonModule,
    MatDividerModule,
    MatFormFieldModule,
    AngularFileUploaderModule
  ],
  exports: [
    TabComponent,
    DialogComponent,
    FileUploaderComponent
  ]
})
export class VacancyModule { }
