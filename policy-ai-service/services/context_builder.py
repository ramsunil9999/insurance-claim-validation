from collections import defaultdict


def build_policy_context(retrieval_results):

    grouped = defaultdict(list)

    for item in retrieval_results:
        grouped[item["policy"]].append(item)

    context_sections = []

    for policy, entries in grouped.items():

        section = f"\nPOLICY: {policy}\n"

        for entry in entries:

            section += (
                f"\nSOURCE FILE: "
                f"{entry['source_file']}\n"
                f"CHUNK: "
                f"{entry['chunk_id']}\n"
                f"EVIDENCE:\n"
                f"{entry['content']}\n"
            )

        context_sections.append(section)

    return "\n\n".join(context_sections)