from flask import Flask, request, jsonify
import pymysql

# Flask 앱 생성
app = Flask(__name__)

# MySQL RDS 연결 설정
DB_CONFIG = {
    'host': 'ecommerce-db.cziiceyekxzs.ap-southeast-2.rds.amazonaws.com',
    'user': 'koreansandwich',
    'password': 'jeon3750',
    'database': 'ecommerce',
    'cursorclass': pymysql.cursors.DictCursor  # 결과를 딕셔너리 형태로 반환
}

# 데이터베이스 연결 함수
def get_db_connection():
    try:
        print("[DEBUG] Connecting to the database...")
        return pymysql.connect(**DB_CONFIG)
    except Exception as e:
        raise Exception(f"Database connection failed: {str(e)}")

@app.route('/recommend_similar', methods=['POST'])
def recommend_similar():
    """
    유사 추천 API
    요청 형식:
    {
        "productName": "EANVIE 엔비 솔루션 수딩 로션 120ml, 120ml, 2개",
        "category": "cream"
    }
    """
    connection = None  # connection 변수 선언
    try:
        # 요청 데이터 출력
        req_data = request.json
        print(f"[DEBUG] Received request data: {req_data}")

        # 요청 데이터 추출
        product_name = req_data.get("productName")
        category = req_data.get("category")
        print(f"[DEBUG] Extracted productName: {product_name}")
        print(f"[DEBUG] Extracted category: {category}")

        # 필수 데이터 검증
        if not product_name or not category:
            print("[DEBUG] Missing productName or category in request data")
            return jsonify({"error": "productName과 category는 필수 입력값입니다."}), 400

        # 데이터베이스 연결
        connection = get_db_connection()
        with connection.cursor() as cursor:
            # 1. items 테이블에서 item_id 조회
            find_item_sql = """
                SELECT item_id
                FROM items
                WHERE item_name = %s
            """
            print(f"[DEBUG] Executing query to find item_id: {find_item_sql} with value ({product_name})")
            cursor.execute(find_item_sql, (product_name,))
            item_result = cursor.fetchone()
            print(f"[DEBUG] Query result for item_id: {item_result}")

            # item_id 조회 실패 시 처리
            if not item_result:
                print("[DEBUG] No item found for given productName")
                return jsonify({"error": "해당 productName에 대한 item_id를 찾을 수 없습니다."}), 404

            item_id = item_result['item_id']

            # 2. similar_recommendations 테이블에서 recommended_item_id 조회
            recommendation_sql = """
                SELECT recommended_item_id
                FROM similar_recommendations
                WHERE item_id = %s AND category = %s
            """
            print(f"[DEBUG] Executing query to find recommended_item_id: {recommendation_sql} with values ({item_id}, {category})")
            cursor.execute(recommendation_sql, (item_id, category))
            recommendation_result = cursor.fetchone()
            print(f"[DEBUG] Recommendation query result: {recommendation_result}")

            # 추천 결과 처리
            if not recommendation_result:
                print("[DEBUG] No recommendation found for given item_id and category")
                return jsonify({"error": "해당 item_id와 category에 대한 추천 데이터가 없습니다."}), 404

            recommended_item_id = recommendation_result['recommended_item_id']
            print(f"[DEBUG] Found recommended_item_id: {recommended_item_id}")

            # 3. items 테이블에서 recommended_item_id로 제품 정보 조회
            detail_sql = """
                SELECT item_id, item_name, item_link, item_final_price, brand, item_image_url
                FROM items
                WHERE item_id = %s
            """
            print(f"[DEBUG] Executing detail query: {detail_sql} with value ({recommended_item_id})")
            cursor.execute(detail_sql, (recommended_item_id,))
            item_details = cursor.fetchone()
            print(f"[DEBUG] Detail query result: {item_details}")

            # 응답 생성
            if item_details:
                response = [{
                    "item_id": item_details['item_id'],
                    "item_name": item_details['item_name'],
                    "item_link": item_details['item_link'],
                    "item_final_price": item_details['item_final_price'],
                    "brand": item_details['brand'],
                    "item_image_url": item_details['item_image_url']
                }]
                print(f"[DEBUG] Response: {response}")
                return jsonify(response), 200
            else:
                print(f"[DEBUG] No details found for recommended_item_id: {recommended_item_id}")
                return jsonify({"error": f"추천된 item_id {recommended_item_id}에 대한 세부 정보가 없습니다."}), 404

    except pymysql.MySQLError as e:
        print(f"[DEBUG] MySQL Error: {str(e)}")
        return jsonify({"error": f"MySQL Error: {str(e)}"}), 500
    except Exception as e:
        print(f"[DEBUG] General Error: {str(e)}")
        return jsonify({"error": str(e)}), 500
    finally:
        if connection:
            print("[DEBUG] Closing database connection")
            connection.close()


if __name__ == '__main__':
    app.run(port=5001, debug=True)
