package com.finalProject.checkify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalProject.checkify.SpringBootCheckifyApplication;
import com.finalProject.checkify.entity.ProductList;
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
class ProductListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    ProductListController productListController;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(productListController).build();
    }

    @Test
    void getAllProductList() throws Exception{
        mockMvc.perform(get("/api/product-list"))
                .andExpect(status().isOk());
    }

    @Test
    void getProductById() throws Exception{
        int id = 2;
        mockMvc.perform(get("/api/product-list/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct() throws Exception{
        ProductList test = new ProductList();

        String requestBody = new ObjectMapper().valueToTree(test).toString();
        mockMvc. perform(put("/api/product-list")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveProduct() throws Exception{
        ProductList test = new ProductList();

        String requestBody = new ObjectMapper().valueToTree(test).toString();
        mockMvc. perform(post("/api/product-list")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct() throws Exception{
        int id = 1;
        mockMvc.perform(delete("/api/product-list/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void findByFridgeId() throws Exception{
        int id = 1;
        mockMvc.perform(get("/api/product-list/search/findByFridgeId/" + id))
                .andExpect(status().isOk());

    }

    @Test
    void findByNameContaining() throws Exception{
        mockMvc.perform(get("/api/product-list/search/findByNameContaining/test"))
                .andExpect(status().isOk());
    }
}