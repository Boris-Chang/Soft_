import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from "@angular/material/table";
import { ReportedSoul } from "../../models";

import { DialogReportComponent } from '../dialog-report/dialog-report.component';
import { MatDialog } from '@angular/material/dialog';


@Component({
  selector: 'app-souls-table',
  templateUrl: './souls-table.component.html',
  styleUrls: ['./souls-table.component.scss']
})
export class SoulsTableComponent implements OnInit {
  public readonly displayedColumns = ['id','name', 'dateOfDeath', 'status'];

  @Input()
  public set souls(val: ReportedSoul[]) {
    console.debug('set');
    this.datasource.data = val;
  };

  public get souls(): ReportedSoul[] {
    return this.datasource.data;
  }
  public datasource = new MatTableDataSource<ReportedSoul>();



  constructor(public dialog: MatDialog) {

  }
  openReportDialog(reportedSoul: ReportedSoul) {
    const dialogRef = this.dialog.open(DialogReportComponent,{data: reportedSoul.soul.id});
    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
      });

  }



  ngOnInit(): void {
  }

  public asTypedModel(o: any): ReportedSoul {
    return o;
  }
}
