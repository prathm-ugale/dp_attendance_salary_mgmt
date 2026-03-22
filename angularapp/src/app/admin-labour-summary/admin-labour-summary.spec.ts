import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminLabourSummary } from './admin-labour-summary';

describe('AdminLabourSummary', () => {
  let component: AdminLabourSummary;
  let fixture: ComponentFixture<AdminLabourSummary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminLabourSummary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminLabourSummary);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
