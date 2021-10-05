import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { PagedResult } from "../../../shared/models";

@Injectable({
  providedIn: 'root'
})
export class SoulsReportApiService {
  //TODO: move this to config, register as a service and inject it through constructor via Dependency Injection
  //https://stackoverflow.com/questions/43193049/app-settings-the-angular-way/43193574#43193574
  private readonly apiUrl = 'http://localhost:8080/api'

  constructor(private readonly http: HttpClient) {
  }

  public setSinsReport(soulId: number,csvSinsReport: string) {
    return this.http.put(`${this.apiUrl}/souls/${soulId}/reports/sins`,csvSinsReport)
  }

  public setGoodnessReport(soulId: number,csvGoodnessReport: string) {
    return this.http.put(`${this.apiUrl}/souls/${soulId}/reports/goodness`,csvGoodnessReport)
  }

}
