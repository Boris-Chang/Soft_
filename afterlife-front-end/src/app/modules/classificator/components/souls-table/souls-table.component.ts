import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from "@angular/material/table";
import { ReportedSoul } from "../../models";

@Component({
  selector: 'app-souls-table',
  templateUrl: './souls-table.component.html',
  styleUrls: ['./souls-table.component.scss']
})
export class SoulsTableComponent implements OnInit {
  public readonly displayedColumns = ['name', 'dateOfDeath', 'status']

  @Input()
  public set souls(val: ReportedSoul[]) {
    console.debug('set');
    this.datasource.data = val;
  };

  public get souls(): ReportedSoul[] {
    return this.datasource.data;
  }

  public datasource = new MatTableDataSource<ReportedSoul>();

  constructor() { }

  ngOnInit(): void {
  }

  public asTypedModel(o: any): ReportedSoul {
    return o;
  }
}
