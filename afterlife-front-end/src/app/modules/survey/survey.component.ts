import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';

import { SurveyApiService } from './service/survey-api.service';

import { Result } from './model/result.model';
import { SurveyInfo } from './model/surveyInfo.model';
import { DialogComponent } from './dialog/dialog.component';

const Surveys = 'Surveys';

@Component({
  selector: 'app-survey',
  templateUrl: './survey.component.html',
  styleUrls: ['./survey.component.scss']
})
export class SurveyComponent implements OnInit {

  subscription: Subscription;
  constructor(private surveyApi: SurveyApiService, public dialog: MatDialog) { }
 
  openDialog() {
    this.dialog.open(DialogComponent);    
  }
  

  resultPage: Result;
  surveys: SurveyInfo[];
  survey: string;

  ngOnInit(): void {
    this.subscription = this.surveyApi.getResultPageSurveys()
    .subscribe( data => {
      this.resultPage = data;
      console.log(this.resultPage);
      this.surveys = this.resultPage.results;
      window.sessionStorage.setItem(Surveys, JSON.stringify(this.surveys));
      console.log(this.surveys);
    })
    this.survey = window.sessionStorage.getItem(Surveys);
    this.surveys = JSON.parse(this.survey);
    // console.log(this.surveys);
  }
  sideBarOpen = true;
  sideBarToggler() {
    this.sideBarOpen = !this.sideBarOpen;
  }
}
