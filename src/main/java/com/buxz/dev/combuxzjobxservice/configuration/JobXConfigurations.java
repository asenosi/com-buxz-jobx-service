package com.buxz.dev.combuxzjobxservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("jobx.scheduling")
public class JobXConfigurations {

    public String update = "*/10 * * * * *";

}
