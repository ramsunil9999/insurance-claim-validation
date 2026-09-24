from fastapi import APIRouter
from fastapi import Request

from services.retrieval_service import (search_policies)

from services.context_builder import (build_policy_context)

from services.recommendation_service import (recommend)

from pydantic import BaseModel

class RetrievalRequest(BaseModel):
    query: str
    top_k: int = 3

router = APIRouter()


@router.post("/retrieve")
def retrieve(request: RetrievalRequest):

    results = search_policies(
        request.query,
        request.top_k
    )

    context = build_policy_context(results)

    return {
        "query": request.query,
        "results": results,
        "policy_context": context
    }

@router.post("/recommend")
def recommend_policy(request: dict):

    return recommend(
        request["query"],
        request["case_data"]
    )
