package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.order.model.CategoryType;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.OrderCategory;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.ws.rs.WebApplicationException;

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
				List<OrderCategory> orderCategories = orderInfo.getCategories();
				if (orderCategories == null) {
					orderInfo.setCategories(new ArrayList<OrderCategory>());
				}
				for (OrderCategory category : orderCategories) {
					category.setCustom(true);
				}
				orderInfo.setStatus(OrderStatus.OPENED);
				orderInfo.setCreationDate(new Date());
				entityManager.persist(orderInfo);
				return orderInfo;
			}
		});
	}

	public OrderInfo update(final OrderInfo newOrder) {
		if (newOrder == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderInfo>() {

			@Override
			public OrderInfo
					call(EntityManager entityManager) {
				OrderInfo order = entityManager.find(OrderInfo.class, newOrder.getId(), LockModeType.PESSIMISTIC_WRITE);
				if (order == null) {
					return null;
				}
				List<OrderCategory> categories = findAllCategories();
				List<OrderCategory> orderCategories = order.getCategories();
				if (orderCategories == null) {
					order.setCategories(new ArrayList<OrderCategory>());
				}
				order.setDuration(newOrder.getDuration());
				order.setDurationType(newOrder.getDurationType());
				order.setEndDate(newOrder.getEndDate());
				order.setGivingCurrency(newOrder.getGivingCurrency());
				order.setGivingValue(newOrder.getGivingValue());
				order.setLanguages(newOrder.getLanguages());
				order.setOrderData(newOrder.getOrderData());
				if (order.getResponses() == null) {
					order.setResponses(new ArrayList<Respond>());
				}
				for (Respond r : newOrder.getResponses()) {
					if (!order.getResponses().contains(r)) {
						order.getResponses().add(r);
					}
				}
				order.setStatus(newOrder.getStatus());
				order.setTakingCurrency(newOrder.getTakingCurrency());
				order.setTakingValue(newOrder.getTakingValue());
				order.setUserPublicKey(newOrder.getUserPublicKey());
				order.setComment(newOrder.getComment());
				if (newOrder.getApprovedRespondId() != null) {
					order.setApprovedRespondId(newOrder.getApprovedRespondId());
				}
				return order;
			}
		});
	}

	public Respond findResponse(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					Respond.class, id, null);
		} finally {
			entityManager.close();
		}
	}

	public OrderCategory createCategory(final String title, final CategoryType type) {
		if ((title == null) || title.isEmpty()) {
			throw new IllegalArgumentException("Title is empty.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderCategory>() {

			@Override
			public OrderCategory call(EntityManager entityManager) {

				OrderCategory category = new OrderCategory(title, type);
				category.setCustom(false);
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
			criteria.where(builder.equal(category.get("custom"), "f"));
			TypedQuery<OrderCategory> query = entityManager.createQuery(criteria);
			List<OrderCategory> c = query.getResultList();
			return c;
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
			criteria = formCriteria(criteria, builder, order, select, type, userPublicKey, filterCriteriaValue, profile, entityManager);

			String sortDataField = filterCriteriaValue.getSortDataField();
			if (sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				if ("partnersRating".equals(sortDataField)) {
					criteria.orderBy(builder.desc(order.get(sortDataField)), builder.asc(order.get("status")));
				}
				if ("rating".equals(sortDataField)) {
					criteria.orderBy(builder.desc(order.get(sortDataField)), builder.asc(order.get("status")));
				} else {
					criteria.orderBy(builder.desc(order.get(sortDataField)), builder.asc(order.get("status")));
				}
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
			criteria = formCriteria(criteria, builder, order, select, type, userPublicKey, filterCriteriaValue, profile, entityManager);

			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	private CriteriaQuery formCriteria(CriteriaQuery criteria, CriteriaBuilder builder, Root<OrderInfo> order, CriteriaQuery<Object> select,
			EntityType<OrderInfo> type, String userPublicKey, SearchRequest filterCriteriaValue, UserPublicProfile profile, EntityManager entityManager) {
		Predicate mainOperatorPredicate = null;
		if ((userPublicKey != null) && !userPublicKey.isEmpty()) {
			Expression<String> typeExpression = order.get("userPublicKey");
			mainOperatorPredicate = builder.equal(typeExpression, userPublicKey);
		}

		List<FilterItem> filterItems = (filterCriteriaValue != null) ? filterCriteriaValue.getFilterItems() : null;

		if (filterItems != null && !filterItems.isEmpty()) {

			Predicate itemsOperatorPredicate = null;
			Predicate languagesPredicate = null;
			Predicate categoriesPredicate = null;
			Predicate statusesPredicate = null;
			Predicate takingCurrencyPredicate = null;
			Predicate givingCurrencyPredicate = null;

			/*for (FilterItem item : filterItems) {
				if ((item.getFilterDataField() != null) && (item.getFilterValue() != null)) {
					Predicate valuePredicate = null;
					if ((item.getFilterCondition() == null) || (FilterCondition.EQUAL == item.getFilterCondition())) {
						if ("status".equals(item.getFilterDataField())) {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), OrderStatus.valueOf(item.getFilterValue()));
							if (statusesPredicate == null) {
								statusesPredicate = valuePredicate;
							} else {
								statusesPredicate = builder.or(valuePredicate, statusesPredicate);
							}
						} else if ("takingCurrency".equals(item.getFilterDataField())) {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), Currency.valueOf(item.getFilterValue()));
							if (takingCurrencyPredicate == null) {
								takingCurrencyPredicate = valuePredicate;
							} else {
								takingCurrencyPredicate = builder.or(valuePredicate, takingCurrencyPredicate);
							}
						} else if ("givingCurrency".equals(item.getFilterDataField())) {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), Currency.valueOf(item.getFilterValue()));
							if (givingCurrencyPredicate == null) {
								givingCurrencyPredicate = valuePredicate;
							} else {
								givingCurrencyPredicate = builder.or(valuePredicate, givingCurrencyPredicate);
							}
						} else if ("partnersRating".equals(item.getFilterDataField()) || "success".equals(item.getFilterDataField())) {
							Path<Integer> field = order.get(item.getFilterDataField());
							valuePredicate = builder.equal(field, Integer.valueOf(item.getFilterValue()));
						} else if ("languages".equals(item.getFilterDataField())) {
							Expression<Collection<String>> languages = order.get("languages").get("title");
							String language = item.getFilterValue();
							Predicate containsLanguages = builder.isMember(language, languages);

							if (languagesPredicate == null) {
								languagesPredicate = containsLanguages;
							} else {
								languagesPredicate = builder.or(containsLanguages, languagesPredicate);
							}

						} else if ("categories".equals(item.getFilterDataField())) {
							Expression<Collection<String>> categories = order.get("categories").get("title");
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
					} else if (FilterCondition.LESS_THAN_OR_EQUAL == item.getFilterCondition()) {
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {

							valuePredicate = builder.lessThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));

						} else {
							valuePredicate = builder.lessThanOrEqualTo(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.LESS_THAN == item.getFilterCondition()) {
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {

							valuePredicate = builder.lessThan(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));

						} else {
							valuePredicate = builder.lessThan(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.GREATER_THAN_OR_EQUAL == item.getFilterCondition()) {
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {
							valuePredicate = builder.greaterThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));
						} else if ("summaryRating".equals(item.getFilterDataField()) || "opennessRating".equals(item.getFilterDataField())) {
							Subquery<Statistics> subquery = criteria.subquery(Statistics.class);
							Root fromStatistics = subquery.from(Statistics.class);
							subquery.select(fromStatistics.get("id"));
							subquery.where(builder.greaterThanOrEqualTo(fromStatistics.get(item.getFilterDataField()), item.getFilterValue()));
							valuePredicate = builder.in(order.get("userPublicKey")).value(subquery);
						} else if ("responsesCount".equals(item.getFilterDataField())) {
							TypedQuery<String> query = entityManager.createQuery("SELECT o.id FROM OrderInfo o JOIN o.responses r GROUP BY o.id HAVING count(r) >= :count AND o.userPublicKey = :publicKey", String.class);
							query.setParameter("count", Long.parseLong(item.getFilterValue()));
							query.setParameter("publicKey", userPublicKey);
							List<String> ordersId = query.getResultList();

							if ((ordersId == null) || ordersId.isEmpty()) {
								ordersId.add("");
							}
							valuePredicate = order.get("id").in(ordersId);
						} else if ("partnersRating".equals(item.getFilterDataField())) {

							TypedQuery<String> query = entityManager.createNamedQuery("OrderInfo.findByPartnersRating", String.class);
							query.setParameter("status", OrderStatus.SUCCESS);
							query.setParameter("rating", Long.parseLong(item.getFilterValue()));
							List<String> publicKeys = query.getResultList();

							if ((publicKeys == null) || publicKeys.isEmpty()) {
								publicKeys.add("");
							}
							valuePredicate = order.get("userPublicKey").in(publicKeys);
						} else {
							valuePredicate = builder.greaterThanOrEqualTo(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.GREATER_THAN == item.getFilterCondition()) {
						if ("endDate".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {
							valuePredicate = builder.greaterThan(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));
						} else {
							valuePredicate = builder.greaterThan(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					}
					if (itemsOperatorPredicate == null) {
						itemsOperatorPredicate = valuePredicate;
					} else if ((item.getFilterOperator() == null) || (FilterOperator.AND == item.getFilterOperator() && !"categories".equals(item.getFilterDataField()) && !"languages".equals(item.getFilterDataField()))) {
						itemsOperatorPredicate = builder.and(valuePredicate, itemsOperatorPredicate);
					} else if (FilterOperator.OR == item.getFilterOperator()) {
						itemsOperatorPredicate = builder.or(valuePredicate, itemsOperatorPredicate);
					}
				}
			}*/
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
			if (statusesPredicate != null) {
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = statusesPredicate;
				} else {
					mainOperatorPredicate = builder.and(statusesPredicate, mainOperatorPredicate);
				}
			}
			if (takingCurrencyPredicate != null) {
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = takingCurrencyPredicate;
				} else {
					mainOperatorPredicate = builder.and(takingCurrencyPredicate, mainOperatorPredicate);
				}
			}
			if (givingCurrencyPredicate != null) {
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = givingCurrencyPredicate;
				} else {
					mainOperatorPredicate = builder.and(givingCurrencyPredicate, mainOperatorPredicate);
				}
			}

			if (mainOperatorPredicate == null) {
				mainOperatorPredicate = itemsOperatorPredicate;
			} else if (itemsOperatorPredicate != null) {
				mainOperatorPredicate = builder.and(itemsOperatorPredicate, mainOperatorPredicate);
			}
		}
		List<Language> userLanguages = (profile != null) ? profile.getLanguages() : null;
		if (userLanguages != null && !userLanguages.isEmpty()) {
			Predicate operatorPredicate = null;
			Expression<Collection<String>> languages = order.get("languages").get("title");
			for (Language language : userLanguages) {
				Predicate containsLanguages = builder.isMember(language.getTitle(), languages);
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

	public long getPartnersRating(String userPublicKey) {
		if ((userPublicKey == null) || userPublicKey.isEmpty()) {
			throw new WebApplicationException();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria;
			criteria = builder.createQuery(Long.class);
			Root<Statistics> statistics = criteria.from(Statistics.class);

			Expression<Integer> ex = statistics.get("summaryRating");
			criteria.select(builder.sumAsLong(ex));

			Subquery<String> usersIdSubquery = criteria.subquery(String.class);
			Root fromResponse = criteria.from(Respond.class);
			usersIdSubquery.select(fromResponse.get("userPublicKey")).distinct(true);

			Subquery<String> subquery = usersIdSubquery.subquery(String.class);
			Root fromOrderInfo = subquery.from(OrderInfo.class);
			subquery.select(fromOrderInfo.get("approvedResponseId"));

			Predicate p1 = builder.and(builder.equal(fromOrderInfo.get("userPublicKey"), userPublicKey), builder.equal(fromOrderInfo.get("status"), OrderStatus.SUCCESS));
			subquery.where(p1);

			Predicate p2 = builder.in(fromResponse.get("id")).value(subquery);
			usersIdSubquery.where(p2);

			Predicate p3 = builder.and(builder.in(statistics.get("id")).value(usersIdSubquery), builder.notEqual(statistics.get("id"), userPublicKey));
			criteria.where(p3);
			Long result = entityManager.createQuery(criteria).getSingleResult();
			return (result == null) ? 0 : result;
		} finally {
			entityManager.close();
		}
	}

	public long getMaxSummaryRating() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria;
			criteria = builder.createQuery(Long.class);
			Root<Statistics> statistics = criteria.from(Statistics.class);

			Expression<Long> ex = statistics.get("summaryRating");
			criteria.select(builder.max(ex));
			Long result = entityManager.createQuery(criteria).getSingleResult();
			return (result == null) ? 0 : result;
		} finally {
			entityManager.close();
		}
	}
}
