package com.qbit.p2p.credit.statistics.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.statistics.model.GlobalStatistics;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.ws.rs.WebApplicationException;

/**
 * @author Alexander_Sergeev
 */
public class StatisticsDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;
	@Inject
	private UserDAO userDAO;

	public Statistics find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					Statistics.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public GlobalStatistics getGlobalStatistics() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			System.out.println("%%% " + DAOUtil.find(entityManagerFactory.createEntityManager(),
					GlobalStatistics.class, 0L, null));
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					GlobalStatistics.class, 0L, null);
		} finally {
			entityManager.close();
		}
	}

	public Statistics create(final String publicKey) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<Statistics>() {

			@Override
			public Statistics call(EntityManager entityManager) {
				UserInfo user = userDAO.find(publicKey);
				if (user == null) {
					throw new WebApplicationException();
				}
				Statistics statistics = new Statistics(publicKey);

				entityManager.persist(statistics);

				return statistics;
			}
		}
		);
	}

	public GlobalStatistics maybeCreateGlobalStatistics() {
		return invokeInTransaction(entityManagerFactory, new TrCallable<GlobalStatistics>() {

			@Override
			public GlobalStatistics call(EntityManager entityManager) {
				GlobalStatistics statistics = entityManager.find(GlobalStatistics.class, 0L, LockModeType.PESSIMISTIC_WRITE);
				if (statistics == null) {
					statistics = new GlobalStatistics();
					entityManager.persist(statistics);
				}
				return statistics;
			}
		});
	}

	public Statistics setProfileRating(final Statistics userStatistics) {
		if (userStatistics == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Statistics>() {

			@Override
			public Statistics
					call(EntityManager entityManager) {
				Statistics statistics = entityManager.find(Statistics.class, userStatistics.getId(), LockModeType.PESSIMISTIC_WRITE);
				if (statistics == null) {
					return null;
				}
				statistics.setOpennessRating(userStatistics.getOpennessRating());
				statistics.setSummaryRating(userStatistics.getSummaryRating());
				return statistics;
			}
		});
	}

	public Statistics setUserOrdersStatistics(final Statistics userStatistics) {
		if (userStatistics == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Statistics>() {

			@Override
			public Statistics
					call(EntityManager entityManager) {
				Statistics statistics = entityManager.find(Statistics.class, userStatistics.getId(), LockModeType.PESSIMISTIC_WRITE);
				if (statistics == null) {
					return null;
				}

				statistics.setOrdersSumValue(userStatistics.getOrdersSumValue());
				statistics.setSuccessTransactionsSum(userStatistics.getSuccessTransactionsSum());
				statistics.setTransactionsSum(userStatistics.getTransactionsSum());
				statistics.setTransactionsRating(userStatistics.getTransactionsRating());
				statistics.setSummaryRating(userStatistics.getSummaryRating());
				return statistics;
			}
		});
	}

	public GlobalStatistics setGlobalStatistics(final GlobalStatistics globalStatistics) {
		if (globalStatistics == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<GlobalStatistics>() {

			@Override
			public GlobalStatistics
					call(EntityManager entityManager) {
				GlobalStatistics statistics = entityManager.find(GlobalStatistics.class, globalStatistics.getId(), LockModeType.PESSIMISTIC_WRITE);
				if (statistics == null) {
					return null;
				}
				statistics.setAllTransactionsSum(globalStatistics.getAllTransactionsSum());
				statistics.setAllSuccessTransactionsSum(globalStatistics.getAllSuccessTransactionsSum());
				return statistics;
			}
		});
	}
}
