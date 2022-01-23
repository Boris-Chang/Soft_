import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ContactService } from '../../../services/vacancy-api.service';
import { ContactComponent } from '../../../components/contact/contact.component';

@Component({
  selector: 'app-tab',
  templateUrl: './tab.component.html',
  styleUrls: ['./tab.component.scss']
})
export class TabComponent implements OnInit {

   isPopupOpened = true;

  constructor(public dialog: MatDialog, private _contactService?: ContactService) {}
   

  ngOnInit(): void {
  }
  get ContactList() {
    return this._contactService.getAllContacts();
  }

  addContact() {
    this.isPopupOpened = true;
    const dialogRef = this.dialog.open(ContactComponent, {
      data: {}
    });


    dialogRef.afterClosed().subscribe(result => {
      this.isPopupOpened = false;
    });
  }

  editContact(id: number) {
    this.isPopupOpened = true;
    const contact = this._contactService.getAllContacts().find(c => c.ID === id);
    const dialogRef = this.dialog.open(ContactComponent, {
      data: contact
    });


    dialogRef.afterClosed().subscribe(result => {
      this.isPopupOpened = false;
    });
  }

  deleteContact(id: number) {
    this._contactService.deleteContact(id);
  }

}
