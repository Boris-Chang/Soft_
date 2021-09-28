import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent implements OnInit {

  constructor(public dialog: MatDialog, private http: HttpClient) {}

  closeDialog() {
    this.dialog.closeAll();
  }
  onSubmit(data: any){
    this.http.post('http://localhost:3000/posts', data)
  }

  ngOnInit(): void {
  }


}
