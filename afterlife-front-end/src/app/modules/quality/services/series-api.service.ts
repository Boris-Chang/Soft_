import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PagedResult } from "../../../shared/models";
import { Measurement } from '../models/measurement.model';
import { Series } from '../models/series.model';
import { ResultSeries } from '../models/resultSeries.model';

@Injectable({
    providedIn: 'root'
  })
  export class SeriesApiService {
    private readonly apiUrl = 'http://localhost:8080/api'
  
    constructor(private readonly http: HttpClient) {}

    public getSeriesBymeasurementId(): Observable<ResultSeries[]> {
      return this.http.get<ResultSeries[]>(`${this.apiUrl}/measurements/2/series`);
    }

    public getAllMeasurement(pageNumber: number, pageSize: number): Observable<PagedResult<Measurement>> {
      return this.http.get<PagedResult<Measurement>>(
        `${this.apiUrl}/measurements?page-number=${pageNumber}&page-size=${pageSize}`);
    }

    public getMeasurementById(measurementId: number): Observable<Measurement> {
      return this.http.get<Measurement>(`${this.apiUrl}/measurements/${measurementId}`);
    }

  }
