import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupervisorAttendance } from './supervisor-attendance';

describe('SupervisorAttendance', () => {
  let component: SupervisorAttendance;
  let fixture: ComponentFixture<SupervisorAttendance>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupervisorAttendance]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupervisorAttendance);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
