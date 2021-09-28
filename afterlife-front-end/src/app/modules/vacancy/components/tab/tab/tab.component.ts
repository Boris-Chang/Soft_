import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../../dialog/dialog.component';

@Component({
  selector: 'app-tab',
  templateUrl: './tab.component.html',
  styleUrls: ['./tab.component.scss']
})
export class TabComponent implements OnInit {

  constructor(public dialog: MatDialog) {}
    openDialog()
    {
      this.dialog.open(DialogComponent);
    }

  ngOnInit(): void {
  }

}
