import {Component, Input, OnInit} from '@angular/core';
import {SoulsReportApiService} from "../../../modules/classificator/services";
import {ReportedSoul} from "../../../modules/classificator/models";

@Component({
  selector: 'app-goodness-uploader',
  templateUrl: './goodness-uploader.component.html',
  styleUrls: ['./goodness-uploader.component.scss']
})
export class GoodnessUploaderComponent implements OnInit {
  @Input() soulId: number;
  constructor(private readonly soulsReportService: SoulsReportApiService) { }
  isFileChosen: boolean = false;
  formData: FormData = null
  onFileSelected(event){
    const file: File = event.target.files[0];
    if (file) {
      this.isFileChosen = true;
      this.formData = new FormData();
      this.formData.append("file", file);
    }
  }

  upload(){
    console.log(this.isFileChosen+";"+this.formData);
    if(this.isFileChosen&&this.formData!=null){
      this.soulsReportService.setGoodnessReport(this.soulId,this.formData).subscribe(value => {
        console.log(value);
      })
    }
  }

  ngOnInit(): void {
  }
}
