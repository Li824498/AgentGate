package com.mylearn.agentgate.chain;

import com.mylearn.agentgate.core.Core;
import com.mylearn.agentgate.core.entity.HistoryRendered;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.utils.UserIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class Chain {
    @Autowired
    private Core core;

    public LResponse RenderNonChain(LRequest lRequest) {
        LResponse lResponse = core.syncNonStreamChatProcess(lRequest);

        lResponse.setMsgIndex(lRequest.getMsgIndex() + 1);
        lResponse.setUserId(UserIdUtils.getUserId());
        lResponse.setChatId(lResponse.getChatId());
        lResponse.setOutContext(Collections.emptyList());

        return lResponse;
    }

    public Flux<LResponse> RenderNonStreamChain(LRequest lRequest) throws IOException {
        return core.syncStreamChatProcess(lRequest);
    }

    public LResponse RenderChain(LRequest lRequest) {
        LResponse lResponse = core.syncNonStreamChatProcess(lRequest);

        List<HistoryRendered> historyRenderedList = core.RenderChatProcess(lRequest, lResponse);

        lResponse.setOutContext(historyRenderedList);

        return lResponse;
    }
}
