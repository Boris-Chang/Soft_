import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";
import { PagedResult } from "../../../../shared/models";
import { Measurement } from '../../models/measurement.model';
import { Series } from "../../models/series.model";
import { SeriesApiService } from "../../services/series-api.service";

@Component({
  selector: 'app-measurement-page',
  templateUrl: './measurement-page.component.html',
  styleUrls: ['./measurement-page.component.scss']
})
export class MeasurementPageComponent implements OnInit {

  public measurements$: Observable<PagedResult<Measurement>>
  constructor(private readonly seriesApiService: SeriesApiService) { }

  ngOnInit(): void {
    //TODO: add pagination
    this.measurements$ = this.seriesApiService.getAllMeasurement(0, 100);
  }

}
