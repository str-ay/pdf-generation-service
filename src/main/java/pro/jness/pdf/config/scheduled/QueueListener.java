package pro.jness.pdf.config.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import pro.jness.pdf.utils.ClassNameUtil;
import pro.jness.pdf.utils.PdfQueueService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author Aleksandr Streltsov (jness.pro@gmail.com)
 *         on 27/08/16
 */
@Configuration
@EnableScheduling
public class QueueListener implements SchedulingConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ClassNameUtil.getCurrentClassName());

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduler());
        taskRegistrar.addFixedDelayTask(() -> logger.info(PdfQueueService.getInfo()), 5000);
    }

    @Bean(destroyMethod = "shutdown")
    public Executor scheduler() {
        return Executors.newScheduledThreadPool(1);
    }
}
