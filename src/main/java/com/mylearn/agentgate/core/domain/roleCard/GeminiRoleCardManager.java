package com.mylearn.agentgate.core.domain.roleCard;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.RoleCard;
import com.mylearn.agentgate.mapper.RoleCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeminiRoleCardManager implements RoleCardManager{

    @Autowired
    private RoleCardMapper roleCardMapper;


    @Override
    public RoleCard process(LRequest lRequest) {
        return roleCardMapper.selectById(lRequest.getRoleCardId());
    }
}
