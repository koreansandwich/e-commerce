package backend.service;

import java.util.HashMap;
import java.util.Map;

public class KeywordExtractor {

    private static final Map<String, String[]> keywordsList = new HashMap<>();

    static {
        keywordsList.put("수분", new String[]{"수분많은", "수분없는"});
        keywordsList.put("유분", new String[]{"유분많은", "유분없는"});
        keywordsList.put("보습", new String[]{"보습잘되는", "보습안되는"});
        keywordsList.put("속건조", new String[]{"속건조에효과있는", "속건조에효과없는"});
        keywordsList.put("자극", new String[]{"자극있는", "자극없는"});
        keywordsList.put("진정", new String[]{"진정되는", "진정안되는"});
        keywordsList.put("탄력", new String[]{"탄력생기는", "탄력안생기는"});
        keywordsList.put("윤기", new String[]{"윤기나는", "윤기나지않는"});
        keywordsList.put("트러블", new String[]{"트러블생기는", "트러블생기지않는"});
        keywordsList.put("트러블개선", new String[]{"트러블없어지는", "트러블없어지지않는"});
        keywordsList.put("미백효과", new String[]{"미백효과있는", "미백효과없는"});
        keywordsList.put("피부톤개선", new String[]{"피부톤이개선되는", "피부톤이개선되지않는"});
        keywordsList.put("피부결개선", new String[]{"피부결좋아지는", "피부결이좋아지지않는"});
        keywordsList.put("주름개선", new String[]{"주름없어지는", "주름안없어지는"});
        keywordsList.put("모공관리", new String[]{"모공관리되는", "모공관리안되는"});
        keywordsList.put("각질제거", new String[]{"각질제거잘되는", "각질제거안되는"});
        keywordsList.put("흡수력", new String[]{"흡수잘되는", "흡수안되는"});
        keywordsList.put("무게감", new String[]{"무거운", "가벼운"});
        keywordsList.put("밀림", new String[]{"밀림있는", "밀림없는"});
        keywordsList.put("흘러내림", new String[]{"흘러내리는", "흘러내리지않는"});
        keywordsList.put("미끌거림", new String[]{"미끌거리는", "미끌거리지않는"});
        keywordsList.put("끈적임", new String[]{"끈적이는", "끈적이지않는"});
        keywordsList.put("향", new String[]{"향이좋은", "향이별로인"});
        keywordsList.put("양", new String[]{"양많은", "양적은"});
    }

    public static Map<String, Integer> extractKeywordsWithScore(String userMessage) {
        Map<String, Integer> keywordScores = new HashMap<>();

        for (Map.Entry<String, String[]> entry : keywordsList.entrySet()) {
            String keyword = entry.getKey();
            String[] options = entry.getValue();

            if (userMessage.contains(options[0])) {
                keywordScores.put(keyword, 1);
            } else if (userMessage.contains(options[1])) {
                keywordScores.put(keyword, -1);
            }
        }
        return keywordScores;
    }
}
