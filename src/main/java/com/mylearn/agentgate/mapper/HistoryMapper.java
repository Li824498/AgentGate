package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface HistoryMapper {

    List<HistoryMessage> selectByUserIdAndChatId(String userId, String chatId, LocalDateTime dateTime);

    void insertHistory(HistoryMessage historyMessage);

    int count();

    void deleteByid(int count);

    void deleteByids(List<Integer> count);
}
