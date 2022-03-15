package com.example.imserver.domain.dao;

import com.example.imserver.domain.po.TalkBox;

public interface TalkBoxMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TalkBox record);

    int insertSelective(TalkBox record);

    TalkBox selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TalkBox record);

    int updateByPrimaryKey(TalkBox record);
}