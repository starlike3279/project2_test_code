package com.example.domain.fund.controller;

import com.example.domain.fund.service.ETFService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor    // 생성자 주입을 위한 어노테이션
@RequestMapping("/api/etf") // 기본 경로 설정
public class ETFController {

    private final ETFService etfService;  // final로 선언

    @GetMapping("/stock")
    public String getStockInfo(Model model) {
        try {
            String stockInfo = etfService.getStockInfo("069500");
            model.addAttribute("stockInfo", stockInfo);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching stock information: " + e.getMessage());
        }
        return "stock";
    }
}