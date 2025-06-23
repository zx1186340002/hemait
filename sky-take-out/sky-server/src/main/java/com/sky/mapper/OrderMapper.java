package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

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

}
