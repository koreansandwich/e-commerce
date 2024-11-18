from flask import Flask, request, jsonify
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import KMeans
import numpy as np

app = Flask(__name__)

# 카테고리별 파일 경로를 매핑한 딕셔너리
CATEGORY_FILE_PATHS = {
    "스킨": "skin_data.csv",
    "로션": "lotion_data.csv",
    "에센스": "essence_data.csv",
    "세럼/앰플/미스트": "serum_ampoule_mist_data.csv",
    "오일": "oil_data.csv",
    "크림/올인원": "cream_all_in_one_data.csv",
    "마스크팩": "mask_pack_data.csv",
    "선케어": "suncare_keyword_score.csv"
}

def load_data(category):
    # 카테고리 값에 따라 해당 파일 경로에서 데이터 로드
    file_path = CATEGORY_FILE_PATHS.get(category, CATEGORY_FILE_PATHS["선케어"])  # 카테고리가 없으면 "선케어" 파일 로드
    try:
        df = pd.read_csv(file_path)
        # 데이터 정규화
        scaler = StandardScaler()
        scaled_values = scaler.fit_transform(df.drop(columns=['Unnamed: 0', 'link', 'name']))
        normalized_df = pd.DataFrame(scaled_values, columns=df.columns[3:28])
        normalized_df['name'] = df['name']
        normalized_df['link'] = df['link']
        return normalized_df
    except FileNotFoundError:
        print(f"Error: File for category '{category}' not found.")
        return None

# 1. 요청에 맞는 아이템 상위 n%의 df를 리턴하는 함수
def items_for_request(sample_request, df):
    request_features = {key: value for key, value in sample_request.items() if value != 0}
    percentage = 0.2
    num_of_percentage = round(len(df) * percentage)
    request_columns = list(request_features.keys())
    for key, value in request_features.items():
        df[key] *= value
    temp = df[(df[request_columns] > 0).all(axis=1)]
    temp['request_score'] = temp[request_columns].prod(axis=1)
    requested_df = temp.nlargest(num_of_percentage, 'request_score')
    return requested_df

# 2. 나머지 특성을 이용해 클러스터링
def clustering(sample_request, requested_df):
    request_features = {key: value for key, value in sample_request.items() if value != 0}
    request_columns = list(request_features.keys())
    temp_without_request = requested_df.drop(columns=request_columns)
    temp = temp_without_request.select_dtypes(exclude=['object'])

    wcss = []
    max_k = int(len(requested_df) / 1.5)
    for k in range(1, max_k + 1):
        kmeans = KMeans(n_clusters=k, random_state=42)
        kmeans.fit(temp)
        wcss.append(kmeans.inertia_)

    wcss_diff = [wcss[i] - wcss[i + 1] for i in range(len(wcss) - 1)]
    threshold = 1
    optimal_k = 4

    for i in range(len(wcss_diff)):
        if abs(wcss_diff[i]) < threshold:
            optimal_k = i + 2
            break

    kmeans = KMeans(n_clusters=optimal_k, random_state=42)
    temp['group'] = kmeans.fit_predict(temp)
    temp['name'] = requested_df['name']
    temp['link'] = requested_df['link']

    return temp

# API 엔드포인트
@app.route('/recommend', methods=['POST'])
def recommend():
    # Java에서 보낸 JSON 데이터를 받아서 sample_request 변수에 저장
    sample_request = request.json
    # 카테고리를 확인하고 데이터를 로드
    categories = sample_request.get("카테고리", ["선케어"])  # 카테고리가 없을 때는 기본값으로 "선케어"
    category = categories[0]  # 첫 번째 카테고리 선택 (다중 카테고리는 우선 하나로 처리)
    df = load_data(category)

    if df is None:
        return jsonify({"error": f"Data for category '{category}' not found"}), 404

    # 요청된 키워드로 필터링하고 클러스터링
    keywords = sample_request.get("키워드", {})
    rdf = items_for_request(keywords, df)
    cluster_df = clustering(keywords, rdf)

    max_request_scores = cluster_df.loc[cluster_df.groupby('group')['request_score'].idxmax()]
    max_request_scores.drop(columns=['request_score', 'group'], inplace=True)

    # JSON 응답 데이터 생성
    recommendations = []
    for _, row in max_request_scores.iterrows():
        product = {
            "name": row['name'],
            "link": row['link'],
            "features": {column: ("↑" if row[column] > 0 else "↓" if row[column] < 0 else "-")
                         for column in max_request_scores.select_dtypes(exclude=['object']).columns}
        }
        recommendations.append(product)

    return jsonify(recommendations)

if __name__ == '__main__':
    app.run(port=5000)
