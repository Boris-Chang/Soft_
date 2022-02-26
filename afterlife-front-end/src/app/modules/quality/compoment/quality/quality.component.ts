import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';

import { Chart, registerables } from 'chart.js';

import { ResultSeries } from '../../models/resultSeries.model';
import { SeriesApiService } from '../../services';
import { Values } from '../../models/values.model';
import { Measurement } from "../../models/measurement.model";

const SeriesValues = 'SeriesValues';
const SeriesTitle = 'SeriesTitle';

@Component({
  selector: 'app-quality',
  templateUrl: './quality.component.html',
  styleUrls: ['./quality.component.scss']
})
export class QualityComponent implements OnInit {

  
  subscription: Subscription;
  constructor(private seriesApi: SeriesApiService,) {
    Chart.register(...registerables);
  }

  resultSeries: ResultSeries[];
  values: Values[];
  measurement: Measurement;
  chart: any;
  qualityTitle: any;
  captionForY: string;
  series: any;


  ngOnInit(): void {
    this.subscription = this.seriesApi.getSeriesBymeasurementId()
    .subscribe( data => {
      this.resultSeries = data;
      this.values = this.resultSeries[0].values;
      window.sessionStorage.setItem(SeriesValues, JSON.stringify(this.values));         
      this.qualityTitle = this.resultSeries[0].measurement.title;
      this.captionForY = this.resultSeries[0].measurement.captionForY;
      window.sessionStorage.setItem(SeriesTitle, JSON.stringify(this.qualityTitle));    
    });
      this.qualityTitle = window.sessionStorage.getItem(SeriesTitle);
      this.series = window.sessionStorage.getItem(SeriesValues);
      this.series = JSON.parse(this.series)

      var arr_series:any[] = new Array(this.series.size);
      arr_series = this.series;
      for (var i = 0; i < arr_series.length; i++)
      {
        arr_series[i] = this.series[i].value;
      }
      console.log(arr_series)
      //show Chart data
      this.chart = new Chart('quality', {
        type: 'line',
        data: {
          labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
          datasets: [
            {
              label: this.qualityTitle,
              data:  arr_series,
              borderWidth: 3,
              fill: false,
              backgroundColor: 'rgba(93, 175, 89, 0.1)',
              borderColor: '#3e95cd'
            }
          ]
        } 
      })
  }

}
