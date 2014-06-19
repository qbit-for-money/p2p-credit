package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.model.UserType;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

/**
 *
 * @author Александр
 */
@Singleton
public class OrderDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	@Inject
	private UserProfileDAO profileDAO;

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

	public OrderInfo create(final OrderInfo orderInfo) {
		if (orderInfo == null) {
			throw new IllegalArgumentException("Order is NULL.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderInfo>() {

			@Override
			public OrderInfo call(EntityManager entityManager) {
				UserInfo userInfo = UserDAO.findAndLock(entityManager, orderInfo.getUserPublicKey());
				if (userInfo == null) {
					return null;
				}
				UserPublicProfile profile = profileDAO.find(orderInfo.getUserPublicKey());
				if ((profile != null) && (profile.getName() != null) && !profile.getName().isEmpty()) {
					orderInfo.setUserName(profile.getName());
				}
				orderInfo.setStatus(OrderStatus.OPENED);
				orderInfo.setCreationDate(new Date());
				entityManager.persist(orderInfo);
				return orderInfo;
			}
		});
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

	public long length(String userPublicKey, String filterDataField, String query) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
			EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);

			Root<OrderInfo> order = criteria.from(OrderInfo.class);
			criteria.select(builder.count(order));
			if ((userPublicKey != null) && !userPublicKey.isEmpty()) {
				ParameterExpression<String> p = builder.parameter(String.class, "userPublicKey");
				criteria.where(builder.equal(order.<String>get("userPublicKey"), p));
			}
			if (filterDataField != null && query != null) {
				criteria.where(
					builder.or(
						builder.like(
							builder.lower(
								order.get(
									type.getDeclaredSingularAttribute(filterDataField, String.class)
								)
							), "%" + query.toLowerCase() + "%"
						)
					)
				);
			}

			return entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	public List<OrderInfo> findWithFilter(String userPublicKey, String filterValue0, String filterDatafield0, 
		String filterValue1, String filterDatafield1, String sortDataField, boolean sortDesc, int offset, int limit) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<OrderInfo> criteria = builder.createQuery(OrderInfo.class);
			EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);

			Root<OrderInfo> order = criteria.from(OrderInfo.class);
			criteria.select(order);
			if ((userPublicKey != null) && !userPublicKey.isEmpty()) {
				ParameterExpression<String> p = builder.parameter(String.class, "userPublicKey");
				criteria.where(builder.equal(order.<String>get("userPublicKey"), p));
			}
			if ((filterDatafield0 != null) && (filterValue0 != null)) {
				if (filterDatafield0.equals("reward")) {
					criteria.where(
						builder.and(
							builder.equal(
								order.get(
									type.getDeclaredSingularAttribute(filterDatafield0, Integer.class)
								), filterValue0)
						)
					);
				} else {
					criteria.where(
						builder.and(
							builder.like(
								builder.lower(
									order.get(
										type.getDeclaredSingularAttribute(filterDatafield0, String.class)
									)
								), "%" + filterValue0.toLowerCase() + "%"
							)
						)
					);
				}
			}
			if ((filterDatafield1 != null) && (filterValue1 != null)) {
				criteria.where(
					builder.or(
						builder.like(
							builder.lower(
								order.get(
									type.getDeclaredSingularAttribute(filterDatafield1, String.class)
								)
							), "%" + filterValue1.toLowerCase() + "%"
						)
					)
				);
			}

			if (sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.desc(order.get(sortDataField)));
			} else if (!sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.asc(order.get(sortDataField)));
			}
			TypedQuery<OrderInfo> query = entityManager.createQuery(criteria);
			if ((userPublicKey != null) && !userPublicKey.isEmpty()) {
				query.setParameter("userPublicKey", userPublicKey);
			}
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			List<OrderInfo> orders = query.getResultList();
			return orders;
		} finally {
			entityManager.close();
		}
	}
}
