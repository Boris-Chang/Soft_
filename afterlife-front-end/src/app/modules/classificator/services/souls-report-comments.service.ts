import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {ReportComment} from "../models/report-comment.model";

@Injectable({
  providedIn: 'root'
})
export class SoulsReportCommentsService {
  //TODO: move this to config, register as a service and inject it through constructor via Dependency Injection
  //https://stackoverflow.com/questions/43193049/app-settings-the-angular-way/43193574#43193574
  private readonly apiUrl = 'http://localhost:8080/api'

  constructor(private readonly http: HttpClient) {
  }

  public getAllCommentsBySoulId(soulId: number): Observable<ReportComment[]> {
    return this.http.get<ReportComment[]>(`${this.apiUrl}/souls/${soulId}/comments`);
  }

  public setCommentBySoulId(soulId: number,comment: ReportComment): Observable<ReportComment> {
    return this.http.post<ReportComment>(`${this.apiUrl}/souls/${soulId}/comments`,comment);
  }

}
