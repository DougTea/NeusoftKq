package com.neusoft.datainsight.checkwork;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.neusoft.datainsight.checkwork.job.MyTimerTask;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			if (i == 0)
				System.setProperty(MyTimerTask.USER, args[0]);
			else if (i == 1)
				System.setProperty(MyTimerTask.PSW, args[1]);
			else
				System.setProperty(MyTimerTask.RETRY_TIMES, args[2]);

		}
		System.out.println("Start scheduler for user " + System.getProperty(MyTimerTask.USER, "kuangjq"));
		JobDetail mJobDetail = JobBuilder.newJob(com.neusoft.datainsight.checkwork.job.MyTimerTask.class).build();
		JobDetail eJobDetail = JobBuilder.newJob(com.neusoft.datainsight.checkwork.job.MyTimerTask.class).build();

		CronTrigger morning = TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(8, 20, 1, 2, 3, 4, 5)).build();
		CronTrigger evening = TriggerBuilder.newTrigger()
				.withSchedule(CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(19, 15, 1, 2, 3, 4, 5)).build();
		Scheduler scheduler;
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(mJobDetail, morning);
			scheduler.scheduleJob(eJobDetail, evening);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}
}
