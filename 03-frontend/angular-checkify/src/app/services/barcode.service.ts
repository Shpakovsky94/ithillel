import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Product} from '../common/product';
import {User} from '../common/user';

@Injectable({
  providedIn: 'root'
})
export class BarcodeService {

  currentUser: User;
  headers: HttpHeaders;

  private baseUrl = 'http://localhost:8080/api/register';
  private barcodeAPI = 'http://localhost:8080/api/products/register/';
  sharedData: Product;

  constructor(private http: HttpClient) {
    this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
    this.headers = new HttpHeaders({
      authorization: 'Bearer ' + this.currentUser.token,
      "Content-Type": "application/json; charset=UTF-8"
    });
  }


  public getAll(barcode: string) {
    return this.http.get<Product>(this.barcodeAPI + barcode, {headers: this.headers});

  }


}
