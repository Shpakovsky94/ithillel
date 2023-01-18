package com.finalProject.checkify.controller;

import com.finalProject.checkify.entity.Fridge;
import com.finalProject.checkify.service.FridgeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/")
public class FridgeController {

    private final FridgeServiceImpl fridgeService;

    @Autowired
    public FridgeController(FridgeServiceImpl fridgeService) {
        this.fridgeService = fridgeService;
    }

    @GetMapping("/fridge")
    public List<Fridge> getAllFridges() {
        return fridgeService.findAll();
    }

    @GetMapping("/fridge/{id}")
    public Fridge getFridgeById(@PathVariable(value = "id") Long fridgeId) {

        return fridgeService.findById(fridgeId);
    }

    @PostMapping("/fridge/{id}")
    public void saveFridge(@RequestBody Fridge theFridge){
        fridgeService.save(theFridge);
    }

    @DeleteMapping("/fridge/{id}")
    public void deleteFridge(@PathVariable(value = "id") Long fridgeId){
        fridgeService.deleteById(fridgeId);
    }

}
