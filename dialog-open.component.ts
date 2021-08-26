import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-dialog-open',
  templateUrl: './dialog-open.component.html',
  styleUrls: ['./dialog-open.component.scss']
})


export class DialogOpenComponent implements OnInit {

  constructor(public dialog: MatDialog) {}

    closeDialog() {
    this.dialog.closeAll();
  }

  ngOnInit(): void {
  }

}
