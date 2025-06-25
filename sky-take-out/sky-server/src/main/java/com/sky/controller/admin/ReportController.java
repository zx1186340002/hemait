package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/repost")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;
    /**
     * 营业额统计。
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics( @DateTimeFormat(pattern = "yyyy-NN-dd") LocalDate begin
            ,  @DateTimeFormat(pattern = "yyyy-NN-dd")     LocalDate end) {
            log.info("日志统计");
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    @GetMapping("user/statistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
                                                ){
        UserReportVO userStatistics = reportService.getUserStatistics(begin, end);
        return Result.success(userStatistics);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数量统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    ){
        return Result.success(reportService.getOrderStatistics(begin, end));

    }

    @GetMapping("/top10")
    @ApiOperation("销量排名")
    public Result<SalesTop10ReportVO> top10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                            @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end
    ){
        log.info("销量排名");
        return Result.success(reportService.getsalesTop10(begin,end));

    }

    /**
     * 导出运营数据报表，其中导出需要用到输出流来传递初期。
     * @param response
     */
    @GetMapping("/export")
    @ApiOperation("输入流")
    public void export(HttpServletResponse response){
        reportService.exportBusinessData(response);

    }



}
