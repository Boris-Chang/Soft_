import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjaGFuZyIsImp0aSI6IjIiLCJpc3MiOiJhZnRlcmxpZmUtYmFja2VuZCIsImlhdCI6MTY0MzA1MTYyMiwiZXhwIjoxNjQzNjU2NDIyfQ.bv9mYbWpdK07nQyi0Sc_nUovECx6kqWLY_4hybzET6goEFaE8sFXj30fiNnKWYRdd6_PrF6lhoXEeowRFBv2tQ'}`
      }
    });
    return next.handle(request);
  }
}
