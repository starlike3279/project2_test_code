package com.example.domain.fund.controller;

import com.example.domain.fund.service.ETFService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/etf")
public class ETFController {
    private final ETFService etfService;

    @GetMapping("/stock")
    public String getStockInfo(Model model) {
        try {
            String stockInfo = etfService.getStockInfo("069500");
            model.addAttribute("stockInfo", stockInfo);
            return "stock";  // templates/stock.html
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching stock information: " + e.getMessage());
            return "error";  // templates/error.html
        }
    }

    @GetMapping("/kodex200")
    public String getKodex200Info(Model model) {
        try {
            String kodex200Info = etfService.getStockInfo("069500");
            model.addAttribute("etfInfo", kodex200Info);
            return "etfInfo";  // templates/etfInfo.html
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching KODEX 200 information: " + e.getMessage());
            return "error";  // templates/error.html
        }
    }
}