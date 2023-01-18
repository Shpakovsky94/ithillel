import {Category} from './category';
import {Fridge} from './fridge';

export class Product {

  id: string;
  barcode: string;
  name: string;
  date: Date;
  description: string;
  imageUrl: string;
  category: Category;
  fridge: Fridge;

}
