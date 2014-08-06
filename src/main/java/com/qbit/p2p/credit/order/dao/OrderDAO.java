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
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.order.model.DateValueProvider;
import com.qbit.p2p.credit.order.model.IntegerValueProvider;
import com.qbit.p2p.credit.order.model.OrderStatusArrayValueProvider;
import com.qbit.p2p.credit.order.model.OrderStatusValueProvider;
import com.qbit.p2p.credit.order.model.SortOrder;
import com.qbit.p2p.credit.order.model.StringArrayValueProvider;
import com.qbit.p2p.credit.order.model.StringValueProvider;
import com.qbit.p2p.credit.order.model.ValueProvider;
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
	private UserProfileDAO profileDAO;

	private static final Map<String, ValueProvider> VALUE_PROVIDERS_MAP;
	private static final Map<String, String> EXPRESSION_PROVIDERS_MAP;

	static {
		ValueProvider stringValueProvider = new StringValueProvider();
		ValueProvider integerValueProvider = new IntegerValueProvider();
		ValueProvider dateValueProvider = new DateValueProvider();
		ValueProvider stringArrayValueProvider = new StringArrayValueProvider();
		ValueProvider orderStatusArrayValueProvider = new OrderStatusArrayValueProvider();
		VALUE_PROVIDERS_MAP = new HashMap<>();
		VALUE_PROVIDERS_MAP.put("status", new OrderStatusValueProvider());
		VALUE_PROVIDERS_MAP.put("takingCurrency", stringValueProvider);
		VALUE_PROVIDERS_MAP.put("givingCurrency", stringValueProvider);
		VALUE_PROVIDERS_MAP.put("partnersRating", integerValueProvider);
		VALUE_PROVIDERS_MAP.put("languages", stringArrayValueProvider);
		VALUE_PROVIDERS_MAP.put("categories", stringArrayValueProvider);
		VALUE_PROVIDERS_MAP.put("bookingDeadline", dateValueProvider);
		VALUE_PROVIDERS_MAP.put("summaryRating", integerValueProvider);
		VALUE_PROVIDERS_MAP.put("responsesCount", integerValueProvider);
		VALUE_PROVIDERS_MAP.put("partnersRating", integerValueProvider);
		VALUE_PROVIDERS_MAP.put("statuses", orderStatusArrayValueProvider);

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
				order.setCategories(newOrder.getCategories());
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

	public List<OrderInfo> findWithFilter(String userId, SearchRequest searchRequest) {

		boolean sortDesc = false;
		if ((searchRequest != null) && searchRequest.getSortOrder() != null && searchRequest.getSortOrder() == SortOrder.DESC) {
			sortDesc = true;
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria;

			criteria = builder.createQuery(OrderInfo.class);
			Root<OrderInfo> order = criteria.from(OrderInfo.class);
			criteria.select(order).distinct(true);
			criteria = formCriteria(userId, searchRequest, entityManager, criteria, builder);

			String sortDataField = searchRequest.getSortDataField();
			if (sortDataField != null && !sortDataField.isEmpty()) {
				if (sortDesc) {
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
			criteria = formCriteria(userId, searchRequest, entityManager, criteria, builder);

			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	private CriteriaQuery formCriteria(String userId, SearchRequest searchRequest, EntityManager entityManager, CriteriaQuery criteria, CriteriaBuilder builder) {
		if ((searchRequest != null) && (searchRequest.getFilterItems() == null)) {
			return criteria;
		}
		Root<OrderInfo> order = criteria.from(OrderInfo.class);
		List<FilterItem> filterItems = (searchRequest != null) ? searchRequest.getFilterItems() : null;
		Predicate prevPredicate = null;
		for (FilterItem item : filterItems) {
			if ((item.getFilterDataField() == null) || (item.getFilterValue() == null)) {
				continue;
			}

			Predicate predicate = null;
			Expression<?> expression = order.get(item.getFilterDataField());
			ValueProvider valueProvider = VALUE_PROVIDERS_MAP.get(item.getFilterDataField());
			String pathExpression = EXPRESSION_PROVIDERS_MAP.get(item.getFilterDataField());
			switch (item.getFilterCondition()) {
				case IS_MEMBER:
					Expression<Collection<String>> itemsExpression;
					if ((pathExpression != null) && !pathExpression.isEmpty()) {
						itemsExpression = order.get(item.getFilterDataField()).get(pathExpression);
					} else {
						itemsExpression = order.get(item.getFilterDataField());
					}
					if (valueProvider instanceof StringArrayValueProvider) {
						StringArrayValueProvider stringArrayValueProvider = (StringArrayValueProvider) valueProvider;
						String[] itemsValues = stringArrayValueProvider.get(item.getFilterValue());
						for (String itemValue : itemsValues) {
							Predicate containsItems = builder.isMember(itemValue, itemsExpression);
							if (predicate == null) {
								predicate = containsItems;
							} else {
								predicate = builder.or(containsItems, predicate);
							}
						}
					} else {
						if (valueProvider instanceof Comparable) {
							Expression<Collection<Comparable>> comparableExpression = order.get(item.getFilterDataField());
							predicate = builder.isMember((Comparable) valueProvider.get(item.getFilterValue()), comparableExpression);
						}
					}
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
						builder.lower(
							order.get(
								type.getDeclaredSingularAttribute(item.getFilterDataField(), String.class)
							)
						), "%" + item.getFilterValue().toLowerCase() + "%"
					);
					break;
				case LESS_THAN_OR_EQUAL:
					if (valueProvider instanceof Comparable) {
						Expression<Comparable> comparableExpression = order.get(item.getFilterDataField());
						predicate = builder.lessThanOrEqualTo(comparableExpression, (Comparable) valueProvider.get(item.getFilterValue()));
					}
					break;
				case LESS_THAN:
					if (valueProvider instanceof Comparable) {
						Expression<Comparable> comparableExpression = order.get(item.getFilterDataField());
						predicate = builder.lessThan(comparableExpression, (Comparable) valueProvider.get(item.getFilterValue()));
					}
					break;
				case GREATER_THAN_OR_EQUAL:
					if (valueProvider instanceof Comparable) {
						Expression<Comparable> comparableExpression = order.get(item.getFilterDataField());
						predicate = builder.greaterThanOrEqualTo(comparableExpression, (Comparable) valueProvider.get(item.getFilterValue()));
					}
					break;
				case GREATER_THAN:
					if (valueProvider instanceof Comparable) {
						Expression<Comparable> comparableExpression = order.get(item.getFilterDataField());
						predicate = builder.greaterThan(comparableExpression, (Comparable) valueProvider.get(item.getFilterValue()));
					}
					break;
			}
			if (prevPredicate != null) {
				switch (item.getFilterOperator()) {
					case AND:
						predicate = builder.and(prevPredicate, predicate);
					case OR:
						predicate = builder.or(prevPredicate, predicate);
				}
				prevPredicate = predicate;
			}
			if (prevPredicate != null) {
				criteria.where(prevPredicate);
			}
		}
		return criteria;
	}
}
