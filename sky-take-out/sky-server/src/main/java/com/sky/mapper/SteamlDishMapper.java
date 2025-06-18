package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SteamlDishMapper {

    /**
     * 根据ID查询ID
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdByDishIDs(List<Long> dishIds);
}
