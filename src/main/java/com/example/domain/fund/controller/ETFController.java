package com.example.domain.fund.controller;

import com.example.domain.fund.model.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.service.ETFService;
import com.example.domain.propercity.service.PropensityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/etf")
public class ETFController {

    private final ETFService etfService;
    private final PropensityService propensityService;

    @GetMapping("/ETF")
    public String getETFInfo(Model model) {
        try {
            String etfInfo = etfService.getETFInfo("069500");
            model.addAttribute("etfInfo", etfInfo);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching stock information: " + e.getMessage());
        }
        return "etfInfo";
    }

    @GetMapping("/survey")
    public String survey() {
        return "survey";
    }

    @GetMapping("/recommendETF")
    public String getRecommendedETFs(Model model) {
        String mbti = "ISTJ"; // PropensityService를 통해 조회하는 방식으로 수정 예정
        List<ETF> recommendedETFs = propensityService.getRecommendedETFs(mbti);
        model.addAttribute("etfs", recommendedETFs);
        return "recommendETF";
    }

}