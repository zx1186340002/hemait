package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param order
     */
    void insert(Orders order);



    /**
     * 根据订单状态查询订单。
     * @param status
     * @param ordersTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{ordersTime} ")
    List<Orders> getByStatusAndOrdersTimeLT(Integer status, LocalDateTime ordersTime);


    void update(Orders order);

    /**
     *动态获取
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 查询订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);


    /**
     * 指定时间内的排名
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSakesTop(LocalDateTime begin, LocalDateTime end);
}
