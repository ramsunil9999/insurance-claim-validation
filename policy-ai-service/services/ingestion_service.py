from pathlib import Path
from docx import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter
from storage.vector_store import vector_store

POLICY_DIRECTORY = "./policies"

def read_docx(file_path):

    document = Document(file_path)

    return "\n".join(
        paragraph.text
        for paragraph in document.paragraphs
        if paragraph.text.strip()
    )


def ingest_documents():

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=800,
        chunk_overlap=150
    )

    total_chunks = 0

    for policy_file in Path(POLICY_DIRECTORY).glob("*.docx"):

        text = read_docx(policy_file)

        chunks = splitter.split_text(text)

        ids = [
                f"{policy_file.stem}_{i}"
                for i in range(len(chunks))
            ]

        metadata = [
                {
                    "policy_name": policy_file.stem,
                    "source_file": policy_file.name,
                    "chunk_id": i
                }
                for i in range(len(chunks))
            ]

        vector_store.add_texts(
            texts=chunks,
            metadatas=metadata,
            ids=ids
        )

        total_chunks += len(chunks)

    return total_chunks