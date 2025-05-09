import sys
import io
import os
from fastapi import FastAPI, Request
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util
import numpy as np
import re
import pickle
import uvicorn
from typing import List
from openai import OpenAI
import pandas as pd

app = FastAPI()

# 서버 부팅 시 미리 캐싱한 CSV 파일 로드
with open("cache/law_cache.pkl", "rb") as f:
    df, x = pickle.load(f)

# 모델 로드
encode_model = SentenceTransformer('distiluse-base-multilingual-cased-v2')

# CHAT GPT
CHATGPT_SECRET = os.getenv("CHATGPT_SECRET")
CHATGPT_MODEL = os.getenv("CHATGPT_MODEL")

### 구조 ###
# 요청
class ScriptRequest(BaseModel):
    content: str

# 가장 유사한 사례
class SimilarMainCase(BaseModel):
    case: str
    caseNo: str
    score: float
    judgementResult: str
    judgementReason: str

# 그 외 유사 사례들
class SimilarOtherCase(BaseModel):
    case: str
    caseNo: str
    score: float
    judgementResult: str

# 서비스에 반환할 최종 응답 형태
class SimilarityResult(BaseModel):
    myCase: str
    mainCase: SimilarMainCase
    keyword: str
    aiPredict: str
    otherCases: List[SimilarOtherCase]

# 예측 모델 응답
class PredictResult(BaseModel):
    content: str

# 유사도 사례 반환
@app.post("/similarity", response_model=SimilarityResult)
def analyze_script(request: ScriptRequest):
    y = encode_model.encode(request.content)
    cos_scores = util.pytorch_cos_sim(x, y).squeeze().cpu().numpy()
    top3_idx = np.argsort(cos_scores)[::-1][:3]

    # TODO: 컬럼 맞추기
    i0 = top3_idx[0]

    # 1. 가장 유사한 케이스
    main_case = SimilarMainCase(
        case = df['story'][i0],
        caseNo = df['number'][i0],
        # TODO: 임시 처리
        score = 100,
        judgementResult = 'String',
        judgementReason = 'String'
    )

    # 2. 나머지 케이스. 몇 개를 뽑느냐에 따라 조절 가능할 듯
    other_cases = [
        SimilarOtherCase(
            case = df['story'][i],
            caseNo = df['number'][i],
            # TODO: 임시 처리
            score = 90,
            judgementResult = 'String'
        ) for i in top3_idx[1:]
    ]

    return SimilarityResult(
        myCase=request.content,
        mainCase=main_case,
        keyword='String',
        aiPredict='String',
        otherCases=other_cases
    )

# 예측 모델
@app.post("/predict", response_model=PredictResult)
def predict_script(request: ScriptRequest):

    user_story = request.content
    x = encode_model.encode(df['story'])
    y = encode_model.encode(user_story)

    cos_scores = util.pytorch_cos_sim(x, y).squeeze()
    cos_scores = np.array(cos_scores)
    if cos_scores.max() > 0.5:
        target_index = np.argsort(cos_scores)[::-1][:4]
        target_prop = np.sort(cos_scores)[::-1][:4]
    else:
        print("현재 고객님이 입력하신 사례와 유사한 사례가 없습니다.\n조금 더 구체적으로 작성해주시거나 추후 유사한 사례가 생길 경우 알려드리도록 하겠습니다.")

    test_story = user_story
    test_similar = df['story'][target_index[0]]

    client = OpenAI(
        api_key=CHATGPT_SECRET
    )
    similar_completion = client.chat.completions.create(
        model=CHATGPT_MODEL,
        messages=[
            {'role': 'system', 'content': '당신은 고객이 입력한 사례의 키워드를 분석하는 직원 역할을 수행합니다.'},
            {'role': 'user', 'content': f'{test_story}와 {test_similar} 앞선 두 사례가 유사한 점을 갖는 이유를 직장 내 괴롭힘을 제외한 다른 키워드 3개만 "키워드1, 키워드2, 키워드3" 형태로 출력'}
        ]
    )
    result = similar_completion.choices[0].message.content
    result = re.sub('[\"\'‘’“”*]', '', result)
    result = re.sub('‧', '·', result)

    buffer = io.StringIO()
    sys_stdout = sys.stdout
    sys.stdout = buffer

    try:
        print('회원님이 작성한 사례와 가장 유사한 사례는 다음과 같습니다.\n')
        print('===== 가장 유사한 사례 키워드 요약 =====\n')
        print(result + '\n')
        print('==== 작성한 사례 ====\n')
        print(user_story + '\n')
        print('===== 가장 유사한 사례 =====\n')
        print('사건번호:', df['number'][target_index[0]] + '\n')
        print(df['story'][target_index[0]] + '\n')
        print('===== 직장 내 괴롭힘 판단 =====\n')
        print('승소 여부:'+ df['outcome'][target_index[0]] + '\n')
        print(df['result'][target_index[0]] + '\n')
    finally:
        sys.stdout = sys_stdout

    output = buffer.getvalue()

    return PredictResult(
        content = output
    )
