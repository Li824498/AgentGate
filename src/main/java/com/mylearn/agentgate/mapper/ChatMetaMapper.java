package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.ChatMeta;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMetaMapper {

    List<ChatMeta> selectAllChatMetas();

    void update(ChatMeta chatMeta);

    void insert(ChatMeta chatMeta);

    ChatMeta selectByChatId(String chatId);

    void upsert(ChatMeta chatMeta);
}
