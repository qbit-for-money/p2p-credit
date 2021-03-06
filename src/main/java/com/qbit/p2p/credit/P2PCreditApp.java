package com.qbit.p2p.credit;

import com.qbit.commons.dao.util.DAOExecutor;
import com.qbit.commons.dao.util.DefaultDAOExecutor;
import com.qbit.commons.env.CommonsEnv;
import com.qbit.commons.mail.MailService;
import com.qbit.commons.user.UserDAO;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.like.dao.LikeDAO;
import com.qbit.commons.log.dao.LogDAO;
import com.qbit.commons.log.service.LogScheduler;
import com.qbit.p2p.credit.material.dao.MaterialDAO;
import com.qbit.p2p.credit.message.dao.MessageDAO;
import com.qbit.p2p.credit.order.dao.CategoryDAO;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.dao.OrderFlowDAO;
import com.qbit.p2p.credit.order.resource.CategoriesResource;
import com.qbit.p2p.credit.order.resource.ResponsesResource;
import com.qbit.p2p.credit.order.service.OrderStatisticsScheduler;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import com.qbit.p2p.credit.user.dao.LanguageDAO;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.core.Application;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import static org.glassfish.jersey.internal.inject.Injections.*;

/**
 *
 * @author Alexander_Alexandrov
 */
public class P2PCreditApp extends Application {

	@Inject
	private ServiceLocator serviceLocator;

	private EntityManagerFactory entityManagerFactory;

	public P2PCreditApp() {
	}

	@PostConstruct
	public void init() {
		DynamicConfiguration configuration = getConfiguration(serviceLocator);

		addBinding(newBinder(CommonsEnv.class).to(CommonsEnv.class).in(Singleton.class), configuration);
		addBinding(newBinder(Env.class).to(Env.class).in(Singleton.class), configuration);

		addBinding(newBinder(MailService.class).to(MailService.class).in(Singleton.class), configuration);

		entityManagerFactory = Persistence.createEntityManagerFactory("P2PCreditPU");
		addBinding(newBinder(entityManagerFactory).to(EntityManagerFactory.class), configuration);

		addBinding(newBinder(DefaultDAOExecutor.class).to(DAOExecutor.class).in(Singleton.class), configuration);

		addBinding(newBinder(UserDAO.class).to(UserDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(UserProfileDAO.class).to(UserProfileDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(LanguageDAO.class).to(LanguageDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(OrderDAO.class).to(OrderDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(OrderFlowDAO.class).to(OrderFlowDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(CategoryDAO.class).to(CategoryDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(CategoriesResource.class).to(CategoriesResource.class).in(Singleton.class), configuration);
		addBinding(newBinder(ResponsesResource.class).to(ResponsesResource.class).in(Singleton.class), configuration);
		addBinding(newBinder(MaterialDAO.class).to(MaterialDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(StatisticsDAO.class).to(StatisticsDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(StatisticsService.class).to(StatisticsService.class).in(Singleton.class), configuration);
		addBinding(newBinder(LikeDAO.class).to(LikeDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(LogDAO.class).to(LogDAO.class).in(Singleton.class), configuration);
		addBinding(newBinder(MessageDAO.class).to(MessageDAO.class).in(Singleton.class), configuration);
		
		addBinding(newBinder(OrderStatisticsScheduler.class).to(OrderStatisticsScheduler.class).in(Singleton.class), configuration);
		addBinding(newBinder(LogScheduler.class).to(LogScheduler.class).in(Singleton.class), configuration);
		

		configuration.commit();
		serviceLocator.createAndInitialize(OrderStatisticsScheduler.class);
		serviceLocator.createAndInitialize(LogScheduler.class);
	}

	/**
	 * Called on application shutdown. We need this workaround because fucking Jersey 2.5.1 doesn't process
	 * @PreDestroy annotated methods in another classes except this one.
	 */
	@PreDestroy
	public void shutdown() {
		try {
			serviceLocator.shutdown();
		} catch (Throwable ex) {
			// Do nothing
		}
		try {
			entityManagerFactory.close();
		} catch (Throwable ex) {
			// Do nothing
		}
	}
}
