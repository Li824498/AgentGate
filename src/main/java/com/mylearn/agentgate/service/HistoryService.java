package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.mapper.HistoryMapper;
import com.mylearn.agentgate.utils.UserIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    public List<HistoryMessage> getHistoriesByChatId(String chatId, LocalDateTime dateTime) {
        String userId = UserIdUtils.getUserId();
        List<HistoryMessage> historyMessages = historyMapper.selectByUserIdAndChatId(userId, chatId, dateTime);
        return historyMessages;
    }
}
