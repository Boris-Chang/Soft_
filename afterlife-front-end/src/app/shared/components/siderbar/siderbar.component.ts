import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserInfo } from '../../models/user-info.model';
import { environment } from '../../../../environments/environment';

const Roles = 'role';

export interface DialogData {
  animal: 'panda' | 'unicorn' | 'lion';
  }
  /**
  * @title Injecting data when opening a dialog
  */

@Component({
  selector: 'app-siderbar',
  templateUrl: './siderbar.component.html',
  styleUrls: ['./siderbar.component.scss']
})
export class SiderbarComponent implements OnInit {

  //
  private host = environment.userUrl;
  user: UserInfo; 
  constructor(public dialog: MatDialog, private http: HttpClient) { 
    this.http.get<UserInfo>(this.host)
      .subscribe( result => {
        this.user = result;
        window.sessionStorage.setItem(Roles, this.user.roles);
      })
  }
  //
  openDialog1() {
    this.dialog.open(DialogDataExampleDialog, {
    data: {
    animal: 'panda',
    },
    });
    }
  ngOnInit(): void {
  }
  openDialog2() {
    this.dialog.open(DialogDataLeadsDialog, {
    data: {
    animal: 'panda',
    },
    });
    }
    
}
@Component({
  selector: 'dialog-data-example-dialog',
  templateUrl: 'contact.html',
  })
  export class DialogDataExampleDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}
  }
  //
@Component({
    selector: 'dialog-data-leads-dialog',
    templateUrl: 'leads.html',
    })
    export class DialogDataLeadsDialog {

    private host = environment.userUrl;
    user: UserInfo;  
    constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData, private http: HttpClient) {
      this.http.get<UserInfo>(this.host)
      .subscribe( result => {
        this.user = result;
      })
    }
 }
