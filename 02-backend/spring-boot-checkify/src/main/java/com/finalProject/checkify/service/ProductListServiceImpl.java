package com.finalProject.checkify.service;

import com.finalProject.checkify.dao.ProductListRepository;
import com.finalProject.checkify.entity.ProductList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductListServiceImpl implements ProductListService {

    private final ProductListRepository productListRepository;

    @Autowired
    public ProductListServiceImpl(ProductListRepository productListRepository) {
        this.productListRepository = productListRepository;
    }

    @Override
    public List<ProductList> findAll() {
        return productListRepository.findAll();
    }

    @Override
    public ProductList findById(Long theId) {
        Optional<ProductList> result = productListRepository.findById(theId);
        ProductList productList = null;

        if (result.isPresent()) {
            productList = result.get();
        } else {
            throw new RuntimeException("Did not find Product id - " + theId);
        }
        return productList;
    }
    @Override
    public Page<ProductList> findByFridgeId(Long fridgeId, Pageable pageable) {
        return productListRepository.findByFridgeId(fridgeId, pageable);
    }

    @Override
    public Page<ProductList> findByNameContaining(String name, Pageable pageable) {
        return productListRepository.findByNameContaining(name, pageable);
    }

    @Override
    public ProductList findByBarcode(String barcode) {
        return productListRepository.findByBarcode(barcode);
    }

    @Override
    public void save(ProductList productList) {
        productListRepository.save(productList);

    }

    @Override
    public void deleteById(Long id) {
        productListRepository.deleteById(id);
    }
}