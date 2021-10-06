import { TestBed } from '@angular/core/testing';

import { SoulsReportCommentsApiService } from './souls-report-comments-api.service';

describe('SoulsReportCommentsService', () => {
  let service: SoulsReportCommentsApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SoulsReportCommentsApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
