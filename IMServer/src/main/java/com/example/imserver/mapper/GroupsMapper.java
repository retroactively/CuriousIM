package com.example.imserver.mapper;

import com.example.imserver.domain.Groups;

public interface GroupsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Groups record);

    int insertSelective(Groups record);

    Groups selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Groups record);

    int updateByPrimaryKey(Groups record);
}