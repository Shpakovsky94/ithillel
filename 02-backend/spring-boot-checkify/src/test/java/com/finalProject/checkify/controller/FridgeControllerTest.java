package com.finalProject.checkify.controller;

import com.finalProject.checkify.SpringBootCheckifyApplication;
import com.finalProject.checkify.entity.Fridge;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBootCheckifyApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FridgeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void contextLoads() {

    }

    @Test
    void getAllFridges() throws Exception{

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/api/fridge",
                HttpMethod.GET, entity, String.class);
        assertNotNull(response.getBody());
    }

    @Test
    void getFridgeById() throws Exception{
        Fridge fridge = restTemplate.getForObject(getRootUrl() + "/api/fridge/1", Fridge.class);
        assertEquals("WORK", fridge.getName());
    }

    @Test
    void saveFridge() throws Exception{
        Fridge fridge = new Fridge();
        fridge.setName("TEST");
        ResponseEntity<Fridge> postResponse = restTemplate.postForEntity(getRootUrl() + "/api/fridge/3", fridge, Fridge.class);
        assertThat(postResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void updateFridge() throws Exception{
        int id = 1;
        Fridge fridge = restTemplate.getForObject(getRootUrl() + "/api/fridge/" + id, Fridge.class);
        fridge.setName("TestFridge");
        restTemplate.put(getRootUrl() + "/api/fridge/" + id, fridge);
        ResponseEntity<Fridge> putResponse = restTemplate.getForEntity(getRootUrl() + "/api/fridge/" + id, Fridge.class);
        assertThat(putResponse.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void deleteFridge() throws Exception{
        int id = 2;
        Fridge fridge = restTemplate.getForObject(getRootUrl() + "/api/fridge/" + id, Fridge.class);
        assertNotNull(fridge);
        restTemplate.delete(getRootUrl() + "/api/fridge/" + id);
        try {
            fridge = restTemplate.getForObject(getRootUrl() + "/api/fridge/" + id, Fridge.class);
        } catch (final HttpClientErrorException e) {
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }

}