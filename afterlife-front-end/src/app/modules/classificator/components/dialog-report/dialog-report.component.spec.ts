import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogReportComponent } from './dialog-report.component';

describe('DialogReportComponent', () => {
  let component: DialogReportComponent;
  let fixture: ComponentFixture<DialogReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
