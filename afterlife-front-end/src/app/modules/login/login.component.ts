import { Component, OnInit } from '@angular/core';

import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { first } from 'rxjs/operators';
import { AuthService } from '../../shared/services/auth.service';
import { AuthLoginInfo } from './model/login-info';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  form: any = {}
  isLoginFailed = false;
  errorMessage = '';
  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
  }
  onSubmit() {
    this.authService.login(new AuthLoginInfo(this.form.login, this.form.password))
    .pipe(first()).subscribe(
      data => {
        this.isLoginFailed = false;
        this.router.navigateByUrl('/home');
      },
      error => {
        this.errorMessage = error.error.message;
        this.isLoginFailed = true;
      }
    )
  }
}
