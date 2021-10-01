import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-dialog-report',
  templateUrl: './dialog-report.component.html',
  styleUrls: ['./dialog-report.component.scss'],
})
export class DialogReportComponent implements OnInit {

  constructor(public dialog: MatDialog) {
    
    this.dataSourceOfgoodness = new MatTableDataSource;
    
    this.dataSourceofsins = new MatTableDataSource;
  }

  ngOnInit(): void {
    this.dataSourceOfgoodness.data = ELEMENT_DATA_goodness;
    this.dataSourceOfgoodness.sort = this.tableOneSort;

    this.dataSourceofsins.data = ELEMENT_DATA_sins;
    this.dataSourceofsins.sort = this.tableTwoSort;
  }
  //define to datasource of goodness
  dataSourceOfgoodness: MatTableDataSource<PeriodicElement_goodness>;
  displayedColumnsOfgoodness: string[] = ['id', 'kind', 'date'];
  @ViewChild('TableOneSort', {static: true}) tableOneSort: MatSort;

  //define to datasource of sins
  dataSourceofsins: MatTableDataSource<PeriodicElement_sins>; 
  displayedColumnsOfsins: string[] = ['id', 'kind', 'date'];
  @ViewChild('TableTwoSort', {static: true}) tableTwoSort: MatSort;

  applyFilterOne(filterValue: string) {
    this.dataSourceOfgoodness.filter = filterValue.trim().toLowerCase();
  }

  applyFilterTwo(filterValue: string) {
    this.dataSourceofsins.filter = filterValue.trim().toLowerCase();
  }

  //комментарии
  panelOpenState = false;

  //select of places
  placesControl = new FormControl();
  
  placesGroups: PlacesGroup[] = [
    {
    name: 'Небеса',
    places: [
    {value: 'Небеса-0', viewValue: 'Небеса-0'},
    {value: 'Небеса-1', viewValue: 'Небеса-1'},
    {value: 'Небеса-2', viewValue: 'Небеса-2'}
    ]
    },
    {
    name: 'Круг ада',
    places: [
    {value: 'Круг ада-3', viewValue: 'Круг ада-0'},
    {value: 'Круг ада-4', viewValue: 'Круг ада-1'},
    {value: 'Круг ада-5', viewValue: 'Круг ада-2'}
    ]
    }
  ];
}
//interface for select of places 
interface Places {
  value: string;
  viewValue: string;
  }
  interface PlacesGroup {
  disabled?: boolean;
  name: string;
  places: Places[];
}

//interface for report of goodness and sins
export interface PeriodicElement_goodness {
  id: number;
  kind: string;
  date: string;
  }
  const ELEMENT_DATA_goodness: PeriodicElement_goodness[] = [
  {kind: 'LOVE', id: 1, date: "2014-01-01"},
  {kind: 'AMBITION', id: 2, date: "2014-01-01"},
  {kind: 'TRIUMPH', id: 3, date: "2014-01-01"},
  {kind: 'THEOLOGY', id: 3, date: "2014-01-01"},
  {kind: 'REFORMISM', id: 4, date: "2014-01-01"},
/*   {kind: 'THEOLOGY', id: 5, date: "2014-01-01"},
  {kind: 'WISDOM', id: 6, date: "2014-01-01"},
  {kind: 'HOLINESS', id: 7, date: "2014-01-01"},
  {kind: 'DIVINITY', id: 8, date: "2014-01-01"},
  {kind: 'AMBITION', id: 9, date: "2014-01-01"}, */
  ];
  /**
  * @title Table that uses the recycle view repeater strategy.
  */

   export interface PeriodicElement_sins {
    id: number;
    kind: string;
    date: string;
    }
  const ELEMENT_DATA_sins: PeriodicElement_sins[] = [
  {kind: 'UNBAPTIZED', id: 1, date: "2014-01-01"},
  {kind: 'UNBAPTIZED', id: 2, date: "2014-01-01"},
  {kind: 'UNBAPTIZED', id: 3, date: "2014-01-01"}
  ];
  /**
  * @title Table that uses the recycle view repeater strategy.
  */