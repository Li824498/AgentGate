package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.RoleCard;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleCardMapper {
    RoleCard selectById(int id);

}
