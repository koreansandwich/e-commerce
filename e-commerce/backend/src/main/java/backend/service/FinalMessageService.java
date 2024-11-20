package backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FinalMessageService {
    private final String OPENAI_API_KEY;

    public FinalMessageService() {
        this.OPENAI_API_KEY = System.getenv("OPENAI_API_KEY"); // 환경 변수에서 API 키 로드
        System.out.println("OPENAI_API_KEY: " + OPENAI_API_KEY);
    }

    /**
     * 추천 제품 데이터를 기반으로 GPT를 호출하여 사용자 메시지를 생성.
     *
     * @param recommendationsJson 추천 제품 데이터를 포함한 JSON 문자열
     * @return 사용자에게 보여줄 자연스러운 메시지
     */
    public String generateMessageFromRecommendations(String recommendationsJson) {
        try {
            // JSON 데이터를 배열로 파싱
            JSONArray recommendationsArray = new JSONArray(recommendationsJson);

            // 프롬프트 생성
            String prompt = createPromptFromRecommendations(recommendationsArray);

            // GPT API 호출
            return callGPTForResponse(prompt);

        } catch (Exception e) {
            System.out.println("Error while processing recommendations: " + e.getMessage());
            e.printStackTrace();
            return "추천 결과를 표시하는 중 오류가 발생했습니다.";
        }
    }

    /**
     * GPT API에 요청을 보내고 응답을 반환.
     *
     * @param prompt 사용자 요청에 맞춘 프롬프트
     * @return GPT로부터 받은 응답 메시지
     */
    private String callGPTForResponse(String prompt) {
        try {
            String apiUrl = "https://api.openai.com/v1/chat/completions";
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);

            // GPT 요청 데이터 생성
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4-turbo");
            requestBody.put("messages", new JSONArray().put(new JSONObject()
                    .put("role", "user")
                    .put("content", prompt)));
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            System.out.println("Sending to GPT: " + requestBody);

            HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject responseBody = new JSONObject(response.getBody());
                String gptResponse = responseBody.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim();

                // 디버깅: 응답에 줄바꿈 확인
                System.out.println("GPT Response: " + gptResponse);

                // 줄바꿈 추가 (필요 시)
                gptResponse = gptResponse.replace("\\n", "\n");

                return gptResponse;
            } else {
                throw new RuntimeException("Failed to call GPT API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Error while calling GPT API: " + e.getMessage());
            e.printStackTrace();
            return "추천 결과를 표시하는 중 오류가 발생했습니다.";
        }
    }

    /**
     * 추천 데이터를 기반으로 GPT에 전달할 프롬프트 생성.
     *
     * @param recommendationsArray 추천 제품 데이터 배열
     * @return GPT에 전달할 프롬프트
     */
    private String createPromptFromRecommendations(JSONArray recommendationsArray) {
        try {
            int maxProducts = Math.min(recommendationsArray.length(), 1); // 최대 1개 제품만 추천

            StringBuilder promptBuilder = new StringBuilder("다음 추천 제품 데이터를 기반으로 간결하고 친근한 메시지를 생성해주세요:\n\n");
            promptBuilder.append("[추천 제품 데이터 시작]\n");

            for (int i = 0; i < maxProducts; i++) {
                JSONObject product = recommendationsArray.getJSONObject(i);
                String name = product.optString("item_name", "제품명 없음");
                String link = product.optString("item_link", "링크 없음");
                String price = product.optString("item_final_price", "가격 정보 없음");
                String brand = product.optString("brand", "브랜드 정보 없음");

                promptBuilder.append("- 제품명: ").append(name).append("\n");
                promptBuilder.append("  브랜드: ").append(brand).append("\n");
                promptBuilder.append("  가격: ").append(price).append("원\n");
                promptBuilder.append("  링크: ").append(link).append("\n\n");
            }

            promptBuilder.append("[추천 제품 데이터 끝]\n\n");
            promptBuilder.append("위 데이터를 바탕으로 사용자에게 제공할 메시지를 생성해주세요. 메시지는 간단하고 친근하며, 가독성을 높이기 위해 줄바꿈을 사용하세요.");
            promptBuilder.append("제품 이름, 브랜드, 가격, 링크를 강조해주세요. 제가 개발 중인 챗봇 시스템에는 '진하게'와 같은 서식이 적용 안 되므로, 서식 설정 없이 텍스트만으로 응답해주세요.");
            promptBuilder.append("다음과 같은 형식으로 메시지를 작성해주세요. 각 정보는 줄바꿈 후에 표시됩니다:\n")
                    .append("제품명: [제품명]\n")
                    .append("가격: [가격]\n")
                    .append("링크: [링크]\n")
                    .append("기타 정보: [기타 정보]\n\n");


            System.out.println("Generated GPT Prompt: " + promptBuilder.toString());
            return promptBuilder.toString();
        } catch (Exception e) {
            System.out.println("Error while creating prompt: " + e.getMessage());
            e.printStackTrace();
            return "추천 데이터를 표시하는 중 오류가 발생했습니다.";
        }
    }
}