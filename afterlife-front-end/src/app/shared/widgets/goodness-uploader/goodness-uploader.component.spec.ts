import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GoodnessUploaderComponent } from './goodness-uploader.component';

describe('GoodnessUploaderComponent', () => {
  let component: GoodnessUploaderComponent;
  let fixture: ComponentFixture<GoodnessUploaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GoodnessUploaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GoodnessUploaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
