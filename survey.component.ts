import { Component, OnInit } from '@angular/core';

export interface PeriodicElement {
  poll: string;
  position: number;
  name: string;
  for: string;
  }
  const ELEMENT_DATA: PeriodicElement[] = [
  {position: 1, poll: 'Опроса качества 1', name: 'Hydrogen', for: 'Пыточники'},
  {position: 2, poll: 'Опроса качества 2', name: 'Helium', for: 'Грешники'},
  {position: 3, poll: 'Опроса качества 3', name: 'Lithium', for: 'Пыточники, Грешники'},
  {position: 4, poll: 'Опроса качества 4', name: 'Bob', for: 'Пыточники'},
  {position: 5, poll: 'Опроса качества 5', name: 'Tom', for: 'Грешники'},
  {position: 6, poll: 'Опроса качества 6', name: 'James', for: 'Грешники'},
  {position: 7, poll: 'Опроса качества 7', name: 'Jim', for: 'Пыточники'},
  {position: 8, poll: 'Опроса качества 8', name: 'Tomas', for: 'Пыточники'},
  {position: 9, poll: 'Опроса качества 9', name: 'Kobe', for: 'Пыточники'},
  {position: 10, poll: 'Опроса качества 10', name: 'Even', for: 'Пыточники'},
  ];

@Component({
  selector: 'app-survey',
  templateUrl: './survey.component.html',
  styleUrls: ['./survey.component.scss']
})
export class SurveyComponent implements OnInit {

  displayedColumns: string[] = ['position', 'poll', 'name', 'for'];
  dataSource = ELEMENT_DATA;

  constructor() { }

  ngOnInit(): void {
  }

}
