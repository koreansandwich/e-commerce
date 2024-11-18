package backend.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final List<String> categoryList = Arrays.asList(
            "스킨", "로션", "에센스", "세럼/앰플/미스트", "오일", "크림/올인원", "마스크팩", "선케어"
    );

    public KeywordExtractor() {
        this.restTemplate = new RestTemplate();
    }

    private String createPrompt(String userMessage) {
        StringBuilder keywordPrompt = new StringBuilder("사용자의 메시지를 분석하여 추천 유형과 세부 정보를 추출하세요.\n");
        keywordPrompt.append("1. 추천 유형: 키워드를 통한 제품 추천일 경우 '키워드 추천' 또는 유사한 제품을 통한 제품 추천일 경우 '유사 추천' 중 하나로 분류하세요.\n");
        keywordPrompt.append("2. 추천 유형이 '키워드를 통한 제품 추천'일 경우:\n");

        for (String keyword : keywordsList) {
            keywordPrompt.append("   - ").append(keyword).append("\n");
        }
        keywordPrompt.append("   - 메시지에서 위의 리스트에 있는 키워드만 있는대로 추출하고, 각 키워드에 대해 긍정은 1, 부정은 -1로 매핑하여 JSON 형식으로 반환하세요. 여기서는 카테고리 말고 제품의 특성 리스트에서만 추출하는 겁니다.\n");
        keywordPrompt.append("3. 이제 메시지에서 제품 카테고리를 추출하세요. 사용자가 여러 카테고리를 요청할 경우 모두 JSON 형식으로 추출하세요. 카테고리 키워드는 다음과 같습니다: 카테고리는 JSON 배열 형식으로 반환하세요.\n");

        for (String category : categoryList) {
            keywordPrompt.append("   - ").append(category).append("\n");
        }

        keywordPrompt.append("   - JSON 형식 예시: {추천 유형: '키워드 추천', 키워드: {키워드1: 스코어, 키워드2: 스코어, ...}, 카테고리: {카테고리1, 카테고리2, ...}\n");



        keywordPrompt.append("4. 추천 유형이 '유사한 제품을 통한 제품 추천'일 경우:\n");
        keywordPrompt.append("   - 메시지에서 제품명을 추출하여 JSON 형식으로 {추천 유형: '유사 추천', 제품명: '추출된 제품명'} 형태로 반환하세요.\n");
        keywordPrompt.append(" 부가적인 설명 없이 정확하게 JSON 형식만 반환하세요. 무조건 '{'로 시작하고, '}'로 끝나야 합니다.");

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
        requestBody.put("model", "gpt-4-turbo");
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

                gptResponse = gptResponse.trim();

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
        System.out.println("Parsing GPT Response..." + gptResponse);

        // Step 1: Remove Markdown formatting (e.g., ```json and ```)
        if (gptResponse.startsWith("```json")) {
            gptResponse = gptResponse.substring(7); // Remove "```json"
        }
        if (gptResponse.startsWith("```")) {
            gptResponse = gptResponse.substring(3); // Remove "```"
        }
        if (gptResponse.endsWith("```")) {
            gptResponse = gptResponse.substring(0, gptResponse.length() - 3); // Remove trailing "```"
        }

        // Step 2: Trim any extra spaces
        gptResponse = gptResponse.trim();

        // Step 3: Validate JSON format
        if (!(gptResponse.startsWith("{") && gptResponse.endsWith("}"))) {
            throw new JSONException("Invalid JSON format: Response does not start with '{' or end with '}'");
        }

        try {
            // 정규식을 사용해 JSON 형식으로 시작하는 부분만 추출
            Pattern jsonPattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
            Matcher matcher = jsonPattern.matcher(gptResponse);
            if (matcher.find()) {
                gptResponse = matcher.group();  // JSON 형식 부분만 추출
            } else {
                throw new JSONException("No JSON content found in GPT response");
            }

            JSONObject jsonResponse = new JSONObject(gptResponse);

            JSONArray categories = new JSONArray();
            if (jsonResponse.has("카테고리")) {
                Object categoryObject = jsonResponse.get("카테고리");
                if (categoryObject instanceof JSONArray) {
                    categories = (JSONArray) categoryObject;
                } else if (categoryObject instanceof String) {
                    categories.put(categoryObject);
                }
            }

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

            }
            if (categories != null) {
                List<String> categoryList = new ArrayList<>();
                for (int i = 0; i < categories.length(); i++) {
                    categoryList.add(categories.getString(i));
                }
                result.put("categories", categoryList);  // 카테고리 리스트를 result에 추가
                System.out.println("Categories: " + categoryList);
            } else {
                System.out.println("No categories found.");
            }

            if (recommendationType.equals("유사 추천") && jsonResponse.has("제품명")) {
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
