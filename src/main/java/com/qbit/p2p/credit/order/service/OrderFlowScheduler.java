package com.qbit.p2p.credit.order.service;

import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.order.resource.OrdersResource;
import java.util.concurrent.Executors;
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
public class OrderFlowScheduler {

	@Inject
	private Env env;

	@Inject
	private OrdersResource ordersResource;
	
	private ScheduledExecutorService executorService;

	@PostConstruct
	public void init() {
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
				ordersResource.writeLastOrders();
			}
		}, env.getUpdateLastOrdersPeriodSecs(), env.getUpdateLastOrdersPeriodSecs(), TimeUnit.HOURS);
	}

	@PreDestroy
	public void shutdown() {
		try {
			executorService.shutdown();
		} catch (Throwable ex) {
			// Do nothing
		}
	}
}
