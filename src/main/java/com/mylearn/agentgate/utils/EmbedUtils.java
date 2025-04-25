package com.mylearn.agentgate.utils;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.jspecify.annotations.NonNull;

public class EmbedUtils {
    private static OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
            .apiKey("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")
            .modelName("text-embedding-3-small")
            .build();

    public static @NonNull Embedding embText2Embedding(String text) {
        Response<Embedding> embed = embeddingModel.embed(text);
        return embed.content();
    }

    public static OpenAiEmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}
