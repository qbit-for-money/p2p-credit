package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.user.model.UserType;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author Александр
 */
@Singleton
public class OrderDAO {

	@Inject
	private Env env;

	@Inject
	private EntityManagerFactory entityManagerFactory;

	public OrderInfo find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					OrderInfo.class, id, null);
		} finally {
			entityManager.close();
		}
	}

	public List<OrderInfo> findByUser(String userPublicKey, int offset, int limit) {
		if ((userPublicKey == null) || userPublicKey.isEmpty()) {
			return Collections.emptyList();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<OrderInfo> query = entityManager.createNamedQuery("OrderInfo.findByUser", OrderInfo.class);
			query.setParameter("userPublicKey", userPublicKey);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}

	public List<OrderInfo> findByUserType(UserType userType, int offset, int limit) {
		if (userType == null) {
			throw new IllegalArgumentException();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<OrderInfo> query = entityManager.createNamedQuery("OrderInfo.findByUserType", OrderInfo.class);
			query.setParameter("userType", userType);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public List<OrderInfo> findByUserAndTimestamp(String userPublicKey, Date creationDate, int offset, int limit) {
		if ((userPublicKey == null) || userPublicKey.isEmpty() || (creationDate == null)) {
			throw new IllegalArgumentException();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<OrderInfo> query = entityManager.createNamedQuery("OrderInfo.findByUserAndTimestamp", OrderInfo.class);
			query.setParameter("userPublicKey", userPublicKey);
			query.setParameter("creationDate", creationDate);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public Long findMedianVolumeOfSuccess() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<Long> query = entityManager.createNamedQuery("OrderInfo.findMedianVolumeOfSuccess", Long.class);
			query.setParameter("status", OrderStatus.SUCCESS);
			return query.getSingleResult();
		} finally {
			entityManager.close();
		}
	}
	
	public OrderInfo create(OrderInfo orderInfo) {
		if (orderInfo == null) {
			throw new IllegalArgumentException("Order is NULL.");
		}
		return create(orderInfo.getUserPublicKey());
	}

	public OrderInfo create(final String userPublicKey) {
		if (userPublicKey == null || userPublicKey.isEmpty()) {
			throw new IllegalArgumentException("Order is inconsistent.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderInfo>() {

			@Override
			public OrderInfo call(EntityManager entityManager) {
				UserInfo userInfo = UserDAO.findAndLock(entityManager, userPublicKey);
				if (userInfo == null) {
					return null;
				}
				OrderInfo order = new OrderInfo();
				order.setCreationDate(new Date());
				order.setUserPublicKey(userPublicKey);
				order.setStatus(OrderStatus.OPENED);
				entityManager.persist(order);
				return order;
			}
		});
	}
}
