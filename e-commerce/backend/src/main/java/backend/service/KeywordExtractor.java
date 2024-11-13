package backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KeywordExtractor {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private final RestTemplate restTemplate;

    private static final List<String> keywordsList = Arrays.asList(
            "수분", "유분", "보습", "속건조", "자극", "진정", "탄력",
            "윤기", "트러블", "트러블개선", "미백효과", "피부톤개선",
            "피부결개선", "주름개선", "모공관리", "각질제거", "흡수력",
            "무게감", "밀림", "흘러내림", "미끌거림", "끈적임", "향", "양"
    );

    public KeywordExtractor() {
        this.restTemplate = new RestTemplate();
    }

    private String createPrompt(String userMessage) {
        StringBuilder keywordPrompt = new StringBuilder("사용자의 메시지를 분석하여 추천 유형과 세부 정보를 추출하세요.\n");
        keywordPrompt.append("1. 추천 유형: '키워드를 통한 제품 추천' 또는 '유사한 제품을 통한 제품 추천' 중 하나로 분류하세요.\n");
        keywordPrompt.append("2. 추천 유형이 '키워드를 통한 제품 추천'일 경우:\n");
        keywordPrompt.append("   - 메시지에서 관련 있는 모든 키워드를 추출하고, 각 키워드에 대해 긍정은 1, 부정은 -1로 매핑하여 JSON 형식으로 반환하세요.\n");
        keywordPrompt.append("   - JSON 형식 예시: {추천 유형: '키워드 추천', 키워드: {키워드1: 스코어, 키워드2: 스코어, ...}}\n");

        for (String keyword : keywordsList) {
            keywordPrompt.append("   - ").append(keyword).append("\n");
        }

        keywordPrompt.append("3. 추천 유형이 '유사한 제품을 통한 제품 추천'일 경우:\n");
        keywordPrompt.append("   - 메시지에서 제품명을 추출하여 JSON 형식으로 {추천 유형: '유사 추천', 제품명: '추출된 제품명'} 형태로 반환하세요.\n");

        String prompt = keywordPrompt.toString() + "\n사용자 메시지: \"" + userMessage + "\"\n" + "결과를 JSON 형식으로 반환합니다: 추천 유형과 관련 데이터.\n";
        System.out.println("Generated Prompt: " + prompt);
        return prompt;
    }

    public Map<String, Object> analyzeMessageWithGPT(String userMessage) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        String prompt = createPrompt(userMessage);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new JSONArray().put(new JSONObject()
                .put("role", "user")
                .put("content", prompt)));
        requestBody.put("max_tokens", 250);
        requestBody.put("temperature", 0.5);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            System.out.println("Sending request to OpenAI API...");
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);

            System.out.println("Response Status Code: " + responseEntity.getStatusCode());
            System.out.println("Response Body: " + responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                JSONObject responseJson = new JSONObject(responseEntity.getBody());
                String gptResponse = responseJson.getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content");

                System.out.println("GPT Response Content: " + gptResponse);
                return parseGPTResponse(gptResponse);

            } else {
                System.err.println("Failed to get response from OpenAI API. Status Code: " + responseEntity.getStatusCode());
                throw new RuntimeException("Failed to get response from OpenAI API. Status Code: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Exception occurred during GPT request: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error during GPT request", e);
        }
    }

    private Map<String, Object> parseGPTResponse(String gptResponse) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("Parsing GPT Response...");

        try {
            JSONObject jsonResponse = new JSONObject(gptResponse);

            String recommendationType = jsonResponse.getString("추천 유형");
            result.put("recommendationType", recommendationType);
            System.out.println("Recommendation Type: " + recommendationType);

            if (recommendationType.equals("키워드 추천") && jsonResponse.has("키워드")) {
                JSONObject keywords = jsonResponse.getJSONObject("키워드");
                Map<String, Integer> keywordScores = new HashMap<>();
                for (String key : keywords.keySet()) {
                    keywordScores.put(key, keywords.getInt(key));
                }
                result.put("keywords", keywordScores);
                System.out.println("Extracted Keywords and Scores: " + keywordScores);

            } else if (recommendationType.equals("유사 추천") && jsonResponse.has("제품명")) {
                result.put("productName", jsonResponse.getString("제품명"));
                System.out.println("Extracted Product Name: " + jsonResponse.getString("제품명"));
            }

        } catch (Exception e) {
            System.err.println("Error parsing GPT response: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse GPT response", e);
        }

        return result;
    }

}
