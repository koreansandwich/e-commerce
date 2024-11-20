from flask import Flask, request, jsonify
from sqlalchemy import create_engine, text
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.cluster import KMeans

app = Flask(__name__)

# MySQL RDS 연결 설정
DATABASE_URI = "mysql+pymysql://koreansandwich:jeon3750@ecommerce-db.cziiceyekxzs.ap-southeast-2.rds.amazonaws.com:3306/ecommerce"
engine = create_engine(DATABASE_URI)

def load_data_from_rds(category):
    try:
        # SQL 쿼리 디버깅
        print(f"Fetching items for category: {category}")
        query = text("""
            SELECT item_id, item_type
            FROM items
            WHERE item_type = :category
        """)
        items_df = pd.read_sql(query, engine, params={"category": category})
        print(f"Items fetched: {items_df}")

        if items_df.empty:
            return None, "No items found for the given category."

        # item_score 데이터 쿼리
        query_scores = text("""
            SELECT *
            FROM item_score
            WHERE item_id IN :item_ids
        """)
        print(f"Fetching scores for item_ids: {tuple(items_df['item_id'])}")
        scores_df = pd.read_sql(query_scores, engine, params={"item_ids": tuple(items_df["item_id"])})
        print(f"Scores fetched: {scores_df}")

        if scores_df.empty:
            return None, "No scores found for the given items."

        # 데이터 병합
        merged_df = pd.merge(items_df, scores_df, on="item_id")
        print(f"Merged data: {merged_df}")

        # 데이터 정규화
        scaler = StandardScaler()
        feature_columns = scores_df.columns.difference(["item_id"])
        scaled_values = scaler.fit_transform(merged_df[feature_columns])
        normalized_df = pd.DataFrame(scaled_values, columns=feature_columns)
        normalized_df["item_id"] = merged_df["item_id"]
        normalized_df["item_type"] = merged_df["item_type"]

        return normalized_df, None

    except Exception as e:
        print(f"Error loading data from RDS: {e}")
        return None, str(e)


    except Exception as e:
        print(f"Error loading data from RDS: {e}")
        return None, str(e)

def items_for_request(sample_request, df):
    """
    사용자 요청을 기반으로 상위 n%의 아이템을 필터링.
    """
    request_features = {key: value for key, value in sample_request.items() if value != 0}
    percentage = 0.2
    num_of_percentage = round(len(df) * percentage)

    request_columns = list(request_features.keys())
    for key, value in request_features.items():
        df[key] *= value

    temp = df[(df[request_columns] > 0).all(axis=1)]
    temp["request_score"] = temp[request_columns].prod(axis=1)
    requested_df = temp.nlargest(num_of_percentage, "request_score")
    return requested_df

def clustering(sample_request, requested_df):
    """
    필터링된 데이터를 클러스터링하여 추천.
    """
    request_features = {key: value for key, value in sample_request.items() if value != 0}
    request_columns = list(request_features.keys())
    temp_without_request = requested_df.drop(columns=request_columns)
    temp = temp_without_request.select_dtypes(exclude=["object"])

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
    temp["group"] = kmeans.fit_predict(temp)
    temp["item_id"] = requested_df["item_id"]
    temp["item_type"] = requested_df["item_type"]

    return temp

@app.route("/recommend", methods=["POST"])
def recommend():
    sample_request = request.json
    categories = sample_request.get("카테고리", ["기본카테고리"])
    category = categories[0]

    # RDS에서 데이터 로드
    df, error = load_data_from_rds(category)
    if df is None:
        return jsonify({"error": error}), 404

    # 요청된 키워드로 필터링하고 클러스터링
    keywords = sample_request.get("키워드", {})
    rdf = items_for_request(keywords, df)
    cluster_df = clustering(keywords, rdf)

    max_request_scores = cluster_df.loc[cluster_df.groupby("group")["request_score"].idxmax()]

    # 추천된 item_id로 전체 정보 가져오기
    recommended_ids = max_request_scores["item_id"].tolist()

    query_details = text("""
        SELECT * 
        FROM items
        WHERE item_id IN :item_ids
    """)
    detailed_items = pd.read_sql(query_details, engine, params={"item_ids": tuple(recommended_ids)})

    # JSON 응답 생성
    recommendations = detailed_items.to_dict(orient="records")
    return jsonify(recommendations)


if __name__ == "__main__":
    app.run(port=5000, debug=True)
