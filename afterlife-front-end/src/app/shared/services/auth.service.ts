import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs';

interface LoginResponse {
  access_token: string;
  data: any;
  name: string;
  status: string;
  message: string;
}

@Injectable({
providedIn: 'root'
})
export class AuthService {
  
  private _isLoggedIn$ = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this._isLoggedIn$.asObservable();
  
  constructor(
      private http: HttpClient, 
      private router: Router) 
      {
        const token = localStorage.getItem('profanis_auth');
        this._isLoggedIn$.next(!!token);
      }
     resp: LoginResponse;
      //API path
      basePath = 'http://localhost:8080/api/sign-in';

    //login
    login(username: string, password: string)
    {
      return this.http.post(this.basePath, { username, password }).pipe(
        tap((response: any) => {
          this._isLoggedIn$.next(true);
          localStorage.setItem('profanis_auth', response.token);
        })
      );
    }
  }