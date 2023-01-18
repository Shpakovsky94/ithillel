import {Fridge} from './../../common/fridge';
import {Component, OnInit} from '@angular/core';
import {ProductService} from 'src/app/services/product.service';
import {UserService} from 'src/app/services/user.service';
import {Router} from '@angular/router';


@Component({
  selector: 'app-fridge-menu',
  templateUrl: './fridge-menu.component.html',
  styleUrls: ['./fridge-menu.component.css']
})
export class FridgeMenuComponent implements OnInit {

  fridges: Fridge[];

  constructor(private productService: ProductService,
              private userService: UserService,
              private router: Router) {
  }

  ngOnInit() {
    this.listFridges();
  }

  listFridges() {

    this.productService.getFridges().subscribe(
      data => {
        this.fridges = data;
      }
    )
  }

  logOut() {
    this.userService.logOut();
    this.router.navigate(['/login']);
  }


}
