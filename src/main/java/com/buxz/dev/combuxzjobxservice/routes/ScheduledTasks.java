package com.buxz.dev.combuxzjobxservice.routes;

import com.buxz.dev.combuxzjobxservice.service.HouseKeepingService;
import com.buxz.dev.combuxzjobxservice.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired JobService jobService;
    @Autowired HouseKeepingService houseKeepingService;

    @Scheduled(cron = "${jobx.scheduling.updateToStatusNew}")
    public void updateJobEntriesToStatusNew() {
        houseKeepingService.updateJobEntriesToNew();
    }

    @Scheduled(cron = "${jobx.scheduling.updateClosingDateDueCron}")
    private void updateClosingDateDue() {
        houseKeepingService.getAllJobEntryWithApproachingDueDate();
    }

    @Scheduled(cron = "${jobx.scheduling.permanentlyDeleteJobCron}")
    private void permanentlyDeleteJobEntries() {
        houseKeepingService.permanentlyDeleteJobEntries();
    }
}
