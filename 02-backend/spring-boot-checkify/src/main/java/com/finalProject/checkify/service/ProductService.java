package com.finalProject.checkify.service;

import com.finalProject.checkify.entity.Product;

import java.util.List;

public interface ProductService {

     List<Product> findAll();
     Product findByBarcode(String barcode);
     void save(Product theProduct);
     void deleteByBarcode(String barcode);

}
