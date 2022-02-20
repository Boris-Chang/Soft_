import {Component, Input, OnInit} from '@angular/core';
import {SoulsReportApiService} from "../../../modules/classificator/services";
import { Role } from 'src/app/shared/enum/role.enum';

const Roles = 'role';

@Component({
  selector: 'app-sins-uploader',
  templateUrl: './sins-uploader.component.html',
  styleUrls: ['./sins-uploader.component.scss']
})
export class SinsUploaderComponent implements OnInit {
  @Input() soulId: number;
  constructor(private readonly soulsReportService: SoulsReportApiService) { }
  isFileChosen: boolean = false;
  formData: FormData = null;
  onFileSelected(event){
    const file: File = event.target.files[0];
    if (file) {
      this.isFileChosen = true;
      this.formData = new FormData();
      this.formData.append("file", file);
    }
  }

  upload(){
    if(this.isFileChosen&&this.formData!=null){
      this.soulsReportService.setSinsReport(this.soulId,this.formData).subscribe(value => {
        console.log(value);
      })
    }
  }
  
   //
  get isProsecutor(): boolean {
    return this.getUserRole() === Role.Heaven_Prosecutor;
  }
  //
  getUserRole(): string{
    return window.sessionStorage.getItem(Roles);
  }
  
  ngOnInit(): void {
  }

}
