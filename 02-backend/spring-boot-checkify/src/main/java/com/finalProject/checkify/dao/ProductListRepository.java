package com.finalProject.checkify.dao;

import com.finalProject.checkify.entity.ProductList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductListRepository extends JpaRepository<ProductList, Long> {

    Page<ProductList> findByFridgeId(Long id, Pageable pageable);

    Page<ProductList> findByNameContaining(String name, Pageable pageable);

    ProductList findByBarcode(String barcode);

}

