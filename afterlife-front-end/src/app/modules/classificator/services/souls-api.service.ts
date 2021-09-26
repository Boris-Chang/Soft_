import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { PagedResult } from "../../../shared/models";
import { ReportedSoul, Soul } from "../models";

@Injectable()
export class SoulsApiService {
  //TODO: move this to config, register as a service and inject it through constructor via Dependency Injection
  //https://stackoverflow.com/questions/43193049/app-settings-the-angular-way/43193574#43193574
  private readonly apiUrl = 'http://localhost:8080/api'

  constructor(private readonly http: HttpClient) {
  }

  public getAllSouls(pageNumber: number, pageSize: number): Observable<PagedResult<ReportedSoul>> {
    return this.http.get<PagedResult<ReportedSoul>>(
      `${this.apiUrl}/souls?page-number=${pageNumber}&page-size=${pageSize}`);
  }

  public getSoulById(soulId: number): Observable<Soul> {
    return this.http.get<Soul>(`${this.apiUrl}/souls/${soulId}`);
  }

}
