import sys
import io
import os
from fastapi import FastAPI, Response, Request, status
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util
import numpy as np
import re
import pickle
import uvicorn
from typing import List, Union
from openai import OpenAI
import pandas as pd

app = FastAPI()

# 서버 부팅 시 미리 캐싱한 CSV 파일 로드
# with open("cache/law_cache.pkl", "rb") as f:
#     df, x = pickle.load(f)
path = './data/law_data_prep_v2.csv'
df = pd.read_csv(path, encoding='utf-8')

# 모델 로드
sbert = SentenceTransformer('distiluse-base-multilingual-cased-v2')

# CHAT GPT
API_SECRET_KEY = os.getenv("API_SECRET_KEY")
KEYWORD_MODEL_KEY = os.getenv("KEYWORD_MODEL_KEY")
PREDICT_MODEL_KEY = os.getenv("PREDICT_MODEL_KEY")

### 구조 ###
# 요청
class ScriptRequest(BaseModel):
    content: str

# 가장 유사한 사례
class SimilarMainCase(BaseModel):
    case: str
    caseNo: str
    score: int
    judgementResult: str
    judgementReason: str

# 그 외 유사 사례들
class SimilarOtherCase(BaseModel):
    case: str
    caseNo: str
    score: int
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
@app.post(
    "/similarity",
    response_model=SimilarityResult,
    responses={204: {"content": "유사 사례가 없습니다."}}
)
def similar_script(request: ScriptRequest):
    total_case = df['story']
    my_case = request.content
    cos_scores = util.pytorch_cos_sim(sbert.encode(total_case), sbert.encode(my_case)).squeeze()
    cos_scores = np.array(cos_scores)
    if cos_scores.max() > 0.5:
        top4_idx = np.argsort(cos_scores)[::-1][:4]

        i0 = top4_idx[0]

        # 1. 가장 유사한 케이스
        main_case = SimilarMainCase(
            case = df['story'][i0],
            caseNo = df['number'][i0],
            score = int(np.round(cos_scores[i0], 2) * 100),
            judgementResult = df['outcome'][i0],
            judgementReason = df['result'][i0]
        )

        # 2. 나머지 케이스
        other_cases = [
            SimilarOtherCase(
                case = df['summary'][i],
                caseNo = df['number'][i],
                score = int(np.round(cos_scores[i], 2) * 100),
                judgementResult = df['outcome'][i],
            ) for i in top4_idx[1:]
        ]

        # 3. 키워드 추출
        def generate_keyword(case_1, case_2):
            client = OpenAI(api_key=API_SECRET_KEY)
            keyword_completion = client.chat.completions.create(
                model=KEYWORD_MODEL_KEY,
                messages=[
                    {'role': 'system', 'content': '당신은 고객이 입력한 사례의 키워드를 분석하는 직원 역할을 수행합니다.'},
                    {'role': 'user', 'content': f'{case_1}와 {case_2} 앞선 두 사례가 유사한 점을 갖는 이유를 직장 내 괴롭힘을 제외한 다른 키워드 3개만 "키워드1, 키워드2, 키워드3" 형태로 출력'}
                ]
            )
            result = keyword_completion.choices[0].message.content
            return result

        keywords = generate_keyword(my_case, main_case)

        # 예측 모델
        client = OpenAI(api_key=API_SECRET_KEY)
        pred_completion = client.chat.completions.create(
            model=PREDICT_MODEL_KEY,
            messages=[
                {'role': 'system', 'content': '당신은 직장 내 괴롭힘과 관련된 재판을 담당하는 대한민국 법원의 판사 역할을 수행하며, 직장 내 괴롭힘으로 인정되는 것을 좀 더 엄격하게 판단'},
                {'role': 'user', 'content': f'{my_case} 위 사례가 직장 내 괴롭힘에 해당하는 경우에는 "이 사건에서 직장 내 괴롭힘이 인정되는 이유는 다음과 같이 요약할 수 있습니다. ~ "의 형태로, 직장 내 괴롭힘에 해당하지 않는 경우에는 "이 사건에서 직장 내 괴롭힘이 인정되지 않는 이유는 다음과 같이 요약할 수 있습니다. ~ " 의 형태로, 일부는 직장 내 괴롭힘에 해당하고 일부는 직장 내 괴롭힘에 해당하지 않는 경우에는 "이 사건에서 직장 내 괴롭힘이 인정되는 부분의 이유는 다음과 같이 요약할 수 있습니다. ~ 반면, 이 사건에서 직장 내 괴롭힘이 인정되지 않는 부분의 이유는 다음과 같이 요약할 수 있습니다. ~"의 형태로 영어표현을 사용하지 않고 출력'}
            ]
        )

        result = pred_completion.choices[0].message.content
        result = re.sub('‧', '·', result)
        result = re.sub(r'\s+', ' ', result)
        result = re.sub('[\n+]', ' ', result)
        result = re.sub(r'\([^)]*\)', '', result)
        result = re.sub('[①②③④⑤]', '', result)

        return SimilarityResult(
            myCase=request.content,
            mainCase=main_case,
            keyword=keywords,
            aiPredict=result,
            otherCases=other_cases
        )

    else:
        return Response(
            status_code=status.HTTP_204_NO_CONTENT
        )

# 예측 모델
@app.post("/predict", response_model=PredictResult)
def predict_script(request: ScriptRequest):
    my_case = request.content
    client = OpenAI(api_key=API_SECRET_KEY)
    pred_completion = client.chat.completions.create(
        model=PREDICT_MODEL_KEY,
        messages=[
            {'role': 'system', 'content': '당신은 직장 내 괴롭힘과 관련된 재판을 담당하는 대한민국 법원의 판사 역할을 수행하며, 직장 내 괴롭힘으로 인정되는 것을 좀 더 엄격하게 판단'},
            {'role': 'user', 'content': f'{my_case} 위 사례가 직장 내 괴롭힘에 해당하는 경우에는 "이 사건에서 직장 내 괴롭힘이 인정되는 이유는 다음과 같이 요약할 수 있습니다. ~ "의 형태로, 직장 내 괴롭힘에 해당하지 않는 경우에는 "이 사건에서 직장 내 괴롭힘이 인정되지 않는 이유는 다음과 같이 요약할 수 있습니다. ~ " 의 형태로, 일부는 직장 내 괴롭힘에 해당하고 일부는 직장 내 괴롭힘에 해당하지 않는 경우에는 "이 사건에서 직장 내 괴롭힘이 인정되는 부분의 이유는 다음과 같이 요약할 수 있습니다. ~ 반면, 이 사건에서 직장 내 괴롭힘이 인정되지 않는 부분의 이유는 다음과 같이 요약할 수 있습니다. ~"의 형태로 영어표현을 사용하지 않고 출력'}
        ]
    )

    result = pred_completion.choices[0].message.content
    result = re.sub('‧', '·', result)
    result = re.sub(r'\s+', ' ', result)
    result = re.sub('[\n+]', ' ', result)
    result = re.sub(r'\([^)]*\)', '', result)
    result = re.sub('[①②③④⑤]', '', result)

    return PredictResult(
        content = result
    )