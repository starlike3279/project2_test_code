package com.example.domain.fund.controller;

import com.example.domain.fund.model.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.service.ETFService;
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

    @GetMapping("/ETF")
    public String getETFInfo(Model model) {
        try {
            String etfInfo = etfService.getETFInfo("252670");
            model.addAttribute("etfInfo", etfInfo);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching stock information: " + e.getMessage());
        }
        return "etfInfo";
    }

}