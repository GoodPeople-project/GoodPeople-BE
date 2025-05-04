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

# 요청 바디 구조
class ScriptRequest(BaseModel):
    script: str

# 응답 구조
class SimilarityCase(BaseModel):
    story: str
    result: str

class SimilarityResponse(BaseModel):
    top3: List[SimilarityCase]

# 유사도 사례 반환
@app.post("/similarity", response_model=SimilarityResponse)
def analyze_script(request: ScriptRequest):
    y = model.encode(request.script)
    cos_scores = util.pytorch_cos_sim(x, y).squeeze().cpu().numpy()
    top3_idx = np.argsort(cos_scores)[::-1][:3]

    result = []
    for i in top3_idx:
        result.append(SimilarityCase(
            story=df['story'][i],
            result=df['result'][i]
        ))

    return SimilarityResponse(top3=result)