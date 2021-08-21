import { Component, OnInit } from '@angular/core';

export interface PeriodicElement {
  name: string;
  position: number;
  date: string;
  status: string;
  }
  const ELEMENT_DATA: PeriodicElement[] = [
    {position: 1, name: 'Hydrogen', date: '09.12', status: 'H'},
    {position: 2, name: 'Helium', date: '01.22', status: 'He'},
    {position: 3, name: 'Lithium', date: '02.28', status: 'Li'},
    {position: 4, name: 'Beryllium', date: '09.15', status: 'Be'},
    {position: 5, name: 'Boron', date: '12.01', status: 'B'},
    {position: 6, name: 'Carbon', date: '11.29', status: 'C'},
    {position: 7, name: 'Nitrogen', date: '10.05', status: 'N'},
    {position: 8, name: 'Oxygen', date: '08.27', status: 'O'},
    {position: 9, name: 'Fluorine', date: '04.13', status: 'F'},
    {position: 10, name: 'Neon', date: '03.01', status: 'Ne'},
    ];

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss']
})
export class PostsComponent implements OnInit {

  displayedColumns: string[] = ['position', 'name', 'date', 'status'];
  dataSource = ELEMENT_DATA;

  constructor() { }

  ngOnInit(): void {
  }

}
