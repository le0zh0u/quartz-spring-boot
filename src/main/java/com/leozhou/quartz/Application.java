package com.leozhou.quartz;

import com.leozhou.quartz.task.config.SchedulerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Created by zhouchunjie on 16/3/25.
 */
@SpringBootApplication
@Import({SchedulerConfig.class})
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        logger.info("start run application");
        SpringApplication.run(Application.class, args);
    }

}
