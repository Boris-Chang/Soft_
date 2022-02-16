import {Component, Input, OnInit} from '@angular/core';
import { SoulsReportApiService } from "../../../modules/classificator/services";
import {HttpEventType, HttpResponse} from "@angular/common/http";
import { Observable } from 'rxjs';


@Component({
  selector: 'app-goodness-uploader',
  templateUrl: './goodness-uploader.component.html',
  styleUrls: ['./goodness-uploader.component.scss']
})
export class GoodnessUploaderComponent implements OnInit {
  @Input() soulId: number;
  selectedFiles?: FileList;
  currentFile?: File;
  progress = 0;
  message = '';
  fileInfos?: Observable<any>;
  constructor(private uploadService: SoulsReportApiService) { }
  selectFile(event: any): void {
    this.selectedFiles = event.target.files;
  }
  upload(): void {
    this.progress = 0;
    if (this.selectedFiles) {
      const file: File | null = this.selectedFiles.item(0);
      if (file) {
        this.currentFile = file;
        this.uploadService.uploadSinsReport(this.soulId,this.currentFile).subscribe(
          (event: any) => {
            if (event.type === HttpEventType.UploadProgress) {
              this.progress = Math.round(100 * event.loaded / event.total);
            } else if (event instanceof HttpResponse) {
              this.message = event.body.message;
            }
          },
          (err: any) => {
            console.log(err);
            this.progress = 0;
            if (err.error && err.error.message) {
              this.message = err.error.message;
            } else {
              this.message = 'Could not upload the file!';
            }
            this.currentFile = undefined;
          });
      }
      this.selectedFiles = undefined;
    }
  }
  ngOnInit(): void {
      
  }
}
