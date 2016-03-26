package com.leozhou.quartz.task.jobs;

import com.leozhou.quartz.service.QuartzService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhouchunjie on 16/3/25.
 */
public class SampleJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(SampleJob.class);

    @Autowired
    private QuartzService quartzService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        quartzService.printTask();
    }
}
