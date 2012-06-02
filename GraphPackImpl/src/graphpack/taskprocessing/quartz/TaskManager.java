/*******************************************************************************
 * Copyright 2012 Amit Portnoy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package graphpack.taskprocessing.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import graphpack.INode;
import graphpack.extensions.Extensions;
import graphpack.taskprocessing.ITask;
import graphpack.taskprocessing.ITaskManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.SchedulerRepository;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * manage and executes tasks using <a href="http://quartz-scheduler.org/">the Quartz scheduler</a>
 * @author amitport
 */
public class TaskManager implements ITaskManager {
	final static int MAX_THREADS = 10;
	Scheduler s;

	public static String getTaskId(String clientName, String nodeName, String taskName){
		return clientName+"."+nodeName+"."+taskName;
	}
	@Inject
	public TaskManager(@Named("serviceName") String serviceName) {
		BasicConfigurator.configure(new NullAppender()); //silence quartz log4j
		
		try {
			SimpleThreadPool threadPool = new SimpleThreadPool(MAX_THREADS,
					Thread.NORM_PRIORITY);
			threadPool.initialize();
			JobStore jobStore = new RAMJobStore();

			DirectSchedulerFactory.getInstance().createScheduler(serviceName,
					DirectSchedulerFactory.DEFAULT_INSTANCE_ID, threadPool,
					jobStore);
			s = SchedulerRepository.getInstance().lookup(serviceName);
			s.start();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addTask(String clientName, String nodeName, String taskName,
			INode node, Extensions extensions, Class<? extends ITask> task) {
		JobDetail job = newJob(TaskJob.class).storeDurably()
				.withIdentity(getTaskId(clientName,nodeName,taskName))// todo clientName and nodeName
				.build();
		job.getJobDataMap().put("task",task);
		job.getJobDataMap().put("extensions",extensions);
		job.getJobDataMap().put("node",node);

		try {
			s.addJob(job, true);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void addScheduledTask(String clientName, String nodeName,
			String taskName, INode node, Extensions extensions,
			Class<? extends ITask> task, String cronExpression) {
        JobDetail job = newJob(TaskJobNoParams.class).storeDurably()
        		.withIdentity(getTaskId(clientName,nodeName,taskName))
				.build();
        job.getJobDataMap().put("task", task);
        job.getJobDataMap().put("extensions",extensions);
		job.getJobDataMap().put("node",node);
		
        Trigger trigger = newTrigger().withSchedule(cronSchedule(cronExpression)).build();

		try {
			s.scheduleJob(job, trigger);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void callTask(String clientName, String nodeName, String taskName,
			Object... params) {
		boolean exists;
		String taskId = getTaskId(clientName,nodeName,taskName);
		try {
			exists = s.checkExists(JobKey.jobKey(taskId));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		if (!exists) {
			throw new RuntimeException("task does not exist");
		}

		Trigger trigger = newTrigger().forJob(taskId).build();
		trigger.getJobDataMap().put("params", params);
		try {
			s.scheduleJob(trigger);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@DisallowConcurrentExecution
	@PersistJobDataAfterExecution
	public static class TaskJob implements Job {
		public void execute(JobExecutionContext context) {
			try {
				Class<?> taskClass = (Class<?>) context.getJobDetail()
						.getJobDataMap().get("task");
				ITask task = (ITask) taskClass.newInstance();
				INode node = (INode) context.getJobDetail().getJobDataMap().get("node");
				Extensions extensions = (Extensions) context.getJobDetail().getJobDataMap().get("extensions");
				Object[] params = (Object[]) context.getMergedJobDataMap().get(
						"params");
				task.execute(node,extensions.getExtendedNodeMap(node),params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@DisallowConcurrentExecution
	@PersistJobDataAfterExecution
	public static class TaskJobNoParams implements Job {
		public void execute(JobExecutionContext context) {
			try {
				Class<?> taskClass = (Class<?>) context.getJobDetail()
						.getJobDataMap().get("task");
				ITask task = (ITask) taskClass.newInstance();
				INode node = (INode) context.getJobDetail().getJobDataMap().get("node");
				Extensions extensions = (Extensions) context.getJobDetail().getJobDataMap().get("extensions");
				task.execute(node,extensions.getExtendedNodeMap(node));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
