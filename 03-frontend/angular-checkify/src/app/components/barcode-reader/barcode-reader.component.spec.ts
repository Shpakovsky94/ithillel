import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BarcodeReaderComponent} from './barcode-reader.component';

describe('BarcodeReaderComponent', () => {
  let component: BarcodeReaderComponent;
  let fixture: ComponentFixture<BarcodeReaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [BarcodeReaderComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BarcodeReaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
