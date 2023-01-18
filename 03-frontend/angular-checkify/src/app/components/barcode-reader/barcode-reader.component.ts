import {AfterViewInit, Component} from '@angular/core';
import Quagga from 'quagga';
import {Product} from 'src/app/common/product';
import {BarcodeService} from "src/app/services/barcode.service"
import {Router} from '@angular/router';


@Component({
  selector: 'app-barcode-reader',
  templateUrl: 'barcode-reader.component.html',
  styleUrls: ['barcode-reader.component.css'],

})
export class BarcodeReaderComponent implements AfterViewInit {
  title = 'scanner-classycode';

  errorMessage: string;
  barCode: string;
  product: Product;
  receivedCode: string;
  redirection: boolean = false;

  constructor(private service: BarcodeService,
              private router: Router) {
    this.product = new Product();

  }

  ngAfterViewInit(): void {

    if (!navigator.mediaDevices || !(typeof navigator.mediaDevices.getUserMedia === 'function')) {
      this.errorMessage = 'getUserMedia is not supported';
      return;
    }
    Quagga.init({
      inputStream: {
        name: "Live",
        type: "LiveStream",
        target: document.querySelector('#yourElement')    // Or '#yourElement' (optional)
      },
      decoder: {
        readers: ["code_128_reader",
          "ean_reader"]
      }
    }, (err) => {
      if (err) {
        console.log(err);
        return
      } else {
        Quagga.start();
        Quagga.onDetected((res) => {
          this.onBarcodeScanned(res.codeResult.code);
        });
      }
    });

  }

  onBarcodeScanned(code: string) {
    console.log('this is code: ' + code);
    this.service.getAll(code).subscribe((data => {
      this.product = data;
      this.service.sharedData = this.product;
      this.router.navigate(['addProduct']);
    }))

    Quagga.stop();


  }


}
