import { TestBed } from '@angular/core/testing';

import { SoulsReportApiService } from './souls-report-api.service';

describe('SoulsReportApiService', () => {
  let service: SoulsReportApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SoulsReportApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
