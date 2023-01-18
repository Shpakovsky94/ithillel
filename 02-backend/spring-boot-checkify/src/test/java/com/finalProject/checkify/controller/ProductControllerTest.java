package com.finalProject.checkify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalProject.checkify.SpringBootCheckifyApplication;
import com.finalProject.checkify.entity.Product;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootCheckifyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    ProductController productController;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void getAllProducts() throws Exception{
        mockMvc.perform(get("/api/products/show"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductListByBarcode() throws Exception{
        String barcode = "3068320014068";
        mockMvc.perform(get("/api/products/" + barcode))
                .andExpect(status().isOk());
    }

    @Test
    void saveProduct() throws Exception{
        Product test = new Product();
        String barcode = "3068320014069";
        test.setBarcode(barcode);

        String requestBody = new ObjectMapper().valueToTree(test).toString();
        mockMvc. perform(post("/api/products")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct() throws Exception{
        String barcode = "3068320014069";
        mockMvc.perform(delete("/api/products/" + barcode))
                .andExpect(status().isOk());
    }

}