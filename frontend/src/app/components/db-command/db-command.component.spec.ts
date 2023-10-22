import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DbCommandComponent } from './db-command.component';

describe('DbCommandComponent', () => {
  let component: DbCommandComponent;
  let fixture: ComponentFixture<DbCommandComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DbCommandComponent]
    });
    fixture = TestBed.createComponent(DbCommandComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
