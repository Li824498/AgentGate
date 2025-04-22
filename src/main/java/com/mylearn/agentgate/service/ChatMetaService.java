package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.ChatMeta;
import com.mylearn.agentgate.mapper.ChatMetaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMetaService {
    @Autowired
    private ChatMetaMapper chatMetaMapper;

    public List<ChatMeta> selectAllChatMetas() {
        return chatMetaMapper.selectAllChatMetas();
    }
}
