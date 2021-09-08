import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-file-uploader',
  templateUrl: './file-uploader.component.html',
  styleUrls: ['./file-uploader.component.scss']
})
export class FileUploaderComponent implements OnInit {

  constructor() { }

  afuConfig = {
    uploadAPI: {
      url:"https://slack.com/api/files.upload"
    }
  };

  ngOnInit(): void {
  }

}
