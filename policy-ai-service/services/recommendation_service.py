from services.retrieval_service import (search_policies)

from services.context_builder import (build_policy_context)

from services.llm_service import (ask_groq)

import json

def recommend(query, case_data):

    results = search_policies(query)

    policy_context = (build_policy_context(results))

    prompt = f"""
            You are a senior health insurance reviewer.

            CASE:
            {case_data}

            POLICY EVIDENCE:
            {policy_context}

            Instructions:
            - Use the policy evidence.
            - Do not invent policy rules.
            - Prefer MANUAL_REVIEW if evidence is insufficient.
            - Return only valid JSON.

            {{
                "recommendation":
                "APPROVED|MANUAL_REVIEW|REJECTED",

                "confidence":
                0.0,

                "reason":
                ""
            }}
            """
    response = ask_groq(prompt)

    parsed_response = json.loads(response)

    return {
        "recommendation": parsed_response,

        "policy_context": policy_context,

        "sources": results
    }