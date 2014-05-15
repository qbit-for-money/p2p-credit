package com.qbit.p2p.credit;

import com.qbit.commons.dao.util.DAOExecutor;
import com.qbit.commons.dao.util.DefaultDAOExecutor;
import com.qbit.commons.env.CommonsEnv;
import com.qbit.commons.mail.MailService;
import com.qbit.commons.user.UserDAO;
import com.qbit.p2p.credit.env.Env;
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
		
		configuration.commit();
	}

	/**
	 * Called on application shutdown. We need this workaround because fucking
	 * Jersey 2.5.1 doesn't process @PreDestroy annotated methods in another
	 * classes except this one.
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
