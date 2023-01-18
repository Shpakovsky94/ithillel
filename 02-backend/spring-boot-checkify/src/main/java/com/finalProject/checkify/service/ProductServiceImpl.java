package com.finalProject.checkify.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.finalProject.checkify.dao.ProductRepository;
import com.finalProject.checkify.entity.Product;
import com.finalProject.checkify.jackson.ProductListDeserialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository =productRepository;
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    @Override
    public void save(Product theProduct) {
        productRepository.save(theProduct);
    }


    @Override
    public void deleteByBarcode(String barcode) {
        productRepository.deleteByBarcode(barcode);

    }

    public Product getProductFromMonsterApi(String barcode) {
        Product testProduct = null;

        if (getJsonFromApi(barcode)!= null){
        testProduct = getProductFromJson(barcode);
        return testProduct;

        } else return testProduct;
    }

    public String getJsonFromApi(String barcode){

        try {
            final String uri = "https://barcode.monster/api/" + barcode;
            RestTemplate restTemplate = new RestTemplate();

            return restTemplate.getForObject(uri, String.class);
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    public Product getProductFromJson(String barcode){

        Product testProduct =null;
        String jsonResult = getJsonFromApi(barcode);
        try {
            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();

            module.addDeserializer(Product.class, new ProductListDeserialization());
            mapper.registerModule(module);

            testProduct = mapper.readValue(jsonResult, Product.class);

        } catch (Exception j) {
            j.printStackTrace();
        }
        return testProduct;
    }

}