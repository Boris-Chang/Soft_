import { Component, OnInit } from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { SoulsApiService } from 'src/app/modules/fileService/souls-api.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-posts',
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.scss'],
  animations: [
    trigger('detailExpand', [
    state('collapsed', style({height: '0px', minHeight: '0'})),
    state('expanded', style({height: '*'})),
    transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
    ]
})
export class PostsComponent implements OnInit {

  dataSource = ELEMENT_DATA;
  columnsToDisplay = ['name', 'date', 'status', 'position'];
  expandedElement!: PeriodicElement | null;

  constructor(private soulsapiService : SoulsApiService) {}

  filenames: string[] = [];
  fileStatus = { status: '', requestType: '', percent: 0 };
  //define function to upload files
  onUploadFiles(files : File[]): void {
    const formData = new FormData();
    for (const file of files) { formData.append('files', file, file.name); }
    this.soulsapiService.upload(formData).subscribe(
      event => {
        console.log(event);
        this.resportProgress(event);
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    );
  }

  private resportProgress(httpEvent: HttpEvent<string[]>) {
    switch (httpEvent.type) {
      case HttpEventType.UploadProgress:
        this.updataStatus(httpEvent.loaded, httpEvent.total!, 'Uploading');
        break;
      case HttpEventType.ResponseHeader:
        console.log('Header returned' ,httpEvent);
        break;
      case HttpEventType.Response:
        if (httpEvent.body instanceof Array) {
          this.fileStatus.status = 'done';
          for (const filename of httpEvent.body) {
            this.filenames.unshift(filename);
          }
        }
        else {
          saveAs(new File([httpEvent.body!], httpEvent.headers.get('File-Name')!, 
                  {type: `${httpEvent.headers.get('Content-Type')};charset=utf-8`}));
        }
        this.fileStatus.status = 'done';
        break;
        default:
          console.log(httpEvent);
          break;
    }
  }
  private updataStatus(loaded: number, total: number, requestType: string) {
    throw new Error('Method not implemented.');
    this.fileStatus.status = 'progress';
    this.fileStatus.requestType = requestType;
    this.fileStatus.percent = Math.round(100 * loaded / total);
  }
  ngOnInit(): void {}
  //default UI sideBar
  sideBarOpen = true;
  sideBarToggler() {
    this.sideBarOpen = !this.sideBarOpen;
  }
 
}

export interface PeriodicElement {
  name: string;
  position: number;
  date: string;
  status: string;
  description: string;
  }
  const ELEMENT_DATA: PeriodicElement[] = [
  {
  position: 1,
  name: 'Hydrogen',
  date: '09.01.2019',
  status: 'Ожидает досье',
  description: `Hydrogen is a chemical element with symbol H and atomic number 1. With a standard
  atomic weight of 1.008, hydrogen is the lightest element on the periodic table.`
  }, {
  position: 2,
  name: 'Helium',
  date: '02.11.2020',
  status: 'Ожидает досье',
  description: `Helium is a chemical element with symbol He and atomic number 2. It is a
  colorless, odorless, tasteless, non-toxic, inert, monatomic gas, the first in the noble gas
  group in the periodic table. Its boiling point is the lowest among all the elements.`
  }, {
  position: 3,
  name: 'Lithium',
  date: '01.02.2021',
  status: 'Ожидает досье',
  description: `Lithium is a chemical element with symbol Li and atomic number 3. It is a soft,
  silvery-white alkali metal. Under standard conditions, it is the lightest metal and the
  lightest solid element.`
  }, {
  position: 4,
  name: 'Beryllium',
  date: '04.01.2013',
  status: 'Ожидает досье',
  description: `Beryllium is a chemical element with symbol Be and atomic number 4. It is a
  relatively rare element in the universe, usually occurring as a product of the spallation of
  larger atomic nuclei that have collided with cosmic rays.`
  }, {
  position: 5,
  name: 'Boron',
  date: '05.08.2021',
  status: 'Ожидает досье',
  description: `Boron is a chemical element with symbol B and atomic number 5. Produced entirely
  by cosmic ray spallation and supernovae and not by stellar nucleosynthesis, it is a
  low-abundance element in the Solar system and in the Earth's crust.`
  }, {
  position: 6,
  name: 'Carbon',
  date: '09.10.2013',
  status: 'Ожидает досье',
  description: `Carbon is a chemical element with symbol C and atomic number 6. It is nonmetallic
  and tetravalent—making four electrons available to form covalent chemical bonds. It belongs
  to group 14 of the periodic table.`
  }, {
  position: 7,
  name: 'Nitrogen',
  date: '02.12.2017',
  status: 'Ожидает досье',
  description: `Nitrogen is a chemical element with symbol N and atomic number 7. It was first
  discovered and isolated by Scottish physician Daniel Rutherford in 1772.`
  }, {
  position: 8,
  name: 'Oxygen',
  date: '08.09.2019',
  status: 'Ожидает досье',
  description: `Oxygen is a chemical element with symbol O and atomic number 8. It is a member of
  the chalcogen group on the periodic table, a highly reactive nonmetal, and an oxidizing
  agent that readily forms oxides with most elements as well as with other compounds.`
  }, {
  position: 9,
  name: 'Fluorine',
  date: '07.06.2020',
  status: 'Ожидает досье',
  description: `Fluorine is a chemical element with symbol F and atomic number 9. It is the
  lightest halogen and exists as a highly toxic pale yellow diatomic gas at standard
  conditions.`
  }, {
  position: 10,
  name: 'Neon',
  date: '05.03.2018',
  status: 'Ожидает досье',
  description: `Neon is a chemical element with symbol Ne and atomic number 10. It is a noble gas.
  Neon is a colorless, odorless, inert monatomic gas under standard conditions, with about
  two-thirds the density of air.`
  },
  ];
  