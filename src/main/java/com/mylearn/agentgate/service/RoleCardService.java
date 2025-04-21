package com.mylearn.agentgate.service;

import com.mylearn.agentgate.core.entity.RoleCard;
import com.mylearn.agentgate.mapper.RoleCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleCardService {
    @Autowired
    private RoleCardMapper roleCardMapper;

    public List<RoleCard> selectAllRoleCards() {
        return roleCardMapper.selectAllRoleCards();
    }

    public void insertRoleCard(String name, String userId, String description, String avatarUrl, String startText, String settingText) {
        roleCardMapper.insertRoleCard(name, userId, description, avatarUrl, startText, settingText);
    }
}
