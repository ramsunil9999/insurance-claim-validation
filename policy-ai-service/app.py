from fastapi import FastAPI
from services.ingestion_service import ingest_documents
from api.policy_routes import router

app = FastAPI(title="Policy Intelligence Service")
app.include_router(router)

@app.get("/health")
def health_check():
    return {
        "status": "UP"
    }

@app.post("/ingest")
def ingest():
    chunks = ingest_documents()
    return {
        "status": "SUCCESS",
        "chunks_created": chunks
    }