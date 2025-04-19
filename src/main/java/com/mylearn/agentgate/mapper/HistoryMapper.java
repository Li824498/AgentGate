package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryMapper {

    List<HistoryMessage> selectByUserIdAndChatId(String userId, String chatId);

    void insertHistory(HistoryMessage historyMessage);
}
