import os
import sys
import io
import pandas as pd
import pickle
from sentence_transformers import SentenceTransformer

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding="utf-8")
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding="utf-8")

# CSV 캐시 파일 경로
BASE_DIR = os.path.abspath(__file__)
CSV_PATH = os.path.join(BASE_DIR, "../data/law_data.csv")
CACHE_PATH = os.path.join(BASE_DIR, "../cache/law_cache.pkl")

# 캐싱 시작
print("캐싱 시작")
sys.stdout.flush()

# 캐시가 이미 존재하면 패스
if os.path.exists(CACHE_PATH):
    print("law_cache.pkl 파일이 이미 존재합니다.")
    sys.stdout.flush()
    exit(0)

sys.stdout.flush()
df = pd.read_csv(CSV_PATH, encoding='utf-8')

# 모델 로딩
model = SentenceTransformer('distiluse-base-multilingual-cased-v2')

# 기준 story 인코딩 (Tensor로 저장)
story_embeddings = model.encode(df['story'].tolist(), convert_to_tensor=True)

# pickle로 저장
with open(CACHE_PATH, 'wb') as f:
    pickle.dump((df, story_embeddings), f)

print("캐싱 완료")
sys.stdout.flush()