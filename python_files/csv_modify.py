import pandas as pd

# Wide Format CSV 읽기
input_file = "recommedation_essential_final.csv"
wide_df = pd.read_csv(input_file)

# Wide → Long 변환
long_df = wide_df.melt(
    id_vars=["item_id", "item_type"],  # 유지할 열
    var_name="category",              # Wide의 열 이름을 'category'로
    value_name="recommended_item_id"  # Wide의 값들을 'recommended_item_id'로
)

# 필요한 열만 선택
long_df = long_df[["item_id", "category", "recommended_item_id"]]

# Long Format 저장
output_file = "long_format_similar_recommendation.csv"
long_df.to_csv(output_file, index=False)

print(f"변환 완료! 결과가 {output_file}에 저장되었습니다.")
