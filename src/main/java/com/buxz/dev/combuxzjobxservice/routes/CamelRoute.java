package com.buxz.dev.combuxzjobxservice.routes;

import com.buxz.dev.combuxzjobxservice.configuration.JobXConfigurations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

@Component
public class CamelRoute  {


    private final JobXConfigurations jobXConfigurations;

    @Autowired
    public CamelRoute(JobXConfigurations jobXConfigurations) {
        this.jobXConfigurations = jobXConfigurations;
    }

    /*private void updateJonEntriesToNew() {
        from(format("quartz2://timers/UpdateToNewStatusTimer?cron=%s", jobXConfigurations.getUpdateToNewStatusCron()))
                .to(endpoint("bean:jobService?method=updateJonEntriesToNew()"))
                .routeId("Update-Job-State-to-NEW")
                .description("Quartz trigger to update JobState to NEW for newly CREATED JobEntry")
                .setAutoStartup(Boolean.TRUE.toString());
    } */
}
