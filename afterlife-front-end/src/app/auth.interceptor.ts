import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor, HttpErrorResponse
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from './shared/services/token-storage.service';
import {catchError, tap} from "rxjs/operators";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private tokenStorage: TokenStorageService) {}
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${this.tokenStorage.getToken()}`
      }
    });
    return next.handle(request).pipe(
      tap(event => event, (err) => {
        if (err instanceof  HttpErrorResponse && err.status == 401) {
          this.tokenStorage.removeToken();
        }
      }));
  }
}
