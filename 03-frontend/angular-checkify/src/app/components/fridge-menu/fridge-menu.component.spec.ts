import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {FridgeMenuComponent} from './fridge-menu.component';

describe('FridgeMenuComponent', () => {
  let component: FridgeMenuComponent;
  let fixture: ComponentFixture<FridgeMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FridgeMenuComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FridgeMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
