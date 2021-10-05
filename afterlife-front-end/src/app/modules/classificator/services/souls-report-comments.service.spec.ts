import { TestBed } from '@angular/core/testing';

import { SoulsReportCommentsService } from './souls-report-comments.service';

describe('SoulsReportCommentsService', () => {
  let service: SoulsReportCommentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SoulsReportCommentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
