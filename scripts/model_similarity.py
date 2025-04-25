# -*- coding: utf-8 -*-
import datetime
import sys
import io
import numpy as np
import pandas as pd
from sentence_transformers import SentenceTransformer, util
import time
import pickle

initial_time = time.time()

def log_time(tag, start):
    end = time.time()
    print(f'{tag} Time: {end - start:.3f}초, 시각: {datetime.datetime.now()}')
    sys.stdout.flush()
    return end

step_time = log_time("초기", initial_time) # Log

# TODO: 바이너리 데이터로 읽으면 읽기 속도 더 빨라질 수도 있음.
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding="utf-8")
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding="utf-8")

step_time = log_time("입출력 설정 후", initial_time) # Log

# # 판례 CSV 데이터 경로
# path = 'C:\dev\goodpeople\scripts\data\law_data.csv'
# df = pd.read_csv(path, encoding='utf-8')
# 캐싱된 CSV, model을 가져온다.
with open('C:/dev/goodpeople/scripts/cache/law_cache.pkl', 'rb') as f:
    df, x = pickle.load(f)

step_time = log_time("CSV 읽기", step_time) # Log

# sentence-transformer 내장 모델
model = SentenceTransformer('distiluse-base-multilingual-cased-v2')
step_time = log_time("모델 로딩", step_time) # Log

# 사용자가 입력한 스크립트를 text 변수에 저장, 내용이 없으면 exit()
if len(sys.argv) < 2:
    print("입력된 스크립트가 없습니다.")
    sys.stdout.flush()
    sys.exit(1)

text = sys.argv[1]
step_time = log_time("사용자 입력 저장", step_time) # Log

# 입력한 텍스트를 인코딩한다.-> 캐싱으로 패스
# x = model.encode(df['story'])
# step_time = log_time("기준 사례 인코딩", step_time) # Log

y = model.encode(text)
step_time = log_time("사용자 입력 인코딩", step_time) # Log

# 유사도 계산
cos_scores = util.pytorch_cos_sim(x, y).squeeze()
cos_scores = np.array(cos_scores)
step_time = log_time("유사도 계산", step_time) # Log

# 유사한 사례를 3개 반환한다.
target_index = np.argsort(cos_scores)[::-1][:3]
target_prop = cos_scores[target_index]

print('회원님이 작성한 사례와 가장 유사한 사례 3가지는 다음과 같습니다.')
print('')
for i in range(3):
    print(f'===== 유사한 괴롭힘 사례 Top {i+1} =====')
    print('')
    print(df['story'][target_index[i]])
    print('')
    print(f'===== 유사한 괴롭힘 사례 Top {i+1}의 괴롭힘 인정 여부 =====')
    print('')
    print(df['result'][target_index[i]])
    print('')

log_time("전체 소요 시간: ", initial_time) # Log
print("스크립트 종료 후 시각", datetime.datetime.now()) # Log