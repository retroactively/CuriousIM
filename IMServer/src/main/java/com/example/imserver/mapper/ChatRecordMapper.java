package com.example.imserver.mapper;

import com.example.imserver.domain.ChatRecord;

public interface ChatRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChatRecord record);

    int insertSelective(ChatRecord record);

    ChatRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChatRecord record);

    int updateByPrimaryKey(ChatRecord record);
}