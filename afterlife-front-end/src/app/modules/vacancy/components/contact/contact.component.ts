import { Component, OnInit, Inject } from '@angular/core';

import { FormGroup } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Validators } from '@angular/forms';
import { ContactService } from '../../services/vacancy-api.service'

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss']
})
export class ContactComponent implements OnInit {

  public _contactForm: FormGroup;

  constructor(private _formBuilder: FormBuilder,
  private dialogRef: MatDialogRef<ContactComponent>,
  private _contactService: ContactService,
  @Inject(MAT_DIALOG_DATA) public data: any) { }

  onNoClick(): void {
    this.dialogRef.close();
   }

  ngOnInit() {
    this._contactForm = this._formBuilder.group({
      ID: [this.data.ID],
      FirstName: [ this.data.FirstName, [Validators.required]],
      LastName: [ this.data.LastName, [Validators.required]],
      Contact: [ this.data.Contact, [Validators.required]],
      Email: [ this.data.Email , [Validators.required]],
    });
  }

  onSubmit() {
    if (isNaN(this.data.ID)) {
      this._contactService.addContact(this._contactForm.value);
      this.dialogRef.close();
    } else {
      this._contactService.editContact(this._contactForm.value);
      this.dialogRef.close();
    }
  }

}
