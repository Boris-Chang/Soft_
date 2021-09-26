import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from "@angular/material/table";
import { SoulsPageComponent, SoulsTableComponent } from './components';
import { SoulsApiService } from "./services";


@NgModule({
  declarations: [
    SoulsTableComponent,
    SoulsPageComponent
  ],
  imports: [
    CommonModule,
    MatTableModule
  ],
  providers: [
    SoulsApiService
  ],
  exports: [
    SoulsPageComponent
  ]
})
export class ClassificatorModule { }
