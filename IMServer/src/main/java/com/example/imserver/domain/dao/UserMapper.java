package com.example.imserver.domain.dao;

import com.example.imserver.domain.po.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    String queryUserPassword(String userId);

    User queryUserById(String userId);

    List<User> queryAllUsers();

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}