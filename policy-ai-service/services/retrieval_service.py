from storage.vector_store import vector_store

def search_policies(query: str, top_k: int = 3):

    results = vector_store.similarity_search(query, k=top_k)

    return [
        {
            "policy": document.metadata.get("policy_name"),
            "source_file": document.metadata.get("source_file"),
            "chunk_id": document.metadata.get("chunk_id"),
            "content": document.page_content
        }
        for document in results
    ]