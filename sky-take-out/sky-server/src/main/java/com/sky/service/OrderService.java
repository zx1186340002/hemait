package com.sky.service;


import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {

    /**
     * 提交
     * @param orderSubmitDTO
     * @return
     */
    OrderSubmitVO submit(OrdersSubmitDTO orderSubmitDTO);
}
