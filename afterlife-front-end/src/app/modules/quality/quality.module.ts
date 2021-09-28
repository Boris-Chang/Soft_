import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HighchartsChartModule } from 'highcharts-angular';
import { QualityComponent} from '../quality/compoment/quality/quality.component';

@NgModule({
  declarations: [
    QualityComponent
  ],
  imports: [
    CommonModule,
    HighchartsChartModule
  ],
  exports: [
    QualityComponent
  ]
})
export class QualityModule { }
