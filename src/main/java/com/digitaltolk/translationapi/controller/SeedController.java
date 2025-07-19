package com.digitaltolk.translationapi.controller;

import com.digitaltolk.translationapi.seed.DataSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seeder")
@RequiredArgsConstructor
public class SeedController {

    private final DataSeeder dataSeeder;

    @PostMapping("/seed")
    public ResponseEntity<String> triggerSeed() {
        dataSeeder.run();
        return ResponseEntity.ok("Seeding triggered successfully.");
    }
}
