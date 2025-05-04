package com.mylearn.agentgate.core.domain.history;

import com.mylearn.agentgate.core.entity.*;
import com.mylearn.agentgate.mapper.ChatMetaMapper;
import com.mylearn.agentgate.mapper.HistoryMapper;
import com.mylearn.agentgate.mapper.RoleCardMapper;
import com.mylearn.agentgate.utils.HistoryIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GeminiHistoryManager implements HistoryManager {
    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private ChatMetaMapper chatMetaMapper;

    @Autowired
    private RoleCardMapper roleCardMapper;


    @Override
    public List<HistoryMessage> processBefore(LRequest lRequest) {
        String userId = lRequest.getUserId();
        String chatId = lRequest.getChatId();

        List<HistoryMessage> historyMessages = historyMapper.selectByUserIdAndChatId(userId, chatId);

        HistoryMessage historyMessage = new HistoryMessage();
        historyMessage.setUserId(lRequest.getUserId());
        historyMessage.setChatId(lRequest.getChatId());
//        historyMessage.setMsgIndex(lRequest.getMsgIndex());
        // todo 楼层数太多咋办？修补，先不信任前端
        historyMessage.setMsgIndex(historyMessages.size() + 1);
        historyMessage.setRole("user");
        historyMessage.setContext(lRequest.getContext());
        historyMessage.setCreateTime(LocalDateTime.now());

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
        historyMessage.setContext(lResponse.getInContext());
        historyMessage.setCreateTime(LocalDateTime.now());

        historyMapper.insertHistory(historyMessage);
        HistoryIdUtils.set(historyMessage.getId());
    }

    @Override
    public void chatMetaProcessBefore(LRequest lRequest) {
        ChatMeta chatMeta = new ChatMeta();

        chatMeta.setChatId(lRequest.getChatId());
        chatMeta.setUserId(lRequest.getUserId());
        chatMeta.setLastHistory(lRequest.getContext());
        chatMeta.setRoleCardId(lRequest.getRoleCardId());

        RoleCard roleCard = roleCardMapper.selectById(lRequest.getRoleCardId());

        chatMeta.setRoleCardName(roleCard.getName());
        chatMeta.setMsgNum(lRequest.getMsgIndex());

        chatMetaMapper.upsert(chatMeta);
    }

    @Override
    public void chatMetaProcessAfter(LRequest lRequest, LResponse lResponse) {
        ChatMeta chatMeta = new ChatMeta();

        chatMeta.setChatId(lResponse.getChatId());
        chatMeta.setUserId(lResponse.getUserId());
        chatMeta.setLastHistory(lResponse.getInContext());
        chatMeta.setRoleCardId(lRequest.getRoleCardId());

        // todo 异步处理？？？
        RoleCard roleCard = roleCardMapper.selectById(lRequest.getRoleCardId());

        chatMeta.setRoleCardName(roleCard.getName());
        chatMeta.setMsgNum(lResponse.getMsgIndex());

        chatMetaMapper.upsert(chatMeta);
    }
}
