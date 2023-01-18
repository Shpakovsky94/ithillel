import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Product} from '../common/product';
import {Fridge} from '../common/fridge';
import {map} from 'rxjs/operators';
import {Category} from '../common/category';
import {User} from '../common/user';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  currentUser: User;
  headers: HttpHeaders;

  private productListURL = "http://localhost:8080/api/product-list";
  private fridgeUrl = 'http://localhost:8080/api/fridge';
  private categoryUrl = 'http://localhost:8080/api/category';


  constructor(private http: HttpClient) {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.headers = new HttpHeaders({
      authorization: 'Bearer ' + this.currentUser.token,
      "Content-Type": "application/json; charset=UTF-8"
    });
  }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.productListURL, {headers: this.headers});
  }

  getProductListByFridge(fridgeID: number) {
    return this.http.get<GetResponseProducts>(
      `${this.productListURL}/search/findByFridgeId/${fridgeID}`,
      {headers: this.headers})
      .pipe(map(response => response.content));
  }

  getFridges(): Observable<Fridge[]> {
    return this.http.get<Fridge[]>(this.fridgeUrl, {headers: this.headers})

  }

  getProductListByFridgeID(theFridgeId: number): Observable<Product[]> {

// need to build URL based on category id, page and size
    const searchUrl = `${this.productListURL}/search/findByFridgeId/${theFridgeId}`;
    return this.http.get<GetResponseProducts>(searchUrl, {headers: this.headers})
      .pipe(map(response => response.content));
  }

  searchProductsByName(theKeyword: string): Observable<Product[]> {

// need to build URL based on keyword, page and size
    const searchUrl = `${this.productListURL}/search/findByNameContaining/${theKeyword}`;

    return this.http.get<GetResponseProducts>(searchUrl, {headers: this.headers})
      .pipe(map(response => response.content));
  }


  deleteProduct(theProductId: string): Observable<any> {

    return this.http.delete(`${this.productListURL}/${theProductId}`, {headers: this.headers});
  }

  saveProduct(product: Product) {
    this.http.post(this.productListURL, product, {headers: this.headers})
      .subscribe();
  }

  getProduct(theProductId: number): Observable<Product> {

    // need to build URL based on product id
    const productUrl = `${this.productListURL}/${theProductId}`;

    return this.http.get<Product>(productUrl, {headers: this.headers});
  }

  updateProduct(data): Observable<any> {
    return this.http.put(`${this.productListURL}`, data, {headers: this.headers});
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryUrl, {headers: this.headers});
  }


}

interface GetResponseProducts {
  content: Product[];
  page: {
    size: number,
    totalElements: number,
    totalPages: number,
    number: number
  }
}
