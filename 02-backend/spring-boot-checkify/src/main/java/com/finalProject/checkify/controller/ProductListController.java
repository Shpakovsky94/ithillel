package com.finalProject.checkify.controller;

import com.finalProject.checkify.entity.ProductList;
import com.finalProject.checkify.service.ProductListServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/product-list")
public class ProductListController {

    private final ProductListServiceImpl productListService;

    @Autowired
    public ProductListController(ProductListServiceImpl productListService) {
        this.productListService = productListService;
    }

    @GetMapping()
    public List<ProductList> getAllProductList() {
        return productListService.findAll();
    }

    @GetMapping("/{id}")
    public ProductList getProductById(@PathVariable(value = "id") Long productId) {

        return productListService.findById(productId);
    }
    @PutMapping()
    public void updateProduct(@RequestBody ProductList productList) {
        productListService.save(productList);
    }

    @PostMapping()
    public void saveProduct(@RequestBody ProductList productList){
        productListService.save(productList);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable(value = "id") Long productId){
        productListService.deleteById(productId);
    }

    @GetMapping("/search/findByFridgeId/{id}")
    public Page<ProductList> findByFridgeId(@PathVariable(value = "id") Long productId, Pageable pageable) {

        return productListService.findByFridgeId(productId, pageable);
    }

    @GetMapping("/search/findByNameContaining/{name}")
    public Page<ProductList> findByNameContaining(@PathVariable(value = "name") String name, Pageable pageable) {

        return productListService.findByNameContaining(name, pageable);
    }
}
