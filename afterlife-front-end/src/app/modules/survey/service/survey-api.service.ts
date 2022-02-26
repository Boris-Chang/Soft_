import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';

import { Result } from '../model/result.model';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-type': 'application/json' })
};

@Injectable({
    providedIn: 'root'
  })
  export class SurveyApiService {
    private readonly resultPageUrl = 'http://localhost:8080/api/surveys?page-number=0&page-size=100';
    evn = environment;

    constructor(private readonly http: HttpClient) {}

    public getResultPageSurveys(): Observable<Result>
    {
      return this.http.get<Result>(this.resultPageUrl);
    }
    //
    createSurvey(surveyObj: any)
    {
      return this.http.post(this.evn.baseUrl + 'surveys', surveyObj, httpOptions);
    }
  }