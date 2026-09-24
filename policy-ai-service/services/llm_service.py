import os
from dotenv import load_dotenv
from groq import Groq

load_dotenv()

client = Groq(
    api_key=os.getenv("GROQ_API_KEY")
)


def ask_groq(prompt):

    response = (
        client.chat.completions.create(model= "openai/gpt-oss-120b",

            messages=[
                {
                    "role": "user",
                    "content": prompt
                }
            ],

            temperature=0.2
        )
    )

    return (
        response
        .choices[0]
        .message
        .content
    )