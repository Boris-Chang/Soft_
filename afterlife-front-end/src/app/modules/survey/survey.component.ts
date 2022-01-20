import { Component, OnInit } from '@angular/core';
import {DataSource} from '@angular/cdk/collections';
import {Observable, ReplaySubject} from 'rxjs';

export interface PeriodicElement {
  name: string;
  position: number;
  weight: string;
  symbol: string;
}
const ELEMENT_DATA: PeriodicElement[] = [
  {position: 1, name: 'Опрос качества', weight: 'googlesheet', symbol: 'Пыточника'},
  {position: 2, name: 'Опрос качества', weight: 'googlesheet', symbol: 'Грешника'},
  {position: 3, name: 'Опрос качества', weight: 'googlesheet', symbol: 'Пыточника'},
  {position: 4, name: 'Опрос качества', weight: 'googlesheet', symbol: 'Грешника'}
];
  /**
  * @title Adding and removing data when using an observable-based datasource.
  */
@Component({
  selector: 'app-survey',
  templateUrl: './survey.component.html',
  styleUrls: ['./survey.component.scss']
})
export class SurveyComponent implements OnInit {
  displayedColumns: string[] = ['position', 'name', 'weight', 'symbol'];
  dataToDisplay = [...ELEMENT_DATA];
  dataSource = new ExampleDataSource(this.dataToDisplay);
  addData() {
  const randomElementIndex = Math.floor(Math.random() * ELEMENT_DATA.length);
  this.dataToDisplay = [...this.dataToDisplay, ELEMENT_DATA[randomElementIndex]];
  this.dataSource.setData(this.dataToDisplay);
  }
  removeData() {
  this.dataToDisplay = this.dataToDisplay.slice(0, -1);
  this.dataSource.setData(this.dataToDisplay);
  }
  constructor() { }

  ngOnInit(): void {
  }
  sideBarOpen = true;
  sideBarToggler() {
    this.sideBarOpen = !this.sideBarOpen;
  }
}
class ExampleDataSource extends DataSource<PeriodicElement> {
  private _dataStream = new ReplaySubject<PeriodicElement[]>();
  constructor(initialData: PeriodicElement[]) {
  super();
  this.setData(initialData);
  }
  connect(): Observable<PeriodicElement[]> {
  return this._dataStream;
  }
  disconnect() {}
  setData(data: PeriodicElement[]) {
  this._dataStream.next(data);
  }
}
