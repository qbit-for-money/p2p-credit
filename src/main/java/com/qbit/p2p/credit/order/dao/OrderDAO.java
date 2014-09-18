package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.env.Env;
import com.qbit.commons.log.model.OperationType;
import com.qbit.commons.log.service.LogScheduler;
import com.qbit.p2p.credit.order.dao.meta.CurrencyValueProvider;
import com.qbit.p2p.credit.order.dao.meta.DateValueProvider;
import com.qbit.p2p.credit.order.dao.meta.IntegerValueProvider;
import com.qbit.p2p.credit.order.dao.meta.OrderStatusArrayValueProvider;
import com.qbit.p2p.credit.order.dao.meta.OrderStatusValueProvider;
import com.qbit.p2p.credit.order.model.SortOrder;
import com.qbit.p2p.credit.order.dao.meta.StringArrayValueProvider;
import com.qbit.p2p.credit.order.dao.meta.StringValueProvider;
import com.qbit.p2p.credit.order.dao.meta.ValueProvider;
import com.qbit.p2p.credit.order.model.Category;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
	private Env env;
	@Inject
	private CategoryDAO categoryDAO;
	@Inject
	private UserProfileDAO userProfileDAO;
	@Inject
	private LogScheduler logScheduler;

	private static final Map<String, ValueProvider> VALUE_PROVIDERS_MAP;
	private static final Map<String, String> EXPRESSION_PROVIDERS_MAP;

	static {
		VALUE_PROVIDERS_MAP = new HashMap<>();
		VALUE_PROVIDERS_MAP.put("status", OrderStatusValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("userId", StringValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("partnerId", StringValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("incomingCurrency", StringValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("outcomingCurrency", StringValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("incomingAmount", StringValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("outcomingAmount", StringValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("partnersRating", IntegerValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("languages", StringArrayValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("categories", StringArrayValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("bookingDeadline", DateValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("summaryRating", IntegerValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("responsesCount", IntegerValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("partnersRating", IntegerValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("statuses", OrderStatusArrayValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("duration", StringValueProvider.INST);
		VALUE_PROVIDERS_MAP.put("bond", StringValueProvider.INST);
		EXPRESSION_PROVIDERS_MAP = new HashMap<>();
		EXPRESSION_PROVIDERS_MAP.put("languages", "code");
		EXPRESSION_PROVIDERS_MAP.put("categories", "code");
	}

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
				if (userProfileDAO.find(userInfo.getPublicKey()) == null) {
					userProfileDAO.create(userInfo.getPublicKey());
				}
				orderInfo.setStatus(OrderStatus.OPENED);
				orderInfo.setCreationDate(new Date());
				List<Category> categories = orderInfo.getCategories();
				if (categories != null) {
					List<Category> notCustomCategories = categoryDAO.findAll();
					if (notCustomCategories != null) {
						for (Category category : categories) {
							if (!notCustomCategories.contains(category)) {
								category.setCustom(true);
							} else {
								category.setCustom(false);
							}
						}
					}
					orderInfo.setCategories(categories);
				}
				OrderInfo mergedOrderInfo = entityManager.merge(orderInfo);
				logScheduler.createLog(OperationType.ORDER_CREATION, 
						mergedOrderInfo.getUserId(), mergedOrderInfo.getId());
				return mergedOrderInfo;
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
				order.setCategories(newOrder.getCategories());
				order.setDuration(newOrder.getDuration());
				order.setDurationType(newOrder.getDurationType());
				order.setBookingDeadline(newOrder.getBookingDeadline());
				order.setOutcomingCurrency(newOrder.getOutcomingCurrency());
				order.setOutcomingAmount(newOrder.getOutcomingAmount());
				order.setLanguages(newOrder.getLanguages());
				order.setOrderData(newOrder.getOrderData());
				order.setResponses(newOrder.getResponses());
				order.setStatus(newOrder.getStatus());
				order.setIncomingCurrency(newOrder.getIncomingCurrency());
				order.setIncomingAmount(newOrder.getIncomingAmount());
				order.setUserId(newOrder.getUserId());
				order.setComment(newOrder.getComment());
				order.setPartnerId(newOrder.getPartnerId());
				return order;
			}
		});
	}

	public List<OrderInfo> findWithFilter(String userId, SearchRequest searchRequest) {
		boolean sortDesc = ((searchRequest != null) && (searchRequest.getSortOrder() == SortOrder.DESC));
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria = builder.createQuery(OrderInfo.class);
			Root<OrderInfo> order = criteria.from(OrderInfo.class);
			criteria.select(order).distinct(true);
			criteria = formCriteria(userId, searchRequest, order, entityManager, criteria, builder);

			String sortDataField = searchRequest.getSortDataField();
			if ((sortDataField != null) && !sortDataField.isEmpty()) {
				if("AMOUNT".equals(sortDataField.toUpperCase())) {
					criteria.orderBy(builder.desc(order.get("incomingAmount")), builder.desc(order.get("outcomingAmount")), builder.asc(order.get("status")));
				} else if (sortDesc) {
					criteria.orderBy(builder.desc(order.get(sortDataField)), builder.asc(order.get("status")));
				} else {
					criteria.orderBy(builder.asc(order.get(sortDataField)), builder.asc(order.get("status")));
				}
			} else {
				criteria.orderBy(builder.asc(order.get("status")), builder.desc(order.get("creationDate")));
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

			criteria = builder.createQuery(Long.class);

			Root<OrderInfo> order = criteria.from(OrderInfo.class);

			criteria.select(builder.countDistinct(order));
			criteria = formCriteria(userId, searchRequest, order, entityManager, criteria, builder);
			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	private CriteriaQuery formCriteria(String userId, SearchRequest searchRequest, Root<OrderInfo> order, EntityManager entityManager, CriteriaQuery criteria, CriteriaBuilder builder) {
		if ((searchRequest == null) || (searchRequest.getFilterItems() == null)) {
			return criteria;
		}
		Predicate prevPredicate = null;
		for (FilterItem item : searchRequest.getFilterItems()) {
			if ((item.getFilterDataField() == null) || (item.getFilterValue() == null)) {
				continue;
			}

			Predicate predicate = null;
			Expression<?> expression = order.get(item.getFilterDataField());
			ValueProvider valueProvider = VALUE_PROVIDERS_MAP.get(item.getFilterDataField());
			String pathExpression = EXPRESSION_PROVIDERS_MAP.get(item.getFilterDataField());
			switch (item.getFilterCondition()) {
				case IS_MEMBER:
					Expression<Collection> itemsExpression;
					if ((pathExpression != null) && !pathExpression.isEmpty()) {
						itemsExpression = order.get(item.getFilterDataField()).get(pathExpression);
					} else {
						itemsExpression = order.get(item.getFilterDataField());
					}
					Object value = valueProvider.get(item.getFilterValue());
					if (value instanceof Object[]) {
						Predicate containsArray = null;
						for (Object itemValue : (Object[]) value) {
							Predicate containsItems = builder.isMember(itemValue, itemsExpression);
							if (containsArray == null) {
								containsArray = containsItems;
							} else {
								containsArray = builder.or(containsItems, containsArray);
							}
						}
						predicate = containsArray;
					}
					break;
				default:
				case EQUAL:
					if ("userId".equals(item.getFilterDataField()) && "CURRENT".equals(item.getFilterValue())) {
						if ((userId != null) && !userId.isEmpty() && userId.contains("@")) {
							Expression<String> userIdExpression = order.get("userId");
							predicate = builder.equal(userIdExpression, userId);
						}
					} else {
						predicate = builder.equal(expression, valueProvider.get(item.getFilterValue()));
					}
					break;
				case NOT_EQUAL:
					predicate = builder.notEqual(expression, valueProvider.get(item.getFilterValue()));
					break;
				case STARTS_WITH:
					EntityType<OrderInfo> type = entityManager.getMetamodel().entity(OrderInfo.class);
					predicate = builder.like(
							builder.lower(order.get(
											type.getDeclaredSingularAttribute(item.getFilterDataField(), String.class))),
							item.getFilterValue().toLowerCase() + "%");
					break;
				case LESS_THAN_OR_EQUAL:
					predicate = builder.lessThanOrEqualTo((Expression<Comparable>) expression,
							(Comparable) valueProvider.get(item.getFilterValue()));
					break;
				case LESS_THAN:
					predicate = builder.lessThan((Expression<Comparable>) expression,
							(Comparable) valueProvider.get(item.getFilterValue()));
					break;
				case GREATER_THAN_OR_EQUAL:
					if ("summaryRating".equals(item.getFilterDataField())) {
						predicate = getSummaryRatingPredicate((Integer) valueProvider.get(item.getFilterValue()),
								criteria, builder);
					} else if ("opennessRating".equals(item.getFilterDataField())) {
						predicate = getOpenessRatingPredicate((Integer) valueProvider.get(item.getFilterValue()),
								criteria, builder);
					} else if ("partnersRating".equals(item.getFilterDataField())) {
						predicate = getPartnersRatingPredicate((Integer) valueProvider.get(item.getFilterValue()),
								criteria, entityManager);
					} else {
						predicate = builder.greaterThanOrEqualTo((Expression<Comparable>) expression,
								(Comparable) valueProvider.get(item.getFilterValue()));
					}
					break;
				case GREATER_THAN:
					predicate = builder.greaterThan((Expression<Comparable>) expression,
							(Comparable) valueProvider.get(item.getFilterValue()));
					break;
			}
			if ((prevPredicate != null) && (predicate != null)) {
				switch (item.getFilterOperator()) {
					default:
					case AND:
						predicate = builder.and(prevPredicate, predicate);
						break;
					case OR:
						predicate = builder.or(prevPredicate, predicate);
						break;
				}
			}
			prevPredicate = predicate;
		}
		if (prevPredicate != null) {
			criteria.where(prevPredicate);
		}
		return criteria;

	}

	private Predicate getSummaryRatingPredicate(double filterValue, CriteriaQuery criteria, CriteriaBuilder builder) {
		Root<OrderInfo> order = criteria.from(OrderInfo.class);
		Subquery<Statistics> subquery = criteria.subquery(Statistics.class);
		Root fromStatistics = subquery.from(Statistics.class);
		subquery.select(fromStatistics.get("id"));
		Expression<Double> openessRatingProd = builder.prod(fromStatistics.<Double>get("opennessRating"), env.getUserRatingOpenessFactor());
		Expression<Double> successOrdersCountProd = builder.prod(fromStatistics.<Double>get("successOrdersCount"), env.getUserSuccessOrdersCountFactor());
		Expression<Double> sumExpression = builder.sum(openessRatingProd, successOrdersCountProd);
		subquery.where(builder.greaterThanOrEqualTo(sumExpression, filterValue));
		return builder.in(order.get("userId")).value(subquery);
	}

	private Predicate getOpenessRatingPredicate(int filterValue, CriteriaQuery criteria, CriteriaBuilder builder) {
		Root<OrderInfo> order = criteria.from(OrderInfo.class);
		Subquery<Statistics> subquery = criteria.subquery(Statistics.class);
		Root fromStatistics = subquery.from(Statistics.class);
		subquery.select(fromStatistics.get("id"));
		Expression<Integer> openessRatingProd = fromStatistics.<Integer>get("opennessRating");
		subquery.where(builder.greaterThanOrEqualTo(openessRatingProd, filterValue));
		return builder.in(order.get("userId")).value(subquery);
	}

	private Predicate getPartnersRatingPredicate(int filterValue, CriteriaQuery criteria, EntityManager entityManager) {
		Root<OrderInfo> order = criteria.from(OrderInfo.class);

		TypedQuery<String> query = entityManager.createNamedQuery("OrderInfo.findByPartnersRating", String.class);
		query.setParameter("status", OrderStatus.SUCCESS);
		query.setParameter("rating", filterValue);
		query.setParameter("openessFactor", env.getUserRatingOpenessFactor());
		query.setParameter("successOrdersCountFactor", env.getUserSuccessOrdersCountFactor());
		List<String> publicKeys = query.getResultList();
		if ((publicKeys == null) || publicKeys.isEmpty()) {
			publicKeys.add("");
		}
		return order.get("userPublicKey").in(publicKeys);
	}
}
