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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WorldBookManager {
    private static final Logger log = LoggerFactory.getLogger(WorldBookManager.class);
    @Autowired
    private WorldBookMapper worldBookMapper;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EmbedUtils embedUtils;

    public List<String> process(LRequest lRequest) {
/*        List<WorldBook> worldBooks = worldBookMapper.selectByIds(lRequest.getWorldBookIds());
        Set<InMemoryEmbeddingStore<TextSegment>> inMemoryEmbeddingStores = worldBooks.stream().map(worldBook -> InMemoryEmbeddingStore.fromFile(worldBook.getUrl())).collect(Collectors.toSet());
        InMemoryEmbeddingStore<TextSegment> embeddingStore = InMemoryEmbeddingStore.merge(inMemoryEmbeddingStores);*/

        // 不使用世界书就不走了
        if(lRequest.getWorldBookIds().isEmpty()) return Collections.emptyList();

        InMemoryEmbeddingStore<TextSegment> embeddingStore = getFromCaffeineCache(lRequest.getWorldBookIds());

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(EmbeddingSearchRequest.builder()
                .queryEmbedding(embedUtils.embText2Embedding(lRequest.getContext()))
                .maxResults(10)
                .build());

        return result.matches().stream().map(textSegmentEmbeddingMatch -> textSegmentEmbeddingMatch.embedded().text()).collect(Collectors.toList());
    }

    public InMemoryEmbeddingStore<TextSegment> getFromCaffeineCache(List<Long> worldBookIds) {
        String key = worldBookIds.stream().sorted().collect(Collectors.toList()).toString();
        InMemoryEmbeddingStore<TextSegment> inMemoryEmbeddingStore = null;

        Cache cache = cacheManager.getCache("inMemoryEmbeddingStores");
        inMemoryEmbeddingStore = cache.get(key, new Callable<InMemoryEmbeddingStore<TextSegment>>() {
            @Override
            public InMemoryEmbeddingStore<TextSegment> call() throws Exception {
                log.info("没有走缓存！");

                List<WorldBook> worldBooks = worldBookMapper.selectByIds(worldBookIds);
                Set<InMemoryEmbeddingStore<TextSegment>> inMemoryEmbeddingStores = worldBooks.stream().map(worldBook -> InMemoryEmbeddingStore.fromFile(worldBook.getUrl())).collect(Collectors.toSet());
                return InMemoryEmbeddingStore.merge(inMemoryEmbeddingStores);
            }
        });

        return inMemoryEmbeddingStore;
    }

    public void insert(WorldBook worldBook) {
        worldBookMapper.insert(worldBook);
    }

    public void updateStatusByUrl(String url) {
        worldBookMapper.updateStatusByUrl(url);
    }
}
