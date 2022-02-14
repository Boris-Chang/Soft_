import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

import { map } from 'rxjs/operators';
import { AuthLoginInfo } from '../../modules/login/model/login-info';
import { JwtResponse } from '../models/jwt-response';
import { TokenStorageService } from './token-storage.service';
import { environment } from 'src/environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-type': 'application/json' })
};
const TOKEN_KEY = 'AuthToken';

@Injectable({
providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser: Observable<any>;

  private host = environment.loginUrl;
  
  constructor(private http: HttpClient,
    private tokenStorage: TokenStorageService) {
    this.currentUserSubject = new BehaviorSubject<any>(sessionStorage.getItem(TOKEN_KEY));
    this.currentUser = this.currentUserSubject.asObservable();
  }
    //login
    login(loginInfo: AuthLoginInfo)
    {
        return this.http.post<JwtResponse>(this.host, loginInfo, httpOptions)
        .pipe(map(data => {
          this.saveUserData(data);
          return data;
        }))
    }
    
    private saveUserData(data){
      this.tokenStorage.saveToken(data.token);
      this.tokenStorage.saveLogin(data.login);
      this.tokenStorage.saveAuthorities(data.authorities);
      this.currentUserSubject.next(data.token);
    }
  }
