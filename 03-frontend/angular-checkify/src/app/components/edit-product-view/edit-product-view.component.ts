import {ProductService} from 'src/app/services/product.service';
import {Component, OnInit} from '@angular/core';
import {Product} from 'src/app/common/product';
import {ActivatedRoute, Router} from '@angular/router';
import {DatePipe, Location} from '@angular/common';
import {Fridge} from 'src/app/common/fridge';
import {NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {BsDatepickerConfig} from 'ngx-bootstrap/datepicker';
import {Category} from 'src/app/common/category';


@Component({
  selector: 'app-edit-product-view',
  templateUrl: './edit-product-view.component.html',
  styleUrls: ['./edit-product-view.component.css']
})
export class EditProductViewComponent implements OnInit {

  datePickerConfig: Partial<BsDatepickerConfig>;

  fridges: Fridge[] = [];
  categories: Category[] = [];
  product: Product = new Product();
  model: NgbDateStruct;
  message = '';

  constructor(private productService: ProductService,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location,
              public datepipe: DatePipe) {
    this.datePickerConfig = Object.assign({}, {
      showWeekNumbers: false,
    });
  }

  ngOnInit(): void {
    this.listFridges();
    this.listCategories();
    this.route.paramMap.subscribe(() => {
      this.handleProductDetails();
    });
  }

  // tslint:disable-next-line:typedef
  handleProductDetails() {

    // get the "id" param string. convert string to a number using the "+" symbol
    const theProductId: number = +this.route.snapshot.paramMap.get('id');

    this.productService.getProduct(theProductId).subscribe(
      data => {
        this.product = data;
      }
    );
  }

  updateTheProduct(): void {
    this.productService.updateProduct(this.product)
      .subscribe(
        response => {
          this.message = 'The product was updated successfully!';
          this.router.navigate(['./product-list/' + this.product.id]);
        },
        error => {
          console.log(error);
        });

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
