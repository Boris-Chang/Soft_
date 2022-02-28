import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms'
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

import { SurveyApiService } from '../service/survey-api.service';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent implements OnInit {

  addSurveyForm: FormGroup = new FormGroup({});

  constructor(private formBuilder: FormBuilder,
              private surveyService: SurveyApiService,
              private _snackBar: MatSnackBar,
              private dialog: MatDialog) { }

  ngOnInit(): void {
    this.addSurveyForm = this.formBuilder.group({
      'title': new FormControl(''),
      'url': new FormControl(''),
      'addressee': new FormControl('')
    })
  }
  createSurvey(){
    this.surveyService.createSurvey(JSON.stringify(this.addSurveyForm.value))
    .subscribe( data => {
      this._snackBar.open("Survey Created successfully");
    }), err => {
      this._snackBar.open("Unable to create survey");
    }
  }

  closeDialog() {
    this.dialog.closeAll();
  }
}
