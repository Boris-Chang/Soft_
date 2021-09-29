import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HighchartsChartModule } from 'highcharts-angular';
import { QualityComponent} from '../quality/compoment/quality/quality.component';

import { SeriesApiService } from '../quality/services/series-api.service';
import { MeasurementPageComponent } from './compoment/measurement-page/measurement-page.component';

@NgModule({
  declarations: [
    QualityComponent,
    MeasurementPageComponent
  ],
  imports: [
    CommonModule,
    HighchartsChartModule
  ],
  providers: [
    SeriesApiService
  ],
  exports: [
    QualityComponent
  ]
})
export class QualityModule { }
