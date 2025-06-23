package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyTask {

    /**
     * 开启定时任务类
     */
    @Scheduled(cron ="0/5 * * * * *")
    public void executeTask() {
        log.info("定时发消息");
    }
}
