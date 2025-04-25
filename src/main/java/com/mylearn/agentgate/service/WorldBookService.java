package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.domain.worldBook.WorldBookManager;
import com.mylearn.agentgate.core.entity.WorldBook;
import com.mylearn.agentgate.dto.WorldBookDTO;
import com.mylearn.agentgate.mapper.WorldBookMapper;
import com.mylearn.agentgate.utils.EmbedUtils;
import com.mylearn.agentgate.utils.UserIdUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WorldBookService {

    private final static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        16,
        32,
        30L,
        TimeUnit.MINUTES,
        new LinkedBlockingDeque<>(100),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Autowired
    private WorldBookManager worldBookManager;

    @Autowired
    private WorldBookMapper worldBookMapper;


    private final static String DBDAOPath = "src/main/resources/DBDAO/";

    public void uploadFile(MultipartFile file) throws IOException {
        // 同步增加元数据
        String userId = UserIdUtils.getUserId();

        WorldBook worldBook = new WorldBook();
        worldBook.setUserId(userId);
        String url = DBDAOPath + String.valueOf("user_" + userId + "/") + UUID.randomUUID();
        worldBook.setUrl(url);
        worldBookManager.insert(worldBook);
        log.info(url + "正在解析");

        // todo 异步化处理

        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                //文件保存
                String originalFilename = file.getOriginalFilename();
                Path path = Path.of(DBDAOPath + String.valueOf("user_" + userId + "/") + originalFilename);
                try {
                    Files.createDirectories(path.getParent());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Document document = FileSystemDocumentLoader.loadDocument(DBDAOPath + String.valueOf("user_" + userId + "/") + originalFilename);

                //异步建库
                DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);


                InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
                OpenAiEmbeddingModel embeddingModel = EmbedUtils.getEmbeddingModel();
                EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                        .documentSplitter(splitter)
                        .embeddingModel(embeddingModel)
                        .embeddingStore(embeddingStore)
                        .build();

                ingestor.ingest(document);

                embeddingStore.serializeToFile(url);

                worldBookManager.updateStatusByUrl(url);
                log.info(url + "解析完毕");
            }
        });

    }

    /**
     * 查询所有可用世界书
     * @return
     */
    public List<WorldBookDTO> selectAllWorldBooks() {
        return worldBookMapper.selectAllWorldBooks();
    }
}
