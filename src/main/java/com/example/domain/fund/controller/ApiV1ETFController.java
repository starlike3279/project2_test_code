package com.example.domain.fund.controller;

import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.repository.ETFRepository;
import com.example.domain.global.RsData.RsData;
import com.example.domain.fund.entity.ETF;
import com.example.domain.fund.service.ETFService;
import com.example.domain.propersity.dto.PropensityDTO;
import com.example.domain.propersity.entity.Propensity;
import com.example.domain.propersity.repository.PropensityRepository;
import com.example.domain.propersity.service.PropensityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/etf")
public class ApiV1ETFController {

    private final ETFService etfService;
    private final PropensityService propensityService;

    @GetMapping("/{code}")
    public RsData<String> getETFInfo(@PathVariable("code") String code) {
        try {
            String etfInfo = etfService.getETFInfo(code);
            return RsData.of("200", "ETF 정보 조회 성공", etfInfo);
        } catch (Exception e) {
            return RsData.of("500", "ETF 정보 조회 실패: " + e.getMessage());
        }
    }

    @PostMapping("/survey/submit")
    public ResponseEntity<RsData<Long>> submitSurvey(@RequestBody Map<String, String> answers) {
        try {

            String mbti = propensityService.calculateMBTI(answers);

            PropensityDTO savedPropensity = propensityService.processAndSavePropensity(answers);

            return ResponseEntity.ok(RsData.of("200", "투자 성향 MBTI 등록 성공", savedPropensity.getPropensityId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RsData.of("400", "투자 성향 MBTI 등록 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/propensity/{id}")
    public ResponseEntity<RsData<PropensityDTO>> getPropensity(@PathVariable("id") Long id) {
        try {
            PropensityDTO propensity = propensityService.getPropensityById(id);
            return ResponseEntity.ok(RsData.of("200", "투자성향 조회 성공", propensity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RsData.of("400", e.getMessage(), null));
        }
    }

    // 상세 정보를 포함한 추천 ETF 조회
    @GetMapping("/recommend/detailed/{id}")
    public ResponseEntity<RsData<List<String>>> getDetailedRecommendedETFs(@PathVariable("id") Long id) {
        try {
            List<String> detailedETFs = propensityService.getDetailedRecommendedETFsById(id);
            return ResponseEntity.ok(RsData.of("200", "상세 추천 ETF 조회 성공", detailedETFs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RsData.of("400", "상세 추천 ETF 조회 실패: " + e.getMessage(), null));
        }
    }

    // 추천 etf 간단한 정보만 조회
    @GetMapping("/recommend/{id}")
    public ResponseEntity<RsData<List<ETF>>> getRecommendedETFs(@PathVariable("id") Long id) {
        try {
            List<ETF> recommendedETFs = propensityService.getRecommendedETFsById(id);
            return ResponseEntity.ok(RsData.of("200", "추천 ETF 조회 성공", recommendedETFs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RsData.of("400", "추천 ETF 조회 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/all/etfs")
    public ResponseEntity<RsData<Map<String, Object>>> testETFs() {
        try {
            // 서비스 계층에 메소드 추가 후 사용
            List<ETF> allETFs = propensityService.getAllETFs();

            Map<String, Object> allETFInfo = new HashMap<>();
            allETFInfo.put("totalETFCount", allETFs.size());
            allETFInfo.put("allETFs", allETFs);

            return ResponseEntity.ok(RsData.of("200", "ETF 조회 성공", allETFInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RsData.of("400", "ETF 조회 실패: " + e.getMessage(), null));
        }
    }

}