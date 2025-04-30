package com.mylearn.agentgate.utils;

import com.mylearn.agentgate.service.ModelApiService;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.annotation.PostConstruct;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmbedUtils {
    @Autowired
    private ModelApiService modelApiService;


    private OpenAiEmbeddingModel embeddingModel;

    @PostConstruct
    public void init() {
        embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(modelApiService.getEmbApi())
                .modelName(modelApiService.EMB_MODEL_NAME)
                .build();
    }

    public @NonNull Embedding embText2Embedding(String text) {
        Response<Embedding> embed = embeddingModel.embed(text);
        return embed.content();
    }

    public OpenAiEmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }
}
