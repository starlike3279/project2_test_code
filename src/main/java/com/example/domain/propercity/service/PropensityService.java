package com.example.domain.propercity.service;

import com.example.domain.fund.model.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.repository.ETFRepository;
import com.example.domain.propercity.controller.PropensityController;
import com.example.domain.propercity.dto.PropensityDTO;
import com.example.domain.propercity.entity.Propensity;
import com.example.domain.propercity.repository.PropensityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropensityService {

    private final ETFRepository etfRepository;
    private final PropensityRepository propensityRepository;

    // 설문 응답 기반 투자 성향 MBTI 계산
    public String calculateMBTI(Map<String, String> answers) {
        StringBuilder mbti = new StringBuilder();

        mbti.append(answers.getOrDefault("q1", "option1").equals("option1") ? "I" : "E");
        mbti.append(answers.getOrDefault("q2", "option1").equals("option1") ? "S" : "N");
        mbti.append(answers.getOrDefault("q3", "option1").equals("option1") ? "T" : "F");
        mbti.append(answers.getOrDefault("q4", "option1").equals("option1") ? "J" : "P");

        return mbti.toString();
    }

    public PropensityDTO processAndSavePropensity(Map<String, String> answers) {
        String mbtiResult = calculateMBTI(answers);

        Propensity propensity = new Propensity();
        propensity.setSurveyAnswer(answers.toString());
        propensity.setSurveyResult(mbtiResult);
        Propensity savedPropensity = propensityRepository.save(propensity);

        return PropensityDTO.builder()
                .propensityId(savedPropensity.getPropensityId())
                .surveyAnswer(savedPropensity.getSurveyAnswer())
                .surveyResult(savedPropensity.getSurveyResult())
                .build();
    }

    public List<ETF> getRecommendedETFsById(Long id) {
        // PropensityRepository를 통해 데이터를 가져오고 비즈니스 로직 수행
        Propensity propensity = propensityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 투자 성향 데이터가 존재하지 않습니다."));

        // MBTI를 가져온 후 추천 ETF를 조회
        String mbti = propensity.getSurveyResult();
        return getRecommendedETFs(mbti);
    }

    // 투자 성향 MBTI 기반 추천 ETF 조회
    public List<ETF> getRecommendedETFs(String mbti) {
        switch (mbti) {
            case "ISTJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.DOMESTIC_BOND);
            case "ISFJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.FOREIGN_BOND);
            case "INFJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            case "INTJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            case "ISTP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.DOMESTIC_BOND);
            case "ISFP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.FOREIGN_BOND);
            case "INFP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            case "INTP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            case "ESTP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.DOMESTIC_BOND);
            case "ESFP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.FOREIGN_BOND);
            case "ENFP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            case "ENTP":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.STOCK, ETFSubCategory.SMALL_MID_CAP);
            case "ESTJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            case "ESFJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.DOMESTIC_BOND);
            case "ENFJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.BOND, ETFSubCategory.FOREIGN_BOND);
            case "ENTJ":
                return etfRepository.findByCategoryAndSubCategory(ETFCategory.COMMODITY, ETFSubCategory.PRECIOUS_METAL);
            default:
                return etfRepository.findByCategory(ETFCategory.STOCK);
        }
    }
}

