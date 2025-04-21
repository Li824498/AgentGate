package com.mylearn.agentgate.mapper;

import com.mylearn.agentgate.core.entity.RoleCard;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleCardMapper {
    RoleCard selectById(int userId);

    List<RoleCard> selectAllRoleCards();

    void insertRoleCard(String name, String userId, String description, String avatarUrl, String startText, String settingText);
}
