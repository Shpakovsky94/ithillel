import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {InviteMenuComponent} from './invite-menu.component';

describe('InviteMenuComponent', () => {
  let component: InviteMenuComponent;
  let fixture: ComponentFixture<InviteMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InviteMenuComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InviteMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
