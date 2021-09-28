import { Component, OnInit } from '@angular/core';

import * as Highcharts from 'highcharts';


@Component({
  selector: 'app-quality',
  templateUrl: './quality.component.html',
  styleUrls: ['./quality.component.scss']
})
export class QualityComponent implements OnInit {


  //define to the отдель контроля кочества
  highcharts = Highcharts;

  chartOptions: Highcharts.Options = {
    title: {
      text: "Температура точка сбора"
    },
    xAxis: {
      title: {
        text: '9 круг ада'
      },
      categories: ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
    },
    yAxis: {
      title: {
        text: "Temprature"
      }
    },
    series: [{
      data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 24.4, 19.3, 16.0, 18.4, 17.9],
      type: 'spline'
    }],
    credits: {
      enabled: false
    },
    exporting: {
      enabled: true,
    }
  }

  constructor() { }

  ngOnInit(): void {
  }

}
