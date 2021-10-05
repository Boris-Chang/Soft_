import {Component, OnInit, AfterViewInit, ViewChild} from '@angular/core';
import { Observable } from "rxjs";
import { PagedResult } from "../../../../shared/models";
import { ReportedSoul } from "../../models";
import { SoulsApiService } from "../../services";
import {MatPaginator, PageEvent} from "@angular/material/paginator";


@Component({
  selector: 'app-souls-page',
  templateUrl: './souls-page.component.html',
  styleUrls: ['./souls-page.component.scss']
})
export class SoulsPageComponent implements OnInit {
  public souls$: Observable<PagedResult<ReportedSoul>>;
  public pageNumber = 0;
  public pageSize = 100;
  @ViewChild(MatPaginator) matPaginator: MatPaginator;
  constructor(private readonly soulsApiService: SoulsApiService) { }

  ngOnInit(): void {
    this.souls$ = this.soulsApiService.getAllSouls(this.pageNumber, this.pageSize);
    this.souls$.subscribe(val =>
    {
      this.setPageCount(this.calculatePageCount(val.totalCount));
    });
  }
  private setPageCount(pageCount: number) {
    this.matPaginator.length = pageCount;
  }

  onChangePage(pe: PageEvent){
    console.log(pe);
    this.pageSize = pe.pageSize;
    this.souls$ = this.soulsApiService.getAllSouls(pe.pageIndex,this.pageSize);
    this.souls$.subscribe(val =>
    {
      this.setPageCount(this.calculatePageCount(val.totalCount));
    });
  }

  private calculatePageCount(totalCount: number): number{
    let x = Math.trunc(totalCount / this.pageSize);
    if(totalCount==0)
      return 1;
    if(totalCount % this.pageSize!=0)
      return x+1;
    else
      return x;
  }


}
