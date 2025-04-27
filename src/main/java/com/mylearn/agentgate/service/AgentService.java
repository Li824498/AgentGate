package com.mylearn.agentgate.service;

import com.mylearn.agentgate.chain.Chain;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.core.processor.AbstractChatProcessor;
import com.mylearn.agentgate.core.processor.ProcessorSelector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@Service
public class AgentService {

    @Autowired
    private Chain chain;



    public LResponse syncNonStreamChatService(LRequest lRequest) {
//        return chain.RenderNonChain(lRequest);
        List<String> renders = lRequest.getRenders();
        if (renders == null || (renders.size() == 1 && renders.get(0).equals("不使用渲染"))) {
            return chain.RenderNonChain(lRequest);
        } else {
            return chain.RenderChain(lRequest);
        }

    }

    public Flux<LResponse> syncStreamChatService(LRequest lRequest) throws IOException {
        return chain.RenderNonStreamChain(lRequest);
    }
}
