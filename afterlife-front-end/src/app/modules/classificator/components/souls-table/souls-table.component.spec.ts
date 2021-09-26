import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SoulsTableComponent } from './souls-table.component';

describe('SoulsTableComponent', () => {
  let component: SoulsTableComponent;
  let fixture: ComponentFixture<SoulsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SoulsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SoulsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
