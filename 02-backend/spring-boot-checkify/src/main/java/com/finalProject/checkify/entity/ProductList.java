package com.finalProject.checkify.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PRODUCT_LIST")
@Data
public class ProductList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BARCODE", length = 20)
    private String barcode;

    @Column(name = "NAME", length = 150)
    private String name;

    @Column(name = "EXPIRE_DATE")
    private Date date;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @JsonIgnoreProperties("products")
    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @JsonIgnoreProperties("productsInFridge")
    @ManyToOne
    @JoinColumn(name = "FRIDGE_ID")
    private  Fridge fridge;

}
