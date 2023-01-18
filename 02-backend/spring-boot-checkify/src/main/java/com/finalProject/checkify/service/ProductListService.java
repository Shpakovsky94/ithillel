package com.finalProject.checkify.service;

import com.finalProject.checkify.entity.ProductList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductListService {

     List<ProductList> findAll();
     ProductList findById(Long theId);
     Page<ProductList> findByFridgeId(Long fridgeId, Pageable pageable);
     Page<ProductList> findByNameContaining(String name, Pageable pageable);
     ProductList findByBarcode(String barcode);

     void save(ProductList productList);
     void deleteById(Long id);


}
