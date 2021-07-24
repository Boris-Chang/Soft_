import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BannerBarComponent } from './banner-bar.component';

describe('BannerBarComponent', () => {
  let component: BannerBarComponent;
  let fixture: ComponentFixture<BannerBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BannerBarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BannerBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
