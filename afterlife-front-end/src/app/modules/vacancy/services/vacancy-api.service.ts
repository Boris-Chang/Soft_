import { Injectable } from '@angular/core';
import { Contact } from '../models/contact';

@Injectable()
export class ContactService {

  _contactList: Contact[] = [];

  constructor() { }

  addContact(contact: Contact) {
    contact.ID = this._contactList.length + 1;
    this._contactList.push(contact);
  }

  editContact(contact: Contact) {
    const index = this._contactList.findIndex(c => c.ID === contact.ID);
    this._contactList[index] = contact;
  }

  deleteContact(id: number) {
    const contact = this._contactList.findIndex(c => c.ID === id);
    this._contactList.splice(contact, 1);
  }

  getAllContacts() {
    return this._contactList;
  }
}
