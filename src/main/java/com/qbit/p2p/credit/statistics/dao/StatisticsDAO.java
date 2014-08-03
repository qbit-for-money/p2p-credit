package com.qbit.p2p.credit.statistics.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.statistics.model.GlobalStatistics;
import com.qbit.p2p.credit.statistics.model.Statistics;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

/**
 * @author Alexander_Sergeev
 */
public class StatisticsDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	public Statistics find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					Statistics.class, id, null);
		} finally {
			entityManager.close();
		}
	}

	public GlobalStatistics findGlobal() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					GlobalStatistics.class, 0L, null);
		} finally {
			entityManager.close();
		}
	}

	public Statistics updateOpennessRating(final String id, final long openessRating) {
		if ((id == null) || id.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Statistics>() {

			@Override
			public Statistics call(EntityManager entityManager) {
				Statistics statistics = entityManager.find(Statistics.class, id, LockModeType.PESSIMISTIC_WRITE);
				if (statistics == null) {
					statistics = new Statistics(id);
				}
				statistics.setOpennessRating(openessRating);
				return entityManager.merge(statistics);
			}
		});
	}

	public Statistics updateUserOrdersStatistics(final Statistics userStatistics) {
		if (userStatistics == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Statistics>() {
			@Override
			public Statistics call(EntityManager entityManager) {
				Statistics statistics = entityManager.find(Statistics.class, userStatistics.getId(), LockModeType.PESSIMISTIC_WRITE);
				if (statistics == null) {
					statistics = new Statistics(userStatistics.getId());
				}
				statistics.setOrdersRating(userStatistics.getOrdersRating());
				statistics.setOrdersValue(userStatistics.getOrdersValue());
				statistics.setOrdersCount(userStatistics.getOrdersCount());
				statistics.setSuccessOrdersCount(userStatistics.getSuccessOrdersCount());
				return entityManager.merge(statistics);
			}
		});
	}

	public GlobalStatistics updateGlobalStatistics(final GlobalStatistics globalStatistics) {
		if (globalStatistics == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<GlobalStatistics>() {

			@Override
			public GlobalStatistics call(EntityManager entityManager) {
				GlobalStatistics statistics = entityManager.find(GlobalStatistics.class, globalStatistics.getId(), LockModeType.PESSIMISTIC_WRITE);
				if (statistics == null) {
					statistics = new GlobalStatistics();
				}
				statistics.setAllOrdersCount(globalStatistics.getAllOrdersCount());
				statistics.setAllSuccessOrdersCount(globalStatistics.getAllSuccessOrdersCount());
				return entityManager.merge(statistics);
			}
		});
	}
}
