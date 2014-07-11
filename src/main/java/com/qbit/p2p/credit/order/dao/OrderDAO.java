package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.util.DateUtil;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.OrderCategory;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.OrderType;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
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
import javax.persistence.criteria.Path;
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
		if ((orderInfo == null) || !orderInfo.isValid()) {
			throw new IllegalArgumentException("Order is null or not valid.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderInfo>() {

			@Override
			public OrderInfo call(EntityManager entityManager) {
				UserInfo userInfo = UserDAO.findAndLock(entityManager, orderInfo.getUserPublicKey());
				if (userInfo == null) {
					return null;
				}
				orderInfo.setStatus(OrderStatus.SUCCESS);
				orderInfo.setCreationDate(new Date());
				entityManager.persist(orderInfo);
				return orderInfo;
			}
		});
	}

	public OrderCategory createCategory(final String title) {
		if ((title == null) || title.isEmpty()) {
			throw new IllegalArgumentException("Title is empty.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderCategory>() {

			@Override
			public OrderCategory call(EntityManager entityManager) {

				OrderCategory category = new OrderCategory(title);
				entityManager.persist(category);
				return category;
			}
		});
	}

	public List<OrderCategory> findAllCategories() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<OrderCategory> criteria = builder.createQuery(OrderCategory.class);
			Root<OrderCategory> category = criteria.from(OrderCategory.class);
			criteria.select(category);
			TypedQuery<OrderCategory> query = entityManager.createQuery(criteria);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}

	public List<OrderInfo> findWithFilter(String userPublicKey, SearchRequest filterCriteriaValue, UserPublicProfile profile) {
		boolean sortDesc = false;
		if ((filterCriteriaValue != null) && filterCriteriaValue.getSortOrder() != null && filterCriteriaValue.getSortOrder().equals("desc")) {
			sortDesc = true;
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria;
			EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);

			criteria = builder.createQuery(OrderInfo.class);
			Root<OrderInfo> order = criteria.from(OrderInfo.class);
			CriteriaQuery<Object> select = criteria.select(order).distinct(true);
			criteria = formCriteria(criteria, builder, order, select, type, userPublicKey, filterCriteriaValue, profile);

			String sortDataField = filterCriteriaValue.getSortDataField();
			if (sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.desc(order.get(sortDataField)), builder.asc(order.get("status")));
			} else if (!sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.asc(order.get(sortDataField)), builder.asc(order.get("status")));
			} else {
				if ((userPublicKey != null) && !userPublicKey.isEmpty()) {
					criteria.orderBy(builder.asc(order.get("status")), builder.desc(order.get("creationDate")));
				} else {
					criteria.orderBy(builder.asc(order.get("status")), builder.desc(order.get("creationDate")));
				}
			}
			TypedQuery<OrderInfo> query = entityManager.createQuery(criteria);
			query.setFirstResult(filterCriteriaValue.getPageNumber() * filterCriteriaValue.getPageSize());
			query.setMaxResults(filterCriteriaValue.getPageSize());
			List<OrderInfo> orders = query.getResultList();
			//System.out.println("@@@ " + orders);
			return orders;
		} finally {
			entityManager.close();
		}
	}

	public long getLengthWithFilter(String userPublicKey, SearchRequest filterCriteriaValue, UserPublicProfile profile) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria;
			EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);

			criteria = builder.createQuery(Long.class);

			Root<OrderInfo> order = criteria.from(OrderInfo.class);

			CriteriaQuery<Object> select = criteria.select(builder.countDistinct(order));
			criteria = formCriteria(criteria, builder, order, select, type, userPublicKey, filterCriteriaValue, profile);

			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	private CriteriaQuery formCriteria(CriteriaQuery criteria, CriteriaBuilder builder, Root<OrderInfo> order, CriteriaQuery<Object> select,
		EntityType<OrderInfo> type, String userPublicKey, SearchRequest filterCriteriaValue, UserPublicProfile profile) {
		Predicate mainOperatorPredicate = null;
		if ((userPublicKey != null) && !userPublicKey.isEmpty()) {
			Expression<String> typeExpression = order.get("userPublicKey");
			mainOperatorPredicate = builder.equal(typeExpression, userPublicKey);
		}
		/*if ((filterCriteriaValue != null) && (filterCriteriaValue.getOrderType() != null)) {
		 Expression<OrderType> typeExpression = order.get("type");
		 Predicate typePredicate = builder.equal(typeExpression, filterCriteriaValue.getOrderType());

		 if (mainOperatorPredicate == null) {
		 mainOperatorPredicate = typePredicate;
		 } else {
		 mainOperatorPredicate = builder.and(typePredicate, mainOperatorPredicate);
		 }
		 }*/

		List<FilterItem> filterItems = (filterCriteriaValue != null) ? filterCriteriaValue.getFilterItems() : null;

		if (filterItems != null && !filterItems.isEmpty()) {

			Predicate itemsOperatorPredicate = null;
			Predicate languagesPredicate = null;
			Predicate categoriesPredicate = null;
			for (FilterItem item : filterItems) {
				if ((item.getFilterDataField() != null) && (item.getFilterValue() != null)) {
					//ParameterExpression<String> parameter = builder.parameter(String.class, item.getFilterDataField());
					//Predicate valuePredicate = builder.equal(builder.lower(order.<String>get(item.getFilterDataField())), "%" + item.getFilterValue().toLowerCase() + "%");
					Predicate valuePredicate = null;
					System.out.println("!!!@@!!! " + item.getFilterCondition() + " " + item.getFilterDataField() + " " + item.getFilterValue());
					if ((item.getFilterCondition() == null) || (FilterCondition.EQUAL == item.getFilterCondition())) {
						if ("status".equals(item.getFilterDataField())) {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), OrderStatus.valueOf(item.getFilterValue()));
						} else if ("givingCurrency".equals(item.getFilterDataField())) {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), Currency.valueOf(item.getFilterValue()));
						} else if ("partnersRating".equals(item.getFilterDataField()) || "success".equals(item.getFilterDataField())) {
							Path<Integer> field = order.get(item.getFilterDataField());
							valuePredicate = builder.equal(field, Integer.valueOf(item.getFilterValue()));
						} else if ("languages".equals(item.getFilterDataField())) {
							Expression<Collection<String>> languages = order.get("languages");
							String language = item.getFilterValue();
							Predicate containsLanguages = builder.isMember(language, languages);

							if (languagesPredicate == null) {
								languagesPredicate = containsLanguages;
							} else {
								languagesPredicate = builder.or(containsLanguages, languagesPredicate);
							}

						} else if ("categories".equals(item.getFilterDataField())) {
							Expression<Collection<String>> categories = order.get("categories");
							String category = item.getFilterValue();
							Predicate containsCategories = builder.isMember(category, categories);

							if (categoriesPredicate == null) {
								categoriesPredicate = containsCategories;
							} else {
								categoriesPredicate = builder.or(containsCategories, categoriesPredicate);
							}
						} else {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.NOT_EQUAL == item.getFilterCondition()) {
						//Expression<OrderType> typeExpression = order.get("type");
						if ("status".equals(item.getFilterDataField())) {
							valuePredicate = builder.notEqual(order.get(item.getFilterDataField()), OrderStatus.valueOf(item.getFilterValue()));
						} else {
							valuePredicate = builder.notEqual(order.get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.STARTS_WITH == item.getFilterCondition()) {

						valuePredicate = builder.like(
							builder.lower(
								order.get(
									type.getDeclaredSingularAttribute(item.getFilterDataField(), String.class)
								)
							), "%" + item.getFilterValue().toLowerCase() + "%"
						);
					} else if (FilterCondition.LE == item.getFilterCondition()) {

						valuePredicate = builder.like(
							builder.lower(
								order.get(
									type.getDeclaredSingularAttribute(item.getFilterDataField(), String.class)
								)
							), "%" + item.getFilterValue().toLowerCase() + "%"
						);
					} else if (FilterCondition.LESS_THAN_OR_EQUAL == item.getFilterCondition()) {
						//continue;
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {

							valuePredicate = builder.lessThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));

						} else {
							valuePredicate = builder.lessThanOrEqualTo(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.LESS_THAN == item.getFilterCondition()) {
						//continue;
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {

							valuePredicate = builder.lessThan(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));

						} else {
							valuePredicate = builder.lessThan(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.GREATER_THAN_OR_EQUAL == item.getFilterCondition()) {
						//continue;
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {
							System.out.println("%%% " + DateUtil.stringToDate(item.getFilterValue()));
							valuePredicate = builder.greaterThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));
						} else if ("summaryRating".equals(item.getFilterDataField()) || "opennessRating".equals(item.getFilterDataField())) {
							Subquery<UserPublicProfile> subquery = criteria.subquery(UserPublicProfile.class);
							Root fromUserPublicProfile = subquery.from(UserPublicProfile.class);
							subquery.select(fromUserPublicProfile.get("publicKey"));
							//subquery.where(builder.equal(fromUserPublicProfile.get("name"), "gg"));
							subquery.where(builder.greaterThanOrEqualTo(fromUserPublicProfile.join("statistic").get(item.getFilterDataField()), item.getFilterValue()));
							valuePredicate = builder.in(order.get("userPublicKey")).value(subquery);
							//subquery.where(builder.ge(fromUserPublicProfile.get("pint"),30000));
							//valuePredicate = builder.greaterThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));
						} else {
							valuePredicate = builder.greaterThanOrEqualTo(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.GREATER_THAN == item.getFilterCondition()) {
						//continue;
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {
							System.out.println("%%% " + DateUtil.stringToDate(item.getFilterValue()));
							valuePredicate = builder.greaterThan(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));
						} else {
							valuePredicate = builder.greaterThan(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
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
			if (languagesPredicate != null) {
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = languagesPredicate;
				} else {
					mainOperatorPredicate = builder.and(languagesPredicate, mainOperatorPredicate);
				}
			}
			if (categoriesPredicate != null) {
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = categoriesPredicate;
				} else {
					mainOperatorPredicate = builder.and(categoriesPredicate, mainOperatorPredicate);
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
			Expression<Currency> givingCurrencyExpression = order.get("givingCurrency");
			for (Currency currency : userCurrencies) {
				Predicate containsGivingCurrencies = builder.equal(givingCurrencyExpression, currency);//builder.isMember(currency, currencies);
				if (operatorPredicate == null) {
					operatorPredicate = containsGivingCurrencies;
				} else {
					operatorPredicate = builder.or(containsGivingCurrencies, operatorPredicate);
				}
			}
			Predicate takingOperatorPredicate = null;
			Expression<Currency> takingCurrencyExpression = order.get("takingCurrency");
			for (Currency currency : userCurrencies) {
				Predicate containsTakingCurrencies = builder.equal(takingCurrencyExpression, currency);
				if (takingOperatorPredicate == null) {
					takingOperatorPredicate = containsTakingCurrencies;
				} else {
					takingOperatorPredicate = builder.or(containsTakingCurrencies, takingOperatorPredicate);
				}
			}
			operatorPredicate = builder.or(operatorPredicate, takingOperatorPredicate);
			if (mainOperatorPredicate == null) {
				mainOperatorPredicate = operatorPredicate;
			} else {
				mainOperatorPredicate = builder.and(operatorPredicate, mainOperatorPredicate);
			}
		}
		if (mainOperatorPredicate != null) {
			criteria.where(mainOperatorPredicate);
		}
		return criteria;
	}

}
