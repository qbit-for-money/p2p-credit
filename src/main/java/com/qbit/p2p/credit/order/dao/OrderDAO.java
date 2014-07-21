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
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
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
				/*List<OrderCategory> categories = findAllCategories();
				 //for(OrderCategory category : categories) {
				 //	if(newOrder.getCategories().)
				 //}
				 List<OrderCategory> orderCategories = orderInfo.getCategories();
				 if(orderCategories == null) {
				 orderInfo.setCategories(new ArrayList<OrderCategory>());
				 }
				
				 for (OrderCategory category : orderCategories) {
				 if (categories.contains(category)) {
				 category.setId(categories.get(categories.indexOf(category)).getId());
				 System.out.println("||||||||||||||||||| " + category);
				 }
					
				 //orderCategories.add(category);
				 }*/
				List<OrderCategory> orderCategories = orderInfo.getCategories();
				if (orderCategories == null) {
					orderInfo.setCategories(new ArrayList<OrderCategory>());
				}
				for (OrderCategory category : orderCategories) {
					category.setCategoryForOrder(true);
				}
				orderInfo.setStatus(OrderStatus.OPENED);
				orderInfo.setCreationDate(new Date());
				entityManager.persist(orderInfo);
				return orderInfo;
			}
		});
	}

	/*public Respond changeResponseStatus(final Respond newResponse) {
	 if (newResponse == null) {
	 throw new IllegalArgumentException();
	 }
	 return invokeInTransaction(entityManagerFactory, new TrCallable<Respond>() {

	 @Override
	 public Respond
	 call(EntityManager entityManager) {
	 Respond response = entityManager.find(Respond.class, newResponse.getId());
	 if (response == null) {
	 return null;
	 }
	 response.setStatus(newResponse.getStatus());
	 return response;
	 }
	 });
	 }*/
	public OrderInfo update(final OrderInfo newOrder) {
		if (newOrder == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderInfo>() {

			@Override
			public OrderInfo
				call(EntityManager entityManager) {
				OrderInfo order = entityManager.find(OrderInfo.class, newOrder.getId());
				if (order == null) {
					return null;
				}
				List<OrderCategory> categories = findAllCategories();
				//for(OrderCategory category : categories) {
				//	if(newOrder.getCategories().)
				//}
				List<OrderCategory> orderCategories = order.getCategories();
				if (orderCategories == null) {
					order.setCategories(new ArrayList<OrderCategory>());
				}

				for (OrderCategory category : newOrder.getCategories()) {
					if (categories.contains(category)) {
						//category.setId(categories.get(categories.indexOf(category)).getId());
						System.out.println("||||||||||||||||||| " + category);
					}

					//orderCategories.add(category);
				}
				//order.setCategories(newOrder.getCategories());
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
				if (newOrder.getApprovedResponseId() != null) {
					//newOrder.getApprovedResponse().setId(null);
					order.setApprovedResponseId(newOrder.getApprovedResponseId());
					System.out.println("############################# " + order.getApprovedResponseId());
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

	public OrderCategory createCategory(final String title) {
		if ((title == null) || title.isEmpty()) {
			throw new IllegalArgumentException("Title is empty.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderCategory>() {

			@Override
			public OrderCategory call(EntityManager entityManager) {

				OrderCategory category = new OrderCategory(title);
				category.setCategoryForOrder(false);
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
			//Join<Entity2>  join2 = root3.join("entity2");
			criteria.select(category);
			criteria.where(builder.equal(category.get("categoryForOrder"), "f"));
			//where(builder.greaterThanOrEqualTo(category.join("ordercategory2").get(item.getFilterDataField()), item.getFilterValue()));
			TypedQuery<OrderCategory> query = entityManager.createQuery(criteria);
			//TypedQuery<OrderCategory> query = entityManager.createQuery("SELECT DISTINCT t0 FROM OrderCategory t0 where t0.id NOT IN ( select o.id from OrderCategory o JOIN o.)", OrderCategory.class);
			//TypedQuery<OrderCategory> query = entityManager.createQuery("SELECT t0 FROM OrderCategory t0 where t0.orderInfo IS NOT NULL ", OrderCategory.class);
			//TypedQuery<OrderCategory> query = entityManager.createQuery(criteria);
			List<OrderCategory> c = query.getResultList();
			//TypedQuery<Respond> query2 = entityManager.createQuery("SELECT t0 FROM Respond t0 LEFT", Respond.class);

			System.out.println("__________________________________ " + c);
			//System.out.println("________________+++_______________ " + query2.getResultList());
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
			for (FilterItem item : filterItems) {
				if ((item.getFilterDataField() != null) && (item.getFilterValue() != null)) {
					//ParameterExpression<String> parameter = builder.parameter(String.class, item.getFilterDataField());
					//Predicate valuePredicate = builder.equal(builder.lower(order.<String>get(item.getFilterDataField())), "%" + item.getFilterValue().toLowerCase() + "%");
					Predicate valuePredicate = null;
					System.out.println("!!!@@!!! " + item.getFilterCondition() + " " + item.getFilterDataField() + " " + item.getFilterValue());
					if ((item.getFilterCondition() == null) || (FilterCondition.EQUAL == item.getFilterCondition())) {
						if ("status".equals(item.getFilterDataField())) {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), OrderStatus.valueOf(item.getFilterValue()));
							if (statusesPredicate == null) {
								statusesPredicate = valuePredicate;
							} else {
								statusesPredicate = builder.or(valuePredicate, statusesPredicate);
							}
						} else if ("givingCurrency".equals(item.getFilterDataField())) {
							valuePredicate = builder.equal(order.get(item.getFilterDataField()), Currency.valueOf(item.getFilterValue()));
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
							Subquery<UserPublicProfile> subquery = criteria.subquery(UserPublicProfile.class);
							Root fromUserPublicProfile = subquery.from(UserPublicProfile.class);
							subquery.select(fromUserPublicProfile.get("publicKey"));
							//subquery.where(builder.equal(fromUserPublicProfile.get("name"), "gg"));
							subquery.where(builder.greaterThanOrEqualTo(fromUserPublicProfile.join("statistic").get(item.getFilterDataField()), item.getFilterValue()));
							valuePredicate = builder.in(order.get("userPublicKey")).value(subquery);
							//subquery.where(builder.ge(fromUserPublicProfile.get("pint"),30000));
							//valuePredicate = builder.greaterThanOrEqualTo(order.<Date>get(item.getFilterDataField()), DateUtil.stringToDate(item.getFilterValue()));
						} else if ("responsesCount".equals(item.getFilterDataField())) {

							//Join<OrderInfo, Respond> secondTable = order.join("responses", JoinType.LEFT);
							/*builder.count(order.get("responses"));
							
							 Subquery<Long> subquery = criteria.subquery(Long.class);
							 Root fromRespond = subquery.from(Respond.class);
							 Root respond = criteria.from(Respond.class);
							 //Expression<Long> responsesExpression = null;
							 //responsesExpression = builder.countDistinct(fromRespond);
							 subquery.select(builder.countDistinct(fromRespond));
							 //subquery.where(builder.equal(fromRespond.join("orderInfo").get("id"), order.get("id")));
							 //subquery.where(builder.equal(fromUserPublicProfile.get("name"), "gg"));
							 //subquery.where(builder.greaterThanOrEqualTo(builder.countDistinct(fromRespond.get("publicKey")), Long.parseLong(item.getFilterValue())));
							 query.select(cb.count(u)).where(cb.in(u).value(subquery));
							 Predicate valuePredicate1 = builder.in(order.get("userPublicKey")).value(subquery);*/
							Expression<Collection<String>> e = order.get("responses").get("id");
							valuePredicate = builder.greaterThanOrEqualTo(builder.count(e), Long.parseLong(item.getFilterValue()));
						} else if ("partnersRating".equals(item.getFilterDataField())) {

							TypedQuery<String> query = entityManager.createQuery("SELECT t1.publicKey FROM UserPublicProfile t0, UserPublicProfile t1 GROUP BY t0.publicKey, t1.publicKey HAVING t0.publicKey IN (SELECT DISTINCT t2.userPublicKey FROM Respond t2 WHERE t2.id IN (SELECT t3.approvedResponseId FROM OrderInfo t3 WHERE (t3.status = :status) AND (t3.userPublicKey = t1.publicKey))) AND SUM(t0.statistic.summaryRating) >= :rating - 1", String.class);
							query.setParameter("status", OrderStatus.SUCCESS);
							System.out.println("***@@@@@ " + Long.parseLong(item.getFilterValue()));
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
			if (statusesPredicate != null) {
				if (mainOperatorPredicate == null) {
					mainOperatorPredicate = statusesPredicate;
				} else {
					mainOperatorPredicate = builder.and(statusesPredicate, mainOperatorPredicate);
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
			Root<UserPublicProfile> profile = criteria.from(UserPublicProfile.class);

			Expression<Integer> ex = profile.get("statistic").get("summaryRating");
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

			Predicate p3 = builder.and(builder.in(profile.get("publicKey")).value(usersIdSubquery), builder.notEqual(profile.get("publicKey"), userPublicKey));
			criteria.where(p3);
			Long result = entityManager.createQuery(criteria).getSingleResult();
			return (result == null) ? 0 : result;
		} finally {
			entityManager.close();
		}
	}
}
