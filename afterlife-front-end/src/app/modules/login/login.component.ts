import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  login:any = FormGroup;
  constructor(private fb:FormBuilder, private router:Router) { }

  ngOnInit(): void {
    this.login = this.fb.group({
      name:['', Validators.required],
      email:['', Validators.compose([Validators.required, Validators.email])]
    })
  }
  loginSubmit(data:any){
    //
    var users = new Map();
    users.set("ChangJiyuan", "cjynoodles@gmail.com");
    users.set("KhlopkovDmitry", "Khlopkov@gmail.com");
    users.set("BaevDmitry", "baev@gmail.com");

    if(data.name)
    {
      users.forEach((item:any) => {
        
      
      if(users.get(data.name) === data.email)
      {
          localStorage.setItem("IsLogged In", "true");
          this.router.navigate(['home']);
          console.log(data);
      }
      else
      {
          localStorage.clear();
      }
    }
    )
  }
 
  }
}
