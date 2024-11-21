import pandas as pd

# 파일 경로 설정
input_file = 'user_history.csv'  # 원본 CSV 파일 경로
output_file = 'users_history2.csv'  # 수정된 CSV 파일 저장 경로

# CSV 파일 읽기
df = pd.read_csv(input_file)

# 컬럼 확인 및 1씩 증가
if 'item_id' in df.columns:
    df['item_id'] = df['item_id'] + 1
if 'user_id' in df.columns:
    df['user_id'] = df['user_id'] + 1

# 'gender' 컬럼 변환
if 'sex' in df.columns:
    df['sex'] = df['sex'].replace({'Male': '남성', 'Female': '여성'})

# 수정된 데이터 저장 (utf-8 인코딩 설정)
df.to_csv(output_file, index=False, encoding='utf-8')

print(f"파일이 성공적으로 저장되었습니다: {output_file}")
