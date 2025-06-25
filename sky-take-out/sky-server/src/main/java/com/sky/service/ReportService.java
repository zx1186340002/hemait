package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

public interface ReportService {

    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计订单数量
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量前十排名
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getsalesTop10(LocalDate begin, LocalDate end);

    /**
     * 导出运行报表
     * @param response
     */
    void exportBusinessData(HttpServletResponse response);
}
