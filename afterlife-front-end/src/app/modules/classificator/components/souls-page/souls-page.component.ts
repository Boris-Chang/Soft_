import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";
import { PagedResult } from "../../../../shared/models";
import { ReportedSoul } from "../../models";
import { SoulsApiService } from "../../services";

@Component({
  selector: 'app-souls-page',
  templateUrl: './souls-page.component.html',
  styleUrls: ['./souls-page.component.scss']
})
export class SoulsPageComponent implements OnInit {
  public souls$: Observable<PagedResult<ReportedSoul>>

  constructor(private readonly soulsApiService: SoulsApiService) { }

  ngOnInit(): void {
    //TODO: add pagination
    this.souls$ = this.soulsApiService.getAllSouls(0, 100);
  }

}
