package com.mylearn.agentgate.core.domain.history;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.mapper.HistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiHistory implements History{
    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public List<HistoryMessage> processBefore(LRequest lRequest) {
        String userId = lRequest.getUserId();
        String chatId = lRequest.getChatId();

        List<HistoryMessage> historyMessages = historyMapper.selectByUserIdAndChatId(userId, chatId);

        HistoryMessage historyMessage = new HistoryMessage();
        historyMessage.setUserId(lRequest.getUserId());
        historyMessage.setChatId(lRequest.getChatId());
        historyMessage.setMsgIndex(lRequest.getMsgIndex());
        historyMessage.setRole("user");
        historyMessage.setContext(lRequest.getContext());

        historyMapper.insertHistory(historyMessage);

        return historyMessages;
    }

    @Override
    public void processAfter(LResponse lResponse) {
        HistoryMessage historyMessage = new HistoryMessage();
        historyMessage.setUserId(lResponse.getUserId());
        historyMessage.setChatId(lResponse.getChatId());
        historyMessage.setMsgIndex(lResponse.getMsgIndex());
        historyMessage.setRole("model");
        historyMessage.setContext(lResponse.getContext());

        historyMapper.insertHistory(historyMessage);
    }
}
