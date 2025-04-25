package com.mylearn.agentgate.core.domain.worldBook;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.WorldBook;
import com.mylearn.agentgate.mapper.WorldBookMapper;
import com.mylearn.agentgate.utils.EmbedUtils;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorldBookManager {
    @Autowired
    private WorldBookMapper worldBookMapper;

    public List<String> process(LRequest lRequest) {
        List<WorldBook> worldBooks = worldBookMapper.selectByIds(lRequest.getWorldBookIds());
        Set<InMemoryEmbeddingStore<TextSegment>> inMemoryEmbeddingStores = worldBooks.stream().map(worldBook -> InMemoryEmbeddingStore.fromFile(worldBook.getUrl())).collect(Collectors.toSet());
        InMemoryEmbeddingStore<TextSegment> embeddingStore = InMemoryEmbeddingStore.merge(inMemoryEmbeddingStores);

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(EmbeddingSearchRequest.builder()
                .queryEmbedding(EmbedUtils.embText2Embedding(lRequest.getContext()))
                .maxResults(10)
                .build());

        return result.matches().stream().map(textSegmentEmbeddingMatch -> textSegmentEmbeddingMatch.embedded().text()).collect(Collectors.toList());
    }

    public void insert(WorldBook worldBook) {
        worldBookMapper.insert(worldBook);
    }

    public void updateStatusByUrl(String url) {
        worldBookMapper.updateStatusByUrl(url);
    }
}
