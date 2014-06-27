package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.OrderType;
import com.qbit.p2p.credit.order.model.OrdersData;
import com.qbit.p2p.credit.order.model.FilterCriteriaValue;
import com.qbit.p2p.credit.order.resource.OrdersResource;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserCurrency;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.model.UserType;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
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

	public List<OrderInfo> findByType(OrderType orderType, int offset, int limit) {
		if (orderType == null) {
			throw new IllegalArgumentException();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<OrderInfo> query = entityManager.createNamedQuery("OrderInfo.findByType", OrderInfo.class);
			query.setParameter("orderType", orderType);
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
				orderInfo.setStatus(OrderStatus.SUCCESS);
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
				order.setStatus(OrderStatus.SUCCESS);
				entityManager.persist(order);
				return order;
			}
		});
	}

	public OrdersData findWithFilter(String userPublicKey, FilterCriteriaValue filterCriteriaValue, UserPublicProfile profile, boolean isLength) {
		boolean sortDesc = false;
		if ((filterCriteriaValue != null) && filterCriteriaValue.getSortOrder() != null && filterCriteriaValue.getSortOrder().equals("desc")) {
			sortDesc = true;
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria;
			EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);

			
			if(isLength) {
				criteria = builder.createQuery(Long.class);
			} else {
				criteria = builder.createQuery(OrderInfo.class);
			}
			Root<OrderInfo> order = criteria.from(OrderInfo.class);
			if(isLength) {
				criteria.select(builder.count(order));
			} else {
				criteria.select(order);
			}
			Predicate mainOperatorPredicate = null;
			if ((userPublicKey != null) && !userPublicKey.isEmpty()) {
				Expression<String> typeExpression = order.get("userPublicKey");
				mainOperatorPredicate = builder.equal(typeExpression, userPublicKey);
			}
			if ((filterCriteriaValue != null) && (filterCriteriaValue.getOrderType() != null)) {
				Expression<OrderType> typeExpression = order.get("type");
				Predicate typePredicate = builder.equal(typeExpression, filterCriteriaValue.getOrderType());

				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = typePredicate;
				} else {
					mainOperatorPredicate = builder.and(typePredicate, mainOperatorPredicate);
				}
			}

			List<FilterCriteriaValue.FilterItem> filterItems = (filterCriteriaValue != null) ? filterCriteriaValue.getFilterItems() : null;

			if (filterItems != null && !filterItems.isEmpty()) {
				Predicate itemsOperatorPredicate = null;
				for (FilterCriteriaValue.FilterItem item : filterItems) {
					if ((item.getFilterDataField() != null) && (item.getFilterValue() != null)) {
						//ParameterExpression<String> parameter = builder.parameter(String.class, item.getFilterDataField());
						//Predicate valuePredicate = builder.equal(builder.lower(order.<String>get(item.getFilterDataField())), "%" + item.getFilterValue().toLowerCase() + "%");
						Predicate valuePredicate = null;
						if ((item.getFilterCondition() == null) || "EQUAL".equals(item.getFilterCondition())) {
							if("status".equals(item.getFilterDataField())) {
								valuePredicate = builder.equal(order.get(item.getFilterDataField()), OrderStatus.valueOf(item.getFilterValue()));
							} else if("currency".equals(item.getFilterDataField())) {
								valuePredicate = builder.equal(order.get(item.getFilterDataField()), Currency.valueOf(item.getFilterValue()));
							} else {
								valuePredicate = builder.equal(order.get(item.getFilterDataField()), item.getFilterValue());
							}	
						} else if ("NOT_EQUAL".equals(item.getFilterCondition())) {
							//Expression<OrderType> typeExpression = order.get("type");
							if("status".equals(item.getFilterDataField())) {
								valuePredicate = builder.notEqual(order.get(item.getFilterDataField()), OrderStatus.valueOf(item.getFilterValue()));
							} else {
								valuePredicate = builder.notEqual(order.get(item.getFilterDataField()), item.getFilterValue());
							}	
						} else if ("STARTS_WITH".equals(item.getFilterCondition())) {

							valuePredicate = builder.like(
								builder.lower(
									order.get(
										type.getDeclaredSingularAttribute(item.getFilterDataField(), String.class)
									)
								), "%" + item.getFilterValue().toLowerCase() + "%"
							);
						}
						if (itemsOperatorPredicate == null) {
							itemsOperatorPredicate = valuePredicate;
						} else if ((item.getFilterOperator() == null) || FilterOperator.AND == item.getFilterOperator()) {
							itemsOperatorPredicate = builder.and(valuePredicate, itemsOperatorPredicate);
						} else if (FilterOperator.OR == item.getFilterOperator()) {
							itemsOperatorPredicate = builder.or(valuePredicate, itemsOperatorPredicate);
						}
					}
				}
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = itemsOperatorPredicate;
				} else {
					mainOperatorPredicate = builder.and(itemsOperatorPredicate, mainOperatorPredicate);
				}
			}
			List<String> userLanguages = (profile != null) ? profile.getLanguages() : null;
			if (userLanguages != null && !userLanguages.isEmpty()) {
				Predicate operatorPredicate = null;
				Expression<Collection<String>> languages = order.get("languages");
				for (String language : userLanguages) {
					Predicate containsLanguages = builder.isMember(language, languages);
					if (operatorPredicate == null) {
						operatorPredicate = containsLanguages;
					} else {
						operatorPredicate = builder.or(containsLanguages, operatorPredicate);
					}
				}
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = operatorPredicate;
				} else {
					mainOperatorPredicate = builder.and(operatorPredicate, mainOperatorPredicate);
				}
			}

			List<Currency> userCurrencies = (profile != null) ? profile.getCurrencies() : null;
			if (userCurrencies != null && !userCurrencies.isEmpty()) {
				Predicate operatorPredicate = null;
				Expression<Currency> currencyExpression = order.get("currency");
				for (Currency currency : userCurrencies) {
					Predicate containsCurrencies = builder.equal(currencyExpression, currency);//builder.isMember(currency, currencies);
					if (operatorPredicate == null) {
						operatorPredicate = containsCurrencies;
					} else {
						operatorPredicate = builder.or(containsCurrencies, operatorPredicate);
					}
				}
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = operatorPredicate;
				} else {
					mainOperatorPredicate = builder.and(operatorPredicate, mainOperatorPredicate);
				}
			}
			if(mainOperatorPredicate != null) {
				criteria.where(mainOperatorPredicate);
			}
			
			if(isLength) {
				return new OrdersData((Long) entityManager.createQuery(criteria).getSingleResult());
			}
			String sortDataField = filterCriteriaValue.getSortDataField();
			if (sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.desc(order.get(sortDataField)), builder.asc(order.get("status")));
			} else if (!sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.asc(order.get(sortDataField)), builder.asc(order.get("status")));
			} else {
				criteria.orderBy(builder.asc(order.get("status")));
			}

			TypedQuery<OrderInfo> query = entityManager.createQuery(criteria);
			query.setFirstResult(filterCriteriaValue.getPageNumber() * filterCriteriaValue.getPageSize());
			query.setMaxResults(filterCriteriaValue.getPageSize());
			List<OrderInfo> orders = query.getResultList();
			
			return new OrdersData(orders);
		} finally {
			entityManager.close();
		}
	}

}
