package backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
public class RecommendationService {

    // 키워드 추천을 처리할 Python API URL
    private static final String KEYWORD_RECOMMENDATION_API_URL = "http://localhost:5000/recommend";
    // 유사 추천을 처리할 Python API URL
    private static final String SIMILAR_RECOMMENDATION_API_URL = "http://localhost:5001/recommend_similar";;

    public String getRecommendations(String recommendationType, Map<String, Object> sampleRequest) {
        try {
            // 추천 유형에 따라 다른 API URL 설정
            String apiUrl;
            if ("키워드 추천".equals(recommendationType)) {
                apiUrl = KEYWORD_RECOMMENDATION_API_URL;
            } else if ("유사 추천".equals(recommendationType)) {
                apiUrl = SIMILAR_RECOMMENDATION_API_URL; // 추후 수정 apiUrl = SIMILAR_RECOMMENDATION_API_URL;
            } else {
                throw new IllegalArgumentException("Invalid recommendation type: " + recommendationType);
            }

            // HTTP 연결 설정
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // JSON 요청 본문 작성
            JSONObject jsonInput = new JSONObject(sampleRequest);

            jsonInput.put("추천 유형", sampleRequest.get("recommendationType")); // 추천 유형 추가
            if ("유사 추천".equals(recommendationType)) {
                // 제품명 추출
                String productName = (String) sampleRequest.get("productName"); // "제품명" 대신 "productName" 사용
                if (productName == null || productName.isEmpty()) {
                    throw new IllegalArgumentException("제품명(productName)이 없습니다.");
                }

                // 추천 카테고리 추출
                List<String> categories = (List<String>) sampleRequest.get("categories"); // "카테고리" 대신 "categories" 사용
                if (categories == null || categories.isEmpty()) {
                    throw new IllegalArgumentException("카테고리(categories)가 없습니다.");
                }

                // JSON 구성
                jsonInput.put("productName", productName);
                jsonInput.put("category", categories.get(0)); // 첫 번째 카테고리만 전송
            }


            // "키워드 추천"에 대한 요청 데이터 처리
            if ("키워드 추천".equals(recommendationType)) {
                if (sampleRequest.containsKey("keywords")) {
                    jsonInput.put("키워드", new JSONObject((Map<String, Object>) sampleRequest.get("keywords"))); // 키워드 추가
                }
                if (sampleRequest.containsKey("categories")) {
                    jsonInput.put("카테고리", new JSONArray((List<String>) sampleRequest.get("categories"))); // 카테고리 추가
                }
            }

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 받기
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            return response.toString();  // JSON 형식의 추천 결과 반환
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

