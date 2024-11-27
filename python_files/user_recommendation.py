from sqlalchemy import create_engine, text
import pandas as pd
from flask import Flask, request, jsonify
import os

# 데이터베이스 연결
DATABASE_URI = "mysql+pymysql://koreansandwich:jeon3750@ecommerce-db.cziiceyekxzs.ap-southeast-2.rds.amazonaws.com:3306/ecommerce"
engine = create_engine(DATABASE_URI)

def fetch_data():
    query = text("""
        SELECT 
            i.item_id,
            i.item_name,
            i.item_type,
            i.item_final_price,
            i.item_link,
            i.brand,
            i.item_image_url,
            u.gender,
            CASE
                WHEN u.age BETWEEN 10 AND 19 THEN '10-19'
                WHEN u.age BETWEEN 20 AND 29 THEN '20-29'
                WHEN u.age BETWEEN 30 AND 39 THEN '30-39'
                WHEN u.age BETWEEN 40 AND 49 THEN '40-49'
                WHEN u.age BETWEEN 50 AND 59 THEN '50-59'
                ELSE '60+'
            END AS age_group,
            AVG(h.rating) AS avg_rating,
            COUNT(h.rating) AS review_count
        FROM user_dummy_history h
        JOIN user_dummy u ON h.user_dummy_id = u.user_dummy_id
        JOIN items i ON h.item_id = i.item_id
        GROUP BY i.item_id, i.item_type, u.gender, age_group;
    """)

    with engine.connect() as connection:
        df = pd.read_sql(query, connection)

    return df

def calculate_scores(df):
    df['score'] = df['avg_rating'] * 0.7 + (df['review_count'] ** 0.3) * 0.3
    recommendations = (
        df.groupby(['age_group', 'gender', 'item_type'], as_index=False)
        .apply(lambda x: x.nlargest(1, 'score'))
        .reset_index(drop=True)
    )
    return recommendations

def store_recommendations(df):
    """
    점수 계산된 데이터프레임을 recommendations 테이블에 저장.
    """
    # MySQL 테이블에 필요한 컬럼만 선택
    columns_to_store = ['item_id', 'item_type', 'gender', 'age_group', 'score', 'avg_rating', 'review_count']
    recommendations_df = df[columns_to_store]

    # MySQL에 저장
    with engine.connect() as connection:
        for _, row in recommendations_df.iterrows():
            print(f"[DEBUG] Preparing to insert row: {row.to_dict()}")  # 디버깅용 출력
            query = text("""
                REPLACE INTO recommendations (item_id, item_type, gender, age_group, score, avg_rating, review_count)
                VALUES (:item_id, :item_type, :gender, :age_group, :score, :avg_rating, :review_count)
            """)
            try:
                connection.execute(query, row.to_dict())  # SQL 실행
                print(f"[DEBUG] Successfully inserted row: {row.to_dict()}")  # 성공 메시지
            except Exception as e:
                print(f"[ERROR] Failed to insert row: {row.to_dict()} - Error: {e}")  # 에러 메시지 출력



def process_and_store_recommendations():
    """
    데이터를 처리하고 recommendation 테이블에 저장.
    """
    print("[INFO] Fetching data from the database...")
    df = fetch_data()
    print(df.head())# SQL에서 데이터 가져오기
    print("[INFO] Calculating scores...")
    recommendations = calculate_scores(df)
    print(recommendations.head())# 점수 계산
    # 필요한 컬럼만 선택
    recommendations = recommendations[['item_id', 'item_type', 'gender', 'age_group', 'score', 'avg_rating', 'review_count']]
    print(recommendations.head())  # 확인용 출력
    print("[INFO] Storing recommendations...")
    store_recommendations(recommendations)  # 데이터베이스 저장
    print("[INFO] Recommendations stored successfully!")

app = Flask(__name__)

@app.route('/recommend_user', methods=['POST'])
def recommend_user():
    try:
        df = fetch_data()
        print(df.head())
        req_data = request.json
        gender = req_data.get('gender')
        age = req_data.get('age')
        item_type = req_data.get('item_type', None)

        if 10 <= age <= 19:
            age_group = '10-19'
        elif 20 <= age <= 29:
            age_group = '20-29'
        elif 30 <= age <= 39:
            age_group = '30-39'
        elif 40 <= age <= 49:
            age_group = '40-49'
        elif 50 <= age <= 59:
            age_group = '50-59'
        else:
            age_group = '60+'

        recommendations = calculate_scores(df)
        user_recommendations = recommendations[
            (recommendations['age_group'] == age_group) &
            (recommendations['gender'] == gender)
            ]

        if item_type:
            user_recommendations = user_recommendations[
                user_recommendations['item_type'] == item_type
                ]

        return jsonify(user_recommendations.to_dict(orient='records')), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 500



def save_to_csv(df, file_name="recommendations.csv"):
    """
    데이터프레임을 CSV 파일로 저장합니다.
    """
    output_dir = "output_files"  # CSV 파일 저장 폴더
    os.makedirs(output_dir, exist_ok=True)  # 폴더가 없으면 생성
    file_path = os.path.join(output_dir, file_name)
    df.to_csv(file_path, index=False, encoding="utf-8-sig")  # CSV 저장
    print(f"[INFO] Recommendations saved to {file_path}")
    return file_path


if __name__ == '__main__':
    # 데이터 처리
    df = fetch_data()
    recommendations = calculate_scores(df)
    save_to_csv(recommendations)  # 데이터프레임을 CSV로 저장

