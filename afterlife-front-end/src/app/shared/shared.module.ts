import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { SiderbarComponent } from './components/siderbar/siderbar.component';
import { MatDividerModule} from '@angular/material/divider';
import { MatToolbarModule } from '@angular/material/toolbar'
import { MatIconModule } from '@angular/material/icon'
import { MatButtonModule } from '@angular/material/button'
import { MatMenuModule } from '@angular/material/menu'
import { MatListModule } from '@angular/material/list'
import { FlexLayoutModule } from '@angular/flex-layout'
import { RouterModule } from '@angular/router';
import { QualitiesComponent } from './widgets/qualities/qualities.component';
import { HighchartsChartModule } from 'highcharts-angular';
import { CardComponent } from './widgets/card/card.component';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { DialogOpenComponent } from './widgets/dialog-open/dialog-open.component';
import { FileUploaderComponent } from './widgets/file-uploader/file-uploader.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AngularFileUploaderModule } from "angular-file-uploader";


@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    SiderbarComponent,
    QualitiesComponent,
    CardComponent,
    DialogOpenComponent,
    FileUploaderComponent
  ],
  imports: [
    CommonModule,
    MatDividerModule,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatListModule,
    FlexLayoutModule,
    RouterModule,
    HighchartsChartModule,
    MatTabsModule,
    MatDialogModule,
    MatInputModule,
    MatFormFieldModule,
    HttpClientModule,
    ReactiveFormsModule,
    AngularFileUploaderModule
  ],
  exports: [
    HeaderComponent,
    FooterComponent,
    SiderbarComponent,
    QualitiesComponent,
    CardComponent,
    FileUploaderComponent
  ]
})
export class SharedModule { }
