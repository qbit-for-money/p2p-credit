package com.qbit.p2p.credit.statistics.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.statistics.model.GlobalStatistics;
import com.qbit.p2p.credit.statistics.model.Statistics;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.ws.rs.WebApplicationException;

/**
 * @author Alexander_Sergeev
 */
public class StatisticsDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;
	@Inject
	private Env env;

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
	
	public Statistics updatePartnersRating(final String id, final long partnersRating) {
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
				statistics.setPartnersRating(partnersRating);
				return entityManager.merge(statistics);
			}
		});
	}

	public long calculatePartnersRating(String userId) {
		if ((userId == null) || userId.isEmpty()) {
			throw new WebApplicationException();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Double> criteria = builder.createQuery(Double.class);
			Root<Statistics> statistics = criteria.from(Statistics.class);
			Expression<Double> openessRatingProd = builder.prod(statistics.<Double>get("opennessRating"), env.getUserRatingOpenessFactor());
			Expression<Double> successOrdersCountProd = builder.prod(statistics.<Double>get("successOrdersCount"), env.getUserRatingTransactionsFactor());
			Expression<Double> sumExpression = builder.sum(openessRatingProd, successOrdersCountProd);
			criteria.select(builder.sum(sumExpression));
			
			Subquery<String> usersIdSubquery = criteria.subquery(String.class);
			Root fromOrder = criteria.from(OrderInfo.class);
			usersIdSubquery.select(fromOrder.get("approvedUserId")).distinct(true);
			usersIdSubquery.where(
				builder.and(builder.equal(fromOrder.get("status"), OrderStatus.SUCCESS), builder.equal(fromOrder.get("userId"), userId)));
	
			criteria.where(builder.in(statistics.get("id")).value(usersIdSubquery));
			
			Double result = entityManager.createQuery(criteria).getSingleResult();
			return (result == null) ? 0 : result.longValue();
		} finally {
			entityManager.close();
		}
	}
}
