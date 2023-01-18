package com.finalProject.checkify.controller;


import com.finalProject.checkify.entity.Product;
import com.finalProject.checkify.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/products")

public class ProductController {

    private final ProductServiceImpl productService;
    private String localBarcode;

    @Autowired
    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @GetMapping("/show")
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @GetMapping("/{barcode}")
    public Product getProductByBarcode(@PathVariable(value = "barcode") String theBarcode) {

        return productService.findByBarcode(theBarcode);
    }

    @PostMapping()
    public void saveProduct(@RequestBody Product theProduct){
        productService.save(theProduct);
    }

    @DeleteMapping("/{barcode}")
    public void deleteProduct(@PathVariable(value = "barcode") String  barcode){
        productService.deleteByBarcode(barcode);
    }


    @GetMapping("/register/{code}")
    public Product getBarcodeProcessAndReturnProduct(@PathVariable(value = "code") String barcode){
        localBarcode = barcode;
        System.out.println(localBarcode);
        Product productTest = null;

        if (productService.findByBarcode(localBarcode) != null){
            return productService.findByBarcode(localBarcode);

        }else if (productService.getProductFromMonsterApi(localBarcode) != null){
            productService.save(productService.getProductFromMonsterApi(localBarcode));
            return productService.findByBarcode(localBarcode);

        }else {
            productTest = new Product();
            productTest.setBarcode(localBarcode);
            productTest.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/4/46/Question_mark_%28black%29.svg/800px-Question_mark_%28black%29.svg.png");
            return productTest;
        }
    }
}