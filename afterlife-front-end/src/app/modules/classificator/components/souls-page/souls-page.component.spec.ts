import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SoulsPageComponent } from './souls-page.component';

describe('SoulsPageComponent', () => {
  let component: SoulsPageComponent;
  let fixture: ComponentFixture<SoulsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SoulsPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SoulsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
