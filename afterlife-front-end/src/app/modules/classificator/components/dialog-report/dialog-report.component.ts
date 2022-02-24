import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { FormControl } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { environment } from 'src/environments/environment'
import { ReportComment, Soul } from '../../models';
import { Argue } from '../../models/argue.model';
import { MarkChange } from '../../models/mark-change.model';
import { ChangeDecision } from '../../models/chang-decision.model';
import { Role } from 'src/app/shared/enum/role.enum';
import { GoodnessEvidence } from '../../models/goodness-evidence.model';
import { SinEvidence } from '../../models';
import { result } from '../../models/result.model';
import { ResultAll } from '../../models/resultAll.model';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-type': 'application/json' })
};

const Roles = 'role';
const GoodnessKind = 'goodnessKind';
const SinsKin = 'sinsKind';

@Component({
  selector: 'app-dialog-report',
  templateUrl: './dialog-report.component.html',
  styleUrls: ['./dialog-report.component.scss'],
})
export class DialogReportComponent implements OnInit {
  public soulId: number;
  private host = environment.soulUrl;
  soul: Soul;
  commentGroups: ReportComment[];
  resultArgue: Argue;
  resultMark: MarkChange;
  id: number;
  text: string;
  createdAt: string;
  sectionIndex: number;
  afterworldKind: string;
  resultChange: ChangeDecision;
  goodnessEvidences: GoodnessEvidence[];
  sinsEvidences: SinEvidence[];
  goodnessEvidence: string;
  sinsEvidence: string;
  resultAll: ResultAll;
  results: result[];
  
  constructor(public dialog: MatDialog, @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpClient) {
    this.dataSourceOfgoodness = new MatTableDataSource;

    this.dataSourceofsins = new MatTableDataSource;
  }
  
  //
  onSubmit(data: any)
  {
    this.http.post(`${this.host}/${this.soulId}/comments`, JSON.stringify(data), httpOptions)
    .subscribe(( result ) => {
      console.warn("result", result);
    })
    console.warn(data);
  }

  ngOnInit(): void {
    this.soulId = this.data as number;
    this.dataSourceOfgoodness.data = ELEMENT_DATA_goodness;
    this.dataSourceOfgoodness.sort = this.tableOneSort;

    this.dataSourceofsins.data = ELEMENT_DATA_sins;
    this.dataSourceofsins.sort = this.tableTwoSort;
    
    this.http.get<Soul>(`${this.host}/${this.soulId}`)
    .subscribe( result => {
      this.soul = result;
    })
    
    this.http.get<ReportComment[]>(`${this.host}/${this.soulId}/comments`)
    .subscribe( result => {
      this.commentGroups = result;
    })
    
    this.http.get<Argue>(`${this.host}/${this.soulId}/argue`)
    .subscribe( result => {
      this.resultArgue = result;
    })
    //get report of sins and goodness
    this.http.get<ResultAll>('http://localhost:8080/api/souls?page-number=0&page-size=100')
    .subscribe( result => {
      this.resultAll = result;

      for (var i in this.resultAll.results)
      {
        if (this.resultAll.results[i].soul.id == this.soulId)
        {
          this.goodnessEvidences = this.resultAll.results[i].goodnessReport.goodnessEvidences;
          // console.log(this.goodnessEvidences);
          window.sessionStorage.setItem(GoodnessKind, JSON.stringify(this.goodnessEvidences));
          this.sinsEvidences = this.resultAll.results[i].sinsReport.sins;
          window.sessionStorage.setItem(SinsKin, JSON.stringify(this.sinsEvidences));         
        }
      }
      this.goodnessEvidence = window.sessionStorage.getItem(GoodnessKind);    
      this.goodnessEvidences = JSON.parse(this.goodnessEvidence);
      // console.log(this.goodnessEvidences);
      this.sinsEvidence = window.sessionStorage.getItem(SinsKin); 
      this.sinsEvidences = JSON.parse(this.sinsEvidence);
    })
  }
  
  //post mark
  postMark() {
    return this.http.post<MarkChange>(`${this.host}/${this.soulId}/argue`, httpOptions)
        .subscribe((result) => {
          console.warn(result);
          this.resultMark = result;
        })
  }
  
  //change-decision
  changeDecision(data: any) {
    return this.http.post<ChangeDecision>(`${this.host}/${this.soulId}/change-decision`,  JSON.stringify(data), httpOptions)
        .subscribe((result) => {
          console.warn(result);
          this.resultChange = result;
        })
  }
  
  //
  get isGodOrDevil(): boolean {
    return this.getUserRole() === Role.God || this.getUserRole() === Role.Devil;
  }
  //
  get isAdvocateOrProsecutor(): boolean {
    return this.getUserRole() === Role.Heaven_Advocate || this.getUserRole() === Role.Heaven_Prosecutor;
  }
  //
  getUserRole(): string{
    return window.sessionStorage.getItem(Roles);
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
