package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户信息。
     * @param openid
     * @return
     */
    @Select("select  * from  user  where openid = #{openid}")
    User getByUserId(String openid);

    /**
     * 插入用户并且返回用户个人信息。
     */
    User insert(User user);


    /**
     * 根据动态时间统计用户数量。
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
