package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
         LocalDateTime  localDateTime= LocalDateTime.now().plusMinutes(-15);
        log.info("定时处理超时订单");
        List<Orders> orderlist = orderMapper.getByStatusAndOrdersTimeLT(Orders.PENDING_PAYMENT, localDateTime);
        if (orderlist!= null && orderlist.size()>0) {
            for (Orders order : orderlist) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelTime(LocalDateTime.now());
                order.setCancelReason("订单超时自动取消");
            }
        }
    }

    /**
     * 处理一致处于派送中的订单
     */

    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder() {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> list = orderMapper.getByStatusAndOrdersTimeLT(Orders.DELIVERY_IN_PROGRESS, localDateTime);
        if (list!= null && list.size()>0) {
            for (Orders order : list) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
