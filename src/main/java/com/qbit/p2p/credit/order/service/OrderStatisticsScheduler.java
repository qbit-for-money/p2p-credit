package com.qbit.p2p.credit.order.service;

import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class OrderStatisticsScheduler {

	public static final class StatisticsFuture {

		private final Future<?> future;
		private final String taskId;

		public StatisticsFuture(Future future, String taskId) {
			this.future = future;
			this.taskId = taskId;
		}

		public Future getFuture() {
			return future;
		}

		public String getTaskId() {
			return taskId;
		}

		@Override
		public String toString() {
			return "StatisticsFuture{" + "future=" + future + ", taskId=" + taskId + '}';
		}
	}
	
	private static final int EXECUTOR_SERVICES_MAX_COUNT = 10;
	private static final int NUMBER_OF_THREADS_IN_THE_POOL = 2;
	private static final int INIT_DELAY = 1;

	@Inject
	private Env env;
	@Inject
	private StatisticsService statisticsService;

	private ScheduledExecutorService executorService;

	private final ExecutorService[] threadPoolExecutorServices = new ExecutorService[10];
	private final Map<Integer, List<StatisticsFuture>> tasksMap = new HashMap<>();

	@PostConstruct
	public void init() {
		for (int i = 0; i < EXECUTOR_SERVICES_MAX_COUNT; i++) {
			threadPoolExecutorServices[i] = Executors.newFixedThreadPool(NUMBER_OF_THREADS_IN_THE_POOL, new ThreadFactory() {

				@Override
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable, "StatisticsService");
					thread.setDaemon(true);
					return thread;
				}
			});
		}
		executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable, "OrderFlowWorker");
				thread.setDaemon(true);
				return thread;
			}
		});
		executorService.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				statisticsService.recalculateGlobalStatistics();
			}
		}, env.getUpdateGlobalStatisticsWorkerPeriodHours(), env.getUpdateGlobalStatisticsWorkerPeriodHours(), TimeUnit.HOURS);
	}

	public void putTask(final Runnable runnable, final String taskId, final String userId) {
		if ((runnable == null) || (taskId == null) || taskId.isEmpty() || (userId == null) || userId.isEmpty()) {
			return;
		}
		final int userPosition = Math.abs(userId.hashCode() % 10);
		List<StatisticsFuture> statisticsFutures = tasksMap.get(userPosition);
		if(statisticsFutures == null) {
			statisticsFutures = new ArrayList<>();
		}
		Iterator<StatisticsFuture> iterator = statisticsFutures.iterator();
		while (iterator.hasNext()) {
			StatisticsFuture statisticsFuture = iterator.next();
			Future future = statisticsFuture.getFuture();
			if (future.isDone()) {
				iterator.remove();
				future = null;
			}
			if ((future != null) && taskId.equals(statisticsFuture.getTaskId())) {
				return;
			}
		}
		Future<?> future = threadPoolExecutorServices[userPosition].submit(new Runnable() {
			@Override
			public void run() {
				executorService.schedule(runnable, INIT_DELAY, TimeUnit.SECONDS);
			}
		});
		List<StatisticsFuture> futuresByUser = tasksMap.get(userPosition);
		if (futuresByUser == null) {
			futuresByUser = new ArrayList<>();
		}
		futuresByUser.add(new StatisticsFuture(future, taskId));
		tasksMap.put(userPosition, futuresByUser);
	}

	@PreDestroy
	public void shutdown() {
		try {
			executorService.shutdown();
			for (ExecutorService threadPoolExecutorService : threadPoolExecutorServices) {
				threadPoolExecutorService.shutdown();
			}
		} catch (Throwable ex) {
			// Do nothing
		}
	}
}
