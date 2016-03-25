package com.leozhou.quartz;

import com.leozhou.quartz.mybatis.conf.CustomDataSourceProperties;
import com.leozhou.quartz.task.SampleJob;
import com.leozhou.quartz.task.config.AutowiringSpringBeanJobFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by zhouchunjie on 16/3/25.
 */
@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

    @Autowired
    CustomDataSourceProperties properties;

    //配置可自动注入来自spring 上下文的对象
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory = new AutowiringSpringBeanJobFactory();
        autowiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        return autowiringSpringBeanJobFactory;
    }

    //根据调度参数和可执行的工作进行执行
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory,
            @Qualifier("sampleJobTrigger") Trigger sampleJobTrigger) throws IOException {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        // this allows to update triggers in DB when updating settings in config file:
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setDataSource(dataSource);
        factoryBean.setJobFactory(jobFactory);

        factoryBean.setQuartzProperties(quartzProperties());
        factoryBean.setTriggers(sampleJobTrigger);

        return factoryBean;
    }

    //引入定时任务配置文件
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    //注入定时任务Detail
    @Bean
    public JobDetailFactoryBean sampleJobDetail() {
        return createJobDetail(SampleJob.class);
    }

    //抽象出创建JobDetail的方法
    private static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        //job has to be durable to be stored in DB
        factoryBean.setDurability(true);
        return factoryBean;
    }

    //注入调度参数
    @Bean(name = "sampleJobTrigger")
    public SimpleTriggerFactoryBean sampleJobTrigger(@Qualifier("sampleJobDetail") JobDetail jobDetail,
            @Value("${samplejob.frequency}") long frequency) {

        return createTrigger(jobDetail, frequency);
    }

    //抽象出创建单个调度参数的方法
    private static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0l);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        // in case of misfire, ignore all missed triggers and continue
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return factoryBean;
    }

    //配置类按揭数据库的dataSource
    @Bean
    public DataSource getDataSource() throws SQLException {
        DataSource dataSource = new DataSource();
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setUrl(properties.getUrl());
        dataSource.setUsername(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        return dataSource;
    }

}
