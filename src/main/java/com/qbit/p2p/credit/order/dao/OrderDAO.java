package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.Category;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.order.model.SortOrder;
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
import javax.persistence.criteria.CriteriaUpdate;
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
	private Env env;
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

	public OrderInfo create(final OrderInfo orderInfo) {
		if ((orderInfo == null) || !orderInfo.isValid()) {
			throw new IllegalArgumentException("Order is null or not valid.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderInfo>() {

			@Override
			public OrderInfo call(EntityManager entityManager) {
				UserInfo userInfo = UserDAO.findAndLock(entityManager, orderInfo.getUserId());
				if (userInfo == null) {
					return null;
				}
				orderInfo.setStatus(OrderStatus.OPENED);
				orderInfo.setCreationDate(new Date());
				entityManager.merge(orderInfo);
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
				List<Category> orderCategories = order.getCategories();
				if (orderCategories == null) {
					order.setCategories(new ArrayList<Category>());
				}
				order.setDuration(newOrder.getDuration());
				order.setDurationType(newOrder.getDurationType());
				order.setBookingDeadline(newOrder.getBookingDeadline());
				order.setOutcomingCurrency(newOrder.getOutcomingCurrency());
				order.setOutcomingAmout(newOrder.getOutcomingAmout());
				order.setLanguages(newOrder.getLanguages());
				order.setOrderData(newOrder.getOrderData());
				order.setResponses(newOrder.getResponses());
				order.setStatus(newOrder.getStatus());
				order.setIncomingCurrency(newOrder.getIncomingCurrency());
				order.setIncomingAmount(newOrder.getIncomingAmount());
				order.setUserId(newOrder.getUserId());
				order.setComment(newOrder.getComment());
				order.setApprovedUserId(newOrder.getApprovedUserId());
				return order;
			}
		});
	}

	public OrderInfo addRespond(final Respond respond, final String orderId) {
		if (respond == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderInfo>() {

			@Override
			public OrderInfo
				call(EntityManager entityManager) {
				OrderInfo order = entityManager.find(OrderInfo.class, orderId, LockModeType.PESSIMISTIC_WRITE);
				if ((order == null) || order.getUserId().equals(respond.getUserId())) {
					return null;
				}
				if (order.getResponses() != null) {
					for (Respond orderResponse : order.getResponses()) {
						if (orderResponse.getUserId().equals(respond.getUserId())) {
							return null;
						}
					}
				} else {
					order.setResponses(new ArrayList<Respond>());
				}
				order.getResponses().add(respond);
				entityManager.merge(order);
				return order;
			}
		});
	}

	public Respond findRespond(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
				Respond.class, id, null);
		} finally {
			entityManager.close();
		}
	}

	public List<OrderInfo> findWithFilter(String userId, SearchRequest searchRequest) {

		boolean sortDesc = false;
		if ((searchRequest != null) && searchRequest.getSortOrder() != null && searchRequest.getSortOrder() == SortOrder.DESC) {
			sortDesc = true;
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria;
			EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);

			criteria = builder.createQuery(OrderInfo.class);
			Root<OrderInfo> order = criteria.from(OrderInfo.class);
			criteria.select(order).distinct(true);
			criteria = formCriteria(userId, criteria, builder, order, type, searchRequest, entityManager);

			String sortDataField = searchRequest.getSortDataField();
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
				if ((userId != null) && !userId.isEmpty()) {
					criteria.orderBy(builder.asc(order.get("status")), builder.desc(order.get("creationDate")));
				} else {
					criteria.orderBy(builder.asc(order.get("status")), builder.desc(order.get("creationDate")));
				}
			}
			TypedQuery<OrderInfo> query = entityManager.createQuery(criteria);
			query.setFirstResult(searchRequest.getPageNumber() * searchRequest.getPageSize());
			query.setMaxResults(searchRequest.getPageSize());
			List<OrderInfo> orders = query.getResultList();
			return orders;
		} finally {
			entityManager.close();
		}
	}

	public long lengthWithFilter(SearchRequest filterCriteriaValue) {
		return lengthWithFilter(null, filterCriteriaValue);
	}

	public long lengthWithFilter(String userId, SearchRequest searchRequest) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria;
			EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);

			criteria = builder.createQuery(Long.class);

			Root<OrderInfo> order = criteria.from(OrderInfo.class);

			criteria.select(builder.countDistinct(order));
			criteria = formCriteria(userId, criteria, builder, order, type, searchRequest, entityManager);

			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	private CriteriaQuery formCriteria(String userId, CriteriaQuery criteria, CriteriaBuilder builder, Root<OrderInfo> order,
		EntityType<OrderInfo> type, SearchRequest searchRequest, EntityManager entityManager) {
		Predicate mainOperatorPredicate = null;
		if ((searchRequest != null) && (searchRequest.getFilterItems() == null)) {
			searchRequest.setFilterItems(new ArrayList<FilterItem>());
		}

		List<FilterItem> filterItems = (searchRequest != null) ? searchRequest.getFilterItems() : null;

		if (filterItems != null && !filterItems.isEmpty()) {

			Predicate itemsOperatorPredicate = null;
			Predicate languagesPredicate = null;
			Predicate categoriesPredicate = null;
			Predicate statusesPredicate = null;
			Predicate takingCurrencyPredicate = null;
			Predicate givingCurrencyPredicate = null;

			for (FilterItem item : filterItems) {
				if ((item.getFilterDataField() != null) && (item.getFilterValue() != null)) {
					Predicate valuePredicate = null;
					if ((item.getFilterCondition() == null) || (FilterCondition.EQUAL == item.getFilterCondition())) {
						if ("userId".equals(item.getFilterDataField()) && "CURRENT".equals(item.getFilterValue())) {
							if ((userId != null) && !userId.isEmpty() && !userId.contains("@")) {
								Expression<String> typeExpression = order.get("userId");
								valuePredicate = builder.equal(typeExpression, userId);
								if (mainOperatorPredicate == null) {
									mainOperatorPredicate = valuePredicate;
								} else {
									mainOperatorPredicate = builder.and(valuePredicate, mainOperatorPredicate);
								}
							}
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), OrderStatus.valueOf(item.getFilterValue()));
							if (statusesPredicate == null) {
								statusesPredicate = valuePredicate;
							} else {
								statusesPredicate = builder.or(valuePredicate, statusesPredicate);
							}
						}
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
						if ("bookingDeadline".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {

							valuePredicate = builder.lessThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));

						} else {
							valuePredicate = builder.lessThanOrEqualTo(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.LESS_THAN == item.getFilterCondition()) {
						if ("bookingDeadline".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {

							valuePredicate = builder.lessThan(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));

						} else {
							valuePredicate = builder.lessThan(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.GREATER_THAN_OR_EQUAL == item.getFilterCondition()) {
						if ("bookingDeadline".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {
							valuePredicate = builder.greaterThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));
						} else if ("summaryRating".equals(item.getFilterDataField()) || "opennessRating".equals(item.getFilterDataField())) {
							Subquery<Statistics> subquery = criteria.subquery(Statistics.class);
							Root fromStatistics = subquery.from(Statistics.class);
							subquery.select(fromStatistics.get("id"));
							subquery.where(builder.greaterThanOrEqualTo(fromStatistics.get(item.getFilterDataField()), item.getFilterValue()));
							valuePredicate = builder.in(order.get("userPublicKey")).value(subquery);
						} else if ("responsesCount".equals(item.getFilterDataField())) {
							TypedQuery<String> query = entityManager.createQuery("SELECT o.id FROM OrderInfo o JOIN o.responses r GROUP BY o.id HAVING count(r) >= :count AND o.userId = :userId", String.class);
							query.setParameter("count", Long.parseLong(item.getFilterValue()));
							query.setParameter("userId", userId);
							List<String> ordersId = query.getResultList();

							if ((ordersId == null) || ordersId.isEmpty()) {
								ordersId.add("");
							}
							valuePredicate = order.get("id").in(ordersId);
						} else if ("partnersRating".equals(item.getFilterDataField())) {

							TypedQuery<String> query = entityManager.createNamedQuery("OrderInfo.findByPartnersRating", String.class);
							query.setParameter("status", OrderStatus.SUCCESS);
							query.setParameter("rating", Long.parseLong(item.getFilterValue()));
							query.setParameter("openessFactor", env.getUserRatingOpenessFactor());
							query.setParameter("transactionsFactor", env.getUserRatingTransactionsFactor());
							List<String> publicKeys = query.getResultList();

							if ((publicKeys == null) || publicKeys.isEmpty()) {
								publicKeys.add("");
							}
							valuePredicate = order.get("userPublicKey").in(publicKeys);
						} else {
							valuePredicate = builder.greaterThanOrEqualTo(order.<String>get(item.getFilterDataField()), item.getFilterValue());
						}
					} else if (FilterCondition.GREATER_THAN == item.getFilterCondition()) {
						if ("bookingDeadline".toLowerCase().equals(item.getFilterDataField().toLowerCase())) {
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
		UserPublicProfile profile = profileDAO.find(userId);
		List<Language> userLanguages = (profile != null) ? profile.getLanguages() : null;
		if (userLanguages != null && !userLanguages.isEmpty()) {
			Predicate operatorPredicate = null;
			Expression<Collection<String>> languages = order.get("languages").get("title");
			for (Language language : userLanguages) {
				Predicate containsLanguages = builder.isMember(language.getCode(), languages);
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

	public int changeStatus(OrderInfo newOrder, String orderId, String userId) {
		OrderInfo order = find(orderId);
		if (!newOrder.getUserId().equals(userId) || !isValidNewOrderStatus(order.getStatus(), newOrder.getStatus())) {
			return 0;
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaUpdate<OrderInfo> update = builder.createCriteriaUpdate(OrderInfo.class);
			Root<OrderInfo> root = update.from(OrderInfo.class);
			if (OrderStatus.IN_PROCESS == newOrder.getStatus()) {
				update.set("approvedUserId", newOrder.getApprovedUserId());
			}
			update.set("status", newOrder.getStatus());
			update.where(builder.equal(root.get("id"), newOrder.getId()));
			int numberOfEntities = entityManager.createQuery(update).executeUpdate();

			CriteriaUpdate<Statistics> updateStatistics = builder.createCriteriaUpdate(Statistics.class);
			Root<Statistics> statisticsRoot = updateStatistics.from(Statistics.class);
			updateStatistics.set("partnersRating", getPartnersRating(newOrder.getUserId()));
			updateStatistics.where(builder.equal(statisticsRoot.get("id"), newOrder.getUserId()));
			entityManager.createQuery(updateStatistics).executeUpdate();
			
			if (OrderStatus.SUCCESS == newOrder.getStatus()) {
				CriteriaUpdate<Statistics> updatePartnersStatistics = builder.createCriteriaUpdate(Statistics.class);
				Root<Statistics> partnersStatisticsRoot = updatePartnersStatistics.from(Statistics.class);
				updatePartnersStatistics.set("partnersRating", getPartnersRating(newOrder.getApprovedUserId()));
				updatePartnersStatistics.where(builder.equal(partnersStatisticsRoot.get("id"), newOrder.getApprovedUserId()));
				entityManager.createQuery(updatePartnersStatistics).executeUpdate();
			}
			return numberOfEntities;
		} finally {
			entityManager.close();
		}
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
			Root fromRespond = criteria.from(Respond.class);
			usersIdSubquery.select(fromRespond.get("userPublicKey")).distinct(true);

			Subquery<String> subquery = usersIdSubquery.subquery(String.class);
			Root fromOrderInfo = subquery.from(OrderInfo.class);
			subquery.select(fromOrderInfo.get("approvedRespondId"));

			Predicate p1 = builder.and(builder.equal(fromOrderInfo.get("userPublicKey"), userPublicKey), builder.equal(fromOrderInfo.get("status"), OrderStatus.SUCCESS));
			subquery.where(p1);

			Predicate p2 = builder.in(fromRespond.get("id")).value(subquery);
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

	private boolean isValidNewOrderStatus(OrderStatus oldStatus, OrderStatus newStatus) {
		if (newStatus == oldStatus) {
			return false;
		}
		if ((OrderStatus.IN_PROCESS == oldStatus) && (OrderStatus.OPENED == newStatus)) {
			return false;
		}
		return !isСompleted(oldStatus);
	}

	private boolean isСompleted(OrderStatus status) {
		return ((status == OrderStatus.SUCCESS)
			|| (status == OrderStatus.NOT_SUCCESS)
			|| (status == OrderStatus.ARBITRATION));
	}
}
