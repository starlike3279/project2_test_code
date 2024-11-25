package com.example.domain.fund.service;

import com.example.domain.fund.accessToken.AccessTokenManager;
import com.example.domain.fund.config.OpenApiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
@Slf4j
public class ETFService {
    private final OpenApiConfig openApiConfig; // API URL과 인증정보를 포함하는 설정 클래스
    private final RestTemplate restTemplate; // HTTP 요청을 처리하기 위한 Spring의 HTTP 클라이언트
    private final AccessTokenManager accessTokenManager; // API 인증 토큰을 관리하는 클래스
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT); //JSON 데이터를 처리하는 Jackson의 유틸리티 클래스

    public String getStockInfo(String stockCode) {
        // ETF 정보를 문자열 형태로 누적하여 저장
        StringBuilder result = new StringBuilder();
        try {
            String token = accessTokenManager.getAccessToken();
            // 토큰 디버깅
            if (token == null || token.isEmpty()) {
                log.error("액세스 토큰이 비어있습니다.");
                return "인증 토큰 오류가 발생했습니다.";
            }
            log.info("Access Token 확인: [{}]", token);

            String basicInfo = getBasicInfo(stockCode, token);
            String componentInfo = getComponentInfo(stockCode, token);
            String investorInfo = getInvestorInfo(stockCode, token);

            // 각 데이터 정보가 null이 아닐 경우 누적 저장
            if (basicInfo != null) result.append(basicInfo).append("\n\n");
            if (componentInfo != null) result.append(componentInfo).append("\n\n");
            if (investorInfo != null) result.append(investorInfo);

            return result.length() > 0 ? result.toString() : "ETF 정보를 불러올 수 없습니다.";
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            return "ETF 정보를 불러올 수 없습니다: " + e.getMessage();
        }
    }

    private String getBasicInfo(String stockCode, String token) {
        try {
            log.info("========== 기본 정보 조회 시작 ==========");
            String url = openApiConfig.getBaseUrl() + "/uapi/domestic-stock/v1/quotations/inquire-price";
            // API 호출의 JSON 응답 데이터를 JsonNode 객체로 반환
            JsonNode response = makeApiCall(url, stockCode, "FHKST01010100", token);

            if (response != null && response.has("output")) {
                JsonNode output = response.get("output");
//                log.info("기본 정보 응답: {}", output.toPrettyString());
                return String.format("""
                        ETF 기본 정보:
                        종목명: %s
                        현재가: %s원
                        전일대비: %s원
                        등락률: %s%%""",
//                      getNodeTextSafely 메서드
//                      - output 객체에서 특정 필드를 안전하게 추출하는 유틸리티 메서드
//                      - 키가 없거나 값이 null인 경우, 기본값을 반환하여 오류를 방지
                        getNodeTextSafely(output, "hts_kor_isnm", "N/A"),
                        getNodeTextSafely(output, "stck_prpr", "0"),
                        getNodeTextSafely(output, "prdy_vrss", "0"),
                        getNodeTextSafely(output, "prdy_ctrt", "0"));
            }
            return "기본 정보를 불러올 수 없습니다.";
        } catch (Exception e) {
//            log.error("기본 정보 조회 중 오류 발생", e);
            return "기본 정보 조회 중 오류가 발생했습니다.";
        }
    }

    private String getComponentInfo(String stockCode, String token) {
        try {
            log.info("========== ETF 구성종목 정보 조회 시작 ==========");
            String url = openApiConfig.getBaseUrl() + "/uapi/etfetn/v1/quotations/inquire-component-stock-price";
//          makeApiCall: API 요청을 수행하고 결과를 JSON 형식으로 반환
            JsonNode response = makeApiCall(url, stockCode,"FHKST121600C0", token);

            if (response == null) {
                log.error("API 응답이 null입니다.");
                return "구성종목 정보를 불러올 수 없습니다.";
            }

            log.info("전체 응답: {}", response.toPrettyString());

            // API 응답 상태 코드 확인
            // rt_cd: 0이면 성공, 그외 실패, 기본값으로 999를 사용해 오류 시 디버깅을 용이하게 만듦.
            String rtCd = getNodeTextSafely(response, "rt_cd", "999");
            if (!"0".equals(rtCd)) {
                String errorMsg = getNodeTextSafely(response, "msg1", "알 수 없는 오류");
                log.error("API 응답 에러 - 코드: {}, 메시지: {}", rtCd, errorMsg);
                return String.format("API 오류 - 코드: %s, 메시지: %s", rtCd, errorMsg);
            }

            // output2 확인 및 처리
            if (response.has("output2")) {
                JsonNode output2 = response.get("output2");
                StringBuilder componentInfo = new StringBuilder();
                componentInfo.append("========== ETF 구성종목 정보 ==========\n\n");
                componentInfo.append(String.format("%-6s %-20s %-15s %-15s %-15s\n",
                        "번호", "종목명", "현재가", "전일대비", "등락률"));
                componentInfo.append("=".repeat(80)).append("\n");

                // 데이터가 배열인지 객체인지 확인
//              배열:
//                최대 10개의 구성종목 데이터를 순회하며 처리.
//              객체:
//                단일 구성종목 데이터를 처리.
                if (output2.isArray()) {
                    for (int i = 0; i < Math.min(10, output2.size()); i++) { // 최대 10개의 종목만 표시
                        JsonNode component = output2.get(i);
                        processComponentData(component, componentInfo, i);
                    }
                } else {
                    log.info("output2는 배열이 아닙니다. 객체로 처리합니다.");
                    processComponentData(output2, componentInfo, 0);
                }

                componentInfo.append("\n구성종목 정보 처리가 완료되었습니다.");
                log.info("ETF 구성종목 정보 처리 결과:\n{}", componentInfo);

                return componentInfo.toString();
            } else {
                log.warn("output2가 응답에 존재하지 않습니다.");
                return "구성종목 정보를 불러올 수 없습니다.";
            }
        } catch (Exception e) {
            log.error("ETF 구성종목 정보 조회 중 오류 발생", e);
            return "ETF 구성종목 정보를 조회 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // 구성종목 데이터 처리 로직
    private void processComponentData(JsonNode component, StringBuilder componentInfo, int index) {
        try {
            String stockName = getNodeTextSafely(component, "hts_kor_isnm", "N/A"); // 종목명
            String currentPrice = getNodeTextSafely(component, "stck_prpr", "0"); // 현재가
            String priceChange = getNodeTextSafely(component, "prdy_vrss", "0"); // 전일대비
            String changeRate = getNodeTextSafely(component, "prdy_ctrt", "0"); // 등락률

            componentInfo.append(String.format("%-6d %-20s %-15s %-15s %-15s\n",
                    index + 1,
                    stockName,
                    formatPrice(currentPrice),
                    formatPriceChange(priceChange),
                    formatRate(changeRate)));

            log.info("구성종목 {}: 종목명={}, 현재가={}, 전일대비={}, 등락률={}",
                    index + 1, stockName, currentPrice, priceChange, changeRate);
        } catch (Exception e) {
            log.error("구성종목 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private String getInvestorInfo(String stockCode, String token) {
        try {
            log.info("========== 투자자 정보 조회 시작 ==========");
            String url = openApiConfig.getBaseUrl() + "/uapi/domestic-stock/v1/quotations/inquire-investor";
            JsonNode response = makeApiCall(url, stockCode, "FHKST01010900", token);

            if (response != null) {
                log.info("투자자 정보 전체 응답: {}", response.toPrettyString());

                JsonNode output = response.get("output");
                if (output != null) {
                    log.info("투자자 정보 데이터: {}", output.toPrettyString());

                    // 데이터 추출
                    String indivQty = getNodeTextSafely(output, "pp_svolume", "0");
                    String foreignQty = getNodeTextSafely(output, "fo_svolume", "0");
                    String instQty = getNodeTextSafely(output, "it_svolume", "0");

                    // 포맷팅된 결과 생성
                    String result = String.format("""
                        ETF 투자자 정보:
                        개인 순매수량: %s주
                        외국인 순매수량: %s주
                        기관 순매수량: %s주""",
                            formatNumber(indivQty),
                            formatNumber(foreignQty),
                            formatNumber(instQty));

                    log.info("투자자 정보 처리 결과:\n{}", result);
                    return result;
                }
            }
            return "투자자 정보를 불러올 수 없습니다.";
        } catch (Exception e) {
            log.error("투자자 정보 조회 중 오류 발생", e);
            return "투자자 정보 조회 중 오류가 발생했습니다.";
        }
    }

    private JsonNode makeApiCall(String url, String stockCode, String trId, String token, String date) {
        try {
            if (token == null || token.isEmpty()) {
                log.error("토큰이 비어있습니다.");
                return null;
            }

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", "Bearer " + token);
            headers.set("appkey", openApiConfig.getApiKey());
            headers.set("appsecret", openApiConfig.getSecretKey());
            headers.set("tr_id", trId);
            headers.set("custtype", "P"); // 고객유형 : p(개인)
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 URL 설정
            StringBuilder queryString = new StringBuilder();
            queryString.append("?FID_COND_MRKT_DIV_CODE=J");
            queryString.append("&FID_INPUT_ISCD=").append(stockCode);
            String requestUrl = url + queryString.toString();

            // 디버깅 로그
            log.info("========== API 요청 정보 ==========");
            log.info("URL: {}", requestUrl);
            log.info("TR_ID: {}", trId);
            log.info("Token: {}", token);
            log.info("AppKey: {}", openApiConfig.getApiKey());
            log.info("Headers: {}", headers);

            // API 호출
            // RestTemplate.exchange:
            // -> API를 호출하고, 응답을 처리하는 Spring의 HTTP 클라이언트 메서드.
            // -> 요청 메서드: HttpMethod.GET.
            // -> 응답 형식: String으로 반환.
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // 응답 처리
            log.info("HTTP Status: {}", response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String responseBody = response.getBody();
                log.info("Raw Response: {}", responseBody);

                // JSON 변환:
                // 응답 본문을 JSON 형식(JsonNode)으로 변환.
                // Jackson의 ObjectMapper를 사용하여 처리.
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                // 응답 로깅
                log.info("Response rt_cd: {}", jsonResponse.path("rt_cd").asText());
                log.info("Response msg_cd: {}", jsonResponse.path("msg_cd").asText());
                log.info("Response msg1: {}", jsonResponse.path("msg1").asText());

                if (jsonResponse.has("output1") || jsonResponse.has("output2")) {
                    log.info("Data found in response");
                    if (jsonResponse.has("output1")) {
                        log.info("output1 data: {}", jsonResponse.get("output1").toString());
                    }
                    if (jsonResponse.has("output2")) {
                        log.info("output2 data: {}", jsonResponse.get("output2").toString());
                    }
                } else {
                    log.warn("No output data found in response");
                }

                return jsonResponse;
            }

            log.error("API call failed with status: {}", response.getStatusCode());
            return null;
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    // makeApiCall 오버로딩 메서드도 수정
    private JsonNode makeApiCall(String url, String stockCode, String trId, String token) {
        return makeApiCall(url, stockCode, trId, token, null);
    }

    // 포맷팅 유틸리티 메서드들
    private String formatPrice(String price) {
        try {
            return String.format("%,d원", Long.parseLong(price));
        } catch (NumberFormatException e) {
            return price + "원";
        }
    }

    private String formatPriceChange(String change) {
        try {
            long value = Long.parseLong(change);
            return String.format("%+,d원", value);
        } catch (NumberFormatException e) {
            return change + "원";
        }
    }

    private String formatRate(String rate) {
        try {
            double value = Double.parseDouble(rate);
            return String.format("%.2f", value);
        } catch (NumberFormatException e) {
            return rate;
        }
    }

    private String formatNumber(String number) {
        try {
            return String.format("%,d", Long.parseLong(number));
        } catch (NumberFormatException e) {
            return number;
        }
    }

    // JSON 노드에서 필드를 안전하게 추출
    private String getNodeTextSafely(JsonNode node, String fieldName, String defaultValue) {
        if (node == null || !node.has(fieldName) || node.get(fieldName).isNull()) {
            return defaultValue;
        }
        return node.get(fieldName).asText(); // 필드가 존재하고 값이 null이 아니면, 해당 필드의 값을 문자열로 반환.
    }
}