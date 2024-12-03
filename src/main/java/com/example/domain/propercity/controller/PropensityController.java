package com.example.domain.propercity.controller;

import com.example.domain.propercity.entity.Propensity;
import com.example.domain.propercity.repository.PropensityRepository;
import com.example.domain.propercity.service.PropensityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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
            String mbtiResult = calculateMBTI(answers);

            Propensity propensity = new Propensity();
            propensity.setSurveyAnswer(answers.toString());
            propensity.setSurveyResult(mbtiResult);
            propensityRepository.save(propensity);

            Map<String, String> mbtiDescription = propensityService.getMBTIDescription(mbtiResult);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("result", mbtiResult);
            response.put("type", mbtiDescription.get("type"));
            response.put("description", mbtiDescription.get("description"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save propensity.");
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
