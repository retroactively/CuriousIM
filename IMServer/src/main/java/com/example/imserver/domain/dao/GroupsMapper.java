package com.example.imserver.domain.dao;

import com.example.imserver.domain.po.Groups;

public interface GroupsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Groups record);

    int insertSelective(Groups record);

    Groups selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Groups record);

    int updateByPrimaryKey(Groups record);
}