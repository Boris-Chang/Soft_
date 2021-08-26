import { Component, OnInit } from '@angular/core';
import { DialogOpenComponent } from 'src/app/shared/widgets/dialog-open/dialog-open.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-widget-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})

export class CardComponent implements OnInit {

  constructor(public dialog: MatDialog) {}
  openDialog() {
  this.dialog.open(DialogOpenComponent);
  }

  ngOnInit(): void {
  }

}

