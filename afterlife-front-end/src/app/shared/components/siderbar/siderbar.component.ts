import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MAT_DIALOG_DATA} from '@angular/material/dialog';
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

  constructor(public dialog: MatDialog) { }
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
    constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}
 }
