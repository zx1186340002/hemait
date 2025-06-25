package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import jdk.internal.util.xml.impl.Input;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderedFormContentFilter orderedFormContentFilter;

    @Autowired
    private WorkspaceService workspaceService;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
       List<LocalDate> dateList = new ArrayList();
       dateList.add(begin);
       begin.plusDays(1);
       //计算指定日期后一天
       while(!begin.equals(end)){
        begin = begin.plusDays(1);
           dateList.add(begin);
       }
        List<Double> turnoverList = new ArrayList<>();
       for(LocalDate date:dateList){


           //查询date日期对应的营业数额，营业额是指状态为已完成的订单金额合计。
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null? 0.0 : turnover;
            turnoverList.add(turnover);
       }


     return TurnoverReportVO.builder()
             .dateList( StringUtils.join(dateList, ","))
             .turnoverList( StringUtils.join(turnoverList,","))
             .build();

    }

    /**
     * 查找指定区间的用户数量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
            List<LocalDate> dataList = new ArrayList<>();
            dataList.add(begin);
            while(!begin.equals(end)){
                begin = begin.plusDays(1);
                dataList.add(begin);
            }

        //select count(id) from user where create_time <? and create_time>?
        List<Integer> newUserList = new ArrayList<>();
            //新增人数
        //select count(id) from user where create_time<?
        List<Integer>  totalUserList = new ArrayList<>();
        //总人数
        for (LocalDate date:dataList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            Map map = new HashMap();
            map.put("end",endTime);
            Integer i = userMapper.countByMap(map);
            totalUserList.add(i);
            map.put("begin",beginTime);
            Integer j = userMapper.countByMap(map);
            newUserList.add(j);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dataList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 查询用户数量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
        dateList.add(begin);
        }
        List<Integer> newOrderList = new ArrayList<>();
        List<Integer>  totalOrderList = new ArrayList<>();

        for(LocalDate date:dateList){
            //查询每天的订单总数 select count(id) from orders where order_time >? and order_time <? ;
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            //查询每天的有效订单数量。select count(id) from orders where order_time >? and order_time <? and status == 5 ;
            Integer orderCount1 = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            totalOrderList.add(orderCount);
            newOrderList.add(orderCount1);
        }
        Integer newSum = newOrderList.stream().reduce(Integer::sum).get();
        Integer totalSum = totalOrderList.stream().reduce(Integer::sum).get();
        //计算订单完成率。

        Double orderCompletionRate = 0.0;
        if(totalSum != 0) {
            orderCompletionRate = newSum.doubleValue() / totalSum;
        }
        OrderReportVO build = OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .validOrderCountList(StringUtils.join(newOrderList, ","))
                .totalOrderCount(totalSum)
                .totalOrderCount(newSum)
                .orderCompletionRate(orderCompletionRate).build();
    return   build;
    }

    /**
     * 销量排名前10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getsalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> sakesTop = orderMapper.getSakesTop(beginTime, endTime);
        List<String> collect = sakesTop.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String join = StringUtils.join(collect, ",");
        List<Integer> collect1 = sakesTop.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String join1 = StringUtils.join(collect1, ",");
        return SalesTop10ReportVO.builder()
                .nameList(join)
                .numberList(join1)
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //查询数据库获取营业数据。
        BusinessDataVO  businessDataVO = new BusinessDataVO();
        LocalDate beginTime = LocalDate.now().minusDays(30);
        LocalDate endTime = LocalDate.now().plusDays(1);
        workspaceService.getBusinessData(LocalDateTime.of(beginTime, LocalTime.MIN), LocalDateTime.of(endTime, LocalTime.MAX));
        //通过POI写入文件中。
        //查询的路径是在当前主类的resources文件夹中查找。
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("/templ/运维数据博快报表.xls");
        try {
            XSSFWorkbook execl = new XSSFWorkbook(in);
            //填充数据。
            XSSFSheet sheet1 = execl.getSheet("Sheet1");
            sheet1.getRow(1).getCell(1).setCellValue("时间："+beginTime+"————"+endTime);
            //继续填充
            sheet1.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet1.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet1.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());

            sheet1.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet1.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());
            //概要数据完成

            for(int i =0 ;i<30;i++ ) {
                LocalDate date = LocalDate.now().plusDays(i);
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                sheet1.getRow(i+7).getCell(2).setCellValue(date.toString());
                sheet1.getRow(i+7).getCell(3).setCellValue(businessDataVO.getTurnover());
                sheet1.getRow(i+7).getCell(4).setCellValue(businessDataVO.getValidOrderCount());
                sheet1.getRow(i+7).getCell(5).setCellValue(businessDataVO.getOrderCompletionRate());
                sheet1.getRow(i+7).getCell(6).setCellValue(businessDataVO.getUnitPrice());
                sheet1.getRow(i+7).getCell(7).setCellValue(businessDataVO.getNewUsers());
            }
            //通过输出将excel文件导出到浏览器中
            ServletOutputStream outputStream = response.getOutputStream();
            execl.write(outputStream);
            execl.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        orderMapper.sumByMap(map);
        return orderMapper.countByMap(map);
    }

}
