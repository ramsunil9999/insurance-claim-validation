from langchain_chroma import Chroma
from sentence_transformers import SentenceTransformer


class LocalEmbeddingFunction:

    def __init__(self):
        self.model = SentenceTransformer("all-MiniLM-L6-v2")

    def embed_documents(self, texts):
        return self.model.encode(texts).tolist()

    def embed_query(self, text):
        return self.model.encode(text).tolist()


embedding_function = LocalEmbeddingFunction()

vector_store = Chroma(
    collection_name="insurance_policies",
    embedding_function=embedding_function,
    persist_directory="./chroma_db"
)