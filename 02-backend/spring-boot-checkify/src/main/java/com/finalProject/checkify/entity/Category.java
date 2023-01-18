package com.finalProject.checkify.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "CATEGORY")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 100)
    private String name;

    @JsonIgnoreProperties("category")
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<ProductList> products;


}
