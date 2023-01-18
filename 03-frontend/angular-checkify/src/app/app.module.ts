import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';

import {AppComponent} from './app.component';
import {ProductListComponent} from './components/product-list/product-list.component';
import {HttpClientModule} from '@angular/common/http';
import {ProductService} from './services/product.service';
import {RouterModule, Routes} from '@angular/router';
import {BarcodeReaderComponent} from './components/barcode-reader/barcode-reader.component'
import {ProductDetailsComponent} from './components/product-details/product-details.component';
import {FridgeMenuComponent} from './components/fridge-menu/fridge-menu.component';
import {SearchComponent} from './components/search/search.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {EditProductViewComponent} from './components/edit-product-view/edit-product-view.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BsDatepickerModule} from 'ngx-bootstrap/datepicker';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AddProductViewComponent} from './components/add-product-view/add-product-view.component';
import {LoginPageComponent} from './components/login-page/login-page.component';
import {RegisterComponent} from './components/register/register.component';


const routes: Routes = [
  {path: '', redirectTo: '/product-list', pathMatch: 'full'},
  {path: 'product-list/:id/editProduct', component: EditProductViewComponent},
  {path: 'product-list/:id', component: ProductDetailsComponent},
  {path: 'addProduct', component: AddProductViewComponent},
  {path: 'search/:keyword', component: ProductListComponent},
  {path: 'fridge/:id', component: ProductListComponent},
  {path: 'fridge', component: ProductListComponent},
  {path: 'product-list', component: ProductListComponent},
  {path: 'barcode', component: BarcodeReaderComponent}

  // {path: '**' , redirectTo: '/product-list', pathMatch: 'full'}
];

@NgModule({
  declarations: [
    AppComponent,
    ProductListComponent,
    BarcodeReaderComponent,
    ProductDetailsComponent,
    FridgeMenuComponent,
    SearchComponent,
    EditProductViewComponent,
    AddProductViewComponent,
    LoginPageComponent,
    RegisterComponent
  ],
  imports: [
    RouterModule.forRoot(routes),
    BrowserModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    BrowserAnimationsModule,
    BsDatepickerModule.forRoot()
  ],
  providers: [ProductService, DatePipe],
  bootstrap: [AppComponent],
})
export class AppModule {
}
