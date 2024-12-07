package com.example.domain.propercity.controller;

import com.example.domain.propercity.dto.PropensityDTO;
import com.example.domain.propercity.entity.Propensity;
import com.example.domain.propercity.repository.PropensityRepository;
import com.example.domain.propercity.service.PropensityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/propensity")
public class PropensityController {

    private final PropensityRepository propensityRepository;
    private final PropensityService propensityService;

    @PostMapping("/survey")
    public ResponseEntity<?> savePropensity(@RequestBody Map<String, String> answers) {
        try {
            PropensityDTO savedPropensity = propensityService.processAndSavePropensity(answers);
            return ResponseEntity.ok(savedPropensity);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save propensity: " + e.getMessage());
        }
    }

    private String calculateMBTI(Map<String, String> answers) {
        StringBuilder mbti = new StringBuilder();
        mbti.append(answers.get("q1").equals("option1") ? "I" : "E");
        mbti.append(answers.get("q2").equals("option1") ? "S" : "N");
        mbti.append(answers.get("q3").equals("option1") ? "T" : "F");
        mbti.append(answers.get("q4").equals("option1") ? "J" : "P");
        return mbti.toString();
    }
}
