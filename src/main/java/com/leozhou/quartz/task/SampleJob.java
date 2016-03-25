package com.leozhou.quartz.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouchunjie on 16/3/25.
 */
public class SampleJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(SampleJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("sample job running.");
    }
}
