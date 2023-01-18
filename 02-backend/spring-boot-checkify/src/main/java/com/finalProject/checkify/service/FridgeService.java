package com.finalProject.checkify.service;

import com.finalProject.checkify.entity.Fridge;

import java.util.List;

public interface FridgeService {

    List<Fridge> findAll();
    Fridge findById(Long id);
    void save(Fridge theFridge);
    void deleteById(Long id);
}
