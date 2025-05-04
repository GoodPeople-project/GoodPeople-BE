from fastapi import FastAPI, Request
from pydantic import BaseModel
from sentence_transformers import SentenceTransformer, util
import numpy as np
import pickle
import uvicorn
from typing import List

app = FastAPI()

# 서버 부팅 시 미리 캐싱한 CSV 파일, 모델 로드
with open("cache/law_cache.pkl", "rb") as f:
    df, x = pickle.load(f)
model = SentenceTransformer('distiluse-base-multilingual-cased-v2')

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


# 유사도 사례 반환
@app.post("/similarity", response_model=SimilarityResult)
def analyze_script(request: ScriptRequest):
    y = model.encode(request.content)
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


#기존 코드#############################################################
# # 요청 바디 구조
# class ScriptRequest(BaseModel):
#     script: str
#
# # 응답 구조
# class SimilarityCase(BaseModel):
#     story: str
#     result: str
#
# class SimilarityResponse(BaseModel):
#     top3: List[SimilarityCase]
#
# # 유사도 사례 반환
# @app.post("/similarity", response_model=SimilarityResponse)
# def analyze_script(request: ScriptRequest):
#     y = model.encode(request.script)
#     cos_scores = util.pytorch_cos_sim(x, y).squeeze().cpu().numpy()
#     top3_idx = np.argsort(cos_scores)[::-1][:3]
#
#     result = []
#     for i in top3_idx:
#         result.append(SimilarityCase(
#             story=df['story'][i],
#             result=df['result'][i]
#         ))
#
#     return SimilarityResponse(top3=result)
#######################################################################