package com.example.domain.propercity.service;

import com.example.domain.fund.model.ETF;
import com.example.domain.fund.model.ETFCategory;
import com.example.domain.fund.model.ETFSubCategory;
import com.example.domain.fund.repository.ETFRepository;
import com.example.domain.propercity.controller.PropensityController;
import com.example.domain.propercity.repository.PropensityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropensityService {

    private final PropensityRepository propensityRepository;
    private final ETFRepository etfRepository;

    // I : 방어, E : 공격, S : 가치, N : 성장, T: 저위험, F: 고위험, J : 저변동, P: 고변동
    // ISTJ, ISFJ, INFJ, INTJ, ISTP, ISFP, INFP, INTP, ESTP, ESFP, ENFP, ENTP, ESTJ, ESFJ, ENFJ, ENTJ

    /*
    //주식형(STOCK)
    LARGE_CAP("대형주"),
    SMALL_MID_CAP("중소형"),

    //채권형(BOND)
    DOMESTIC_BOND("국내채권"),
    FOREIGN_BOND("해외채권"),

    //원자재(COMMODITY)
    PRECIOUS_METAL("귀금속"),
    ENERGY("에너지"),

    //섹터(SECTOR)
    IT_SEMICONDUCTOR("IT/반도체"),
    FINANCE("금융"),
    HEALTHCARE("헬스케어"),

    //고위험
    LEVERAGE("레버리지"),
    INVERSE("인버스")
    */

    public Map<String, String> getMBTIDescription(String mbti) {
        Map<String, String> description = new HashMap<>();

        switch (mbti) {
            // 방어적 자산배분형 (I__J)
            case "ISTJ":
                description.put("type", "안정 추구형 투자자");
                description.put("description", "보수적이고 안정적인 투자를 선호하며, 국내 채권형 ETF를 중심으로 안전한 투자를 추구합니다. 철저한 분석과 위험 최소화를 중시합니다.");
                break;
            case "ISFJ":
                description.put("type", "보수적 가치투자자");
                description.put("description", "검증된 안전자산을 선호하며, 해외 채권형 ETF를 통해 안정적인 수익을 추구합니다. 장기적 관점의 안전 투자를 선호합니다.");
                break;
            case "INFJ":
                description.put("type", "신중한 가치투자자");
                description.put("description", "가치 중심의 안전자산을 선호하며, 귀금속 ETF와 같은 안정적인 실물자산에 투자합니다. 장기적 가치 보존을 중시합니다.");
                break;
            case "INTJ":
                description.put("type", "분석적 안전투자자");
                description.put("description", "체계적 분석을 통한 안전자산 투자를 선호하며, 국채와 우량 채권 ETF를 중심으로 투자합니다. 위험 대비 수익성을 중시합니다.");
                break;

            // 공격적 자산배분형 (E__J)
            case "ESTJ":
                description.put("type", "적극적 성과추구형");
                description.put("description", "목표 지향적 투자를 선호하며, 대형주 중심의 주식형 ETF에 투자합니다. 명확한 투자 전략과 실행력을 갖추고 있습니다.");
                break;
            case "ESFJ":
                description.put("type", "균형 성장투자형");
                description.put("description", "안정적 성장을 추구하며, 배당주 ETF와 성장주 ETF를 균형있게 투자합니다. 위험과 수익의 균형을 중시합니다.");
                break;
            case "ENFJ":
                description.put("type", "진취적 가치성장형");
                description.put("description", "미래 가치 중심의 투자를 선호하며, 신성장 산업 ETF에 주로 투자합니다. 장기적 성장 가능성을 중시합니다.");
                break;
            case "ENTJ":
                description.put("type", "전략적 공격투자형");
                description.put("description", "적극적인 수익 추구를 선호하며, 레버리지 ETF와 섹터 ETF를 전략적으로 활용합니다. 높은 수익을 위한 계산된 위험 감수를 특징으로 합니다.");
                break;

            // 방어적 트레이딩형 (I__P)
            case "ISTP":
                description.put("type", "기술적 분석중시형");
                description.put("description", "철저한 기술적 분석을 바탕으로 투자하며, 중소형 가치주 ETF를 선호합니다. 리스크 관리를 중시합니다.");
                break;
            case "ISFP":
                description.put("type", "보수적 실용주의형");
                description.put("description", "안정성과 실용성을 중시하며, 우량 기업 채권형 ETF를 선호합니다. 검증된 투자 방식을 선호합니다.");
                break;
            case "INFP":
                description.put("type", "가치중심 장기투자형");
                description.put("description", "개인의 가치관을 중시하는 투자를 선호하며, ESG ETF나 테마형 ETF에 투자합니다. 장기적 가치 상승을 추구합니다.");
                break;
            case "INTP":
                description.put("type", "논리적 분석형");
                description.put("description", "체계적인 분석과 논리적 접근을 통해 투자하며, 기술주 ETF나 혁신기업 ETF를 선호합니다. 새로운 투자 기회를 분석적으로 발굴합니다.");
                break;

            // 공격적 트레이딩형 (E__P)
            case "ESTP":
                description.put("type", "기회포착형 투자자");
                description.put("description", "단기 매매 기회를 포착하여 투자하며, 변동성이 큰 섹터 ETF나 인버스 ETF를 활용합니다. 빠른 의사결정과 실행력이 특징입니다.");
                break;
            case "ESFP":
                description.put("type", "트렌드 추종형");
                description.put("description", "시장 트렌드를 따라 투자하며, 인기 섹터나 테마 ETF에 투자합니다. 시장 분위기에 민감하게 반응합니다.");
                break;
            case "ENFP":
                description.put("type", "혁신추구형 투자자");
                description.put("description", "새로운 투자 기회와 혁신적인 상품을 선호하며, 신기술 및 4차 산업혁명 관련 ETF에 투자합니다. 높은 성장 잠재력을 추구합니다.");
                break;
            case "ENTP":
                description.put("type", "전략적 트레이딩형");
                description.put("description", "다양한 투자 전략을 구사하며, 레버리지와 인버스 ETF를 적극 활용합니다. 시장 상황에 따른 탄력적인 대응이 특징입니다.");
                break;
            default:
                description.put("type", "일반 투자자");
                description.put("description", "투자 성향을 파악할 수 없습니다. 재분석이 필요합니다.");
        }
        return description;
    }

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
