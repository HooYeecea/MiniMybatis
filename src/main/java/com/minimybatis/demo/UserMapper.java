package com.minimybatis.demo;

import java.util.List;

public interface UserMapper {

    User findById(Integer id);

    List<User> findAll();

    int insert(User user);

    int update(User user);

    int deleteById(Integer id);
}
