import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
//import { AuthService } from './auth/auth.service';
import { Observable } from 'rxjs';
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImp0aSI6IjEiLCJpc3MiOiJhZnRlcmxpZmUtYmFja2VuZCIsImlhdCI6MTY0MjYzMTk3NiwiZXhwIjoxNjQzMjM2Nzc2fQ.rW5molRKo3A8Fe1RoMTSVjgTVGyPAtj25WJNMSmepOIOfSrqeEXpmZE8j26wcWqgUDGoOPTwPCSSNGJgtCTvqg"}`
      }
    });
    return next.handle(request);
  }
}
