import { HttpClient, HttpEvent, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { PagedResult } from "../../../shared/models";

@Injectable({
  providedIn: 'root'
})
export class SoulsReportApiService {
  //TODO: move this to config, register as a service and inject it through constructor via Dependency Injection
  //https://stackoverflow.com/questions/43193049/app-settings-the-angular-way/43193574#43193574
  private readonly apiUrl = 'http://localhost:8080/api';
  private baseUrl = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {
  }

  public setSinsReport(soulId: number,csvSinsReport: FormData) : Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/souls/${soulId}/reports/sins`,csvSinsReport);
  }

  public setGoodnessReport(soulId: number,csvGoodnessReport: FormData) : Observable<any>{
    return this.http.put<any>(`${this.apiUrl}/souls/${soulId}/reports/goodness`,csvGoodnessReport);
  }
  
  uploadSinsReport(soulId: number, csvSinsReport: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', csvSinsReport);
    const req = new HttpRequest('PUT', `${this.baseUrl}/souls/${soulId}/reports/sins`, formData, {
      reportProgress: true,
      responseType: 'json'
    });
    return this.http.request(req);
  }

  uploadGoodnessReport(soulId: number, csvGoodnessReport: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', csvGoodnessReport);
    const req = new HttpRequest('PUT', `${this.baseUrl}/souls/${soulId}/reports/goodness`, formData, {
      reportProgress: true,
      responseType: 'json'
    });
    return this.http.request(req);
  }

}
