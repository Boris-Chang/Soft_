import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SinsUploaderComponent } from './sins-uploader.component';

describe('FileUploaderComponent', () => {
  let component: SinsUploaderComponent;
  let fixture: ComponentFixture<SinsUploaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SinsUploaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SinsUploaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
