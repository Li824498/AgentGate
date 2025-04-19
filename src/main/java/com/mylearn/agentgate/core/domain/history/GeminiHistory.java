package com.mylearn.agentgate.core.domain.history;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.mapper.HistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiHistory implements History{
    @Autowired
    private HistoryMapper historyMapper;

    @Override
    public List<HistoryMessage> process(LRequest lRequest) {
        String userId = lRequest.getUserId();
        String chatId = lRequest.getChatId();

        List<HistoryMessage> historyMessages = historyMapper.selectByUserIdAndChatId(userId, chatId);

        HistoryMessage historyMessage = new HistoryMessage();
        historyMessage.setUserId(lRequest.getUserId());
        historyMessage.setChatId(lRequest.getChatId());
        historyMessage.setMsgIndex(lRequest.getMsgIndex());
        historyMessage.setRole("role");
        historyMessage.setText(lRequest.getText());

        historyMapper.insertHistory(historyMessage);

        return historyMessages;
    }
}
