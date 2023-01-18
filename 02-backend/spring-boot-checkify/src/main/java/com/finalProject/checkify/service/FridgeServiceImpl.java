package com.finalProject.checkify.service;

import com.finalProject.checkify.dao.FridgeRepository;
import com.finalProject.checkify.entity.Fridge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FridgeServiceImpl implements FridgeService {

    private final FridgeRepository fridgeRepository;

    @Autowired
    public FridgeServiceImpl(FridgeRepository fridgeRepository) {
        this.fridgeRepository = fridgeRepository;
    }

    @Override
    public List<Fridge> findAll() {
        return fridgeRepository.findAll();
    }

    @Override
    public Fridge findById(Long id) {
        Optional<Fridge> result = fridgeRepository.findById(id);
        Fridge theFridge = null;

        if (result.isPresent()) {
            theFridge = result.get();
        } else {
            throw new RuntimeException("Did not find Fridge by id - " + id);
        }
        return theFridge;
    }

    @Override
    public void save(Fridge theFridge) {
    fridgeRepository.save(theFridge);
    }

    @Override
    public void deleteById(Long id) {
    fridgeRepository.deleteById(id);
    }
}
