package com.mylearn.agentgate.core.domain.roleCard;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.RoleCard;

public interface RoleCardManager {
    RoleCard process(LRequest lRequest);
}
