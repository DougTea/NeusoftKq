/**
 * 
 */
package com.neusoft.datainsight.checkwork.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.neusoft.datainsight.checkwork.Attendance;

/**
 * @author kuangjq
 *
 */
public class MyTimerTask implements Job  {
	
	public static final String USER="user";
	public static final String PSW="psw";
	public static final String RETRY_TIMES="retrytime";

	
	Attendance at = new Attendance();

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		if(at.tryToSignIn(System.getProperty(USER, "kuangjq"),System.getProperty(PSW, "Aa123456"),Integer.parseInt(System.getProperty(RETRY_TIMES, "10")))) {
			System.out.println("==================================================================");
			System.out.println(String.format("Success sign in at %s",new Date().toString()));
			System.out.println("==================================================================");
		}
		else {
			System.out.println("==================================================================");
			System.out.println(String.format("Failed to sign in at %s",new Date().toString()));
			System.out.println("==================================================================");
		}
	}

}
