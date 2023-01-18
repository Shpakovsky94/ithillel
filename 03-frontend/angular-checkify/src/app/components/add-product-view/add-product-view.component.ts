import {Component, OnInit} from '@angular/core';
import {ProductService} from 'src/app/services/product.service';
import {Product} from 'src/app/common/product';
import {ActivatedRoute, Router} from '@angular/router';
import {DatePipe, Location} from '@angular/common';
import {Fridge} from 'src/app/common/fridge';
import {NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {BsDatepickerConfig} from 'ngx-bootstrap/datepicker';
import {BarcodeService} from 'src/app/services/barcode.service';
import {Category} from 'src/app/common/category';

@Component({
  selector: 'app-add-product-view',
  templateUrl: './add-product-view.component.html',
  styleUrls: ['./add-product-view.component.css']
})
export class AddProductViewComponent implements OnInit {

  datePickerConfig: Partial<BsDatepickerConfig>;

  fridges: Fridge[] = [];
  categories: Category[] = [];
  product: Product = new Product();
  model: NgbDateStruct;
  message = '';

  constructor(private productService: ProductService,
              private route: ActivatedRoute,
              private location: Location,
              public datepipe: DatePipe,
              private dataService: BarcodeService,
              private navRouter: Router) {
    this.datePickerConfig = Object.assign({}, {
      showWeekNumbers: false,
    });
    this.product = this.dataService.sharedData;
  }

  ngOnInit(): void {
    this.listFridges();
    this.listCategories();
  }

  // tslint:disable-next-line:typedef

  saveTheProduct(): void {
    this.productService.updateProduct(this.product)
      .subscribe(
        response => {
          this.message = 'The product was updated successfully!';
          this.navRouter.navigate(['./product-list']);
        },
        error => {
          console.log(error);
        });
    // this.navRouter.navigate(['app-product-list']);


  }

  // takes back to the previous component
  goBack(): void {
    this.location.back();
  }


  listFridges() {
    this.productService.getFridges().subscribe(
      data => {
        this.fridges = data;
      }
    )
  }

  listCategories() {
    this.productService.getCategories().subscribe(
      data => {
        this.categories = data;
      }
    )
  }

}
