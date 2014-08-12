package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.order.model.Comment;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class OrderFlowDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	@Inject
	private StatisticsService statisticsService;

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
				List<Respond> responses = order.getResponses();
				if (responses != null) {
					for (Respond orderRespond : order.getResponses()) {
						if (orderRespond.getUserId().equals(respond.getUserId())) {
							return null;
						}
					}
				} else {
					responses = new ArrayList<>();
				}
				responses.add(respond);
				order.setResponses(responses);
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

	public int changeStatusByPartner(String orderId, String partnerId, OrderStatus status, Comment comment) {
		if ((orderId == null) || orderId.isEmpty() || (partnerId == null) || partnerId.isEmpty()
			|| (status == null)) {
			return 0;
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaUpdate<OrderInfo> update = builder.createCriteriaUpdate(OrderInfo.class);
			Root<OrderInfo> root = update.from(OrderInfo.class);
			update.set("status", status);
			Predicate updateStatusPredicate = builder.equal(root.get("id"), orderId);
			updateStatusPredicate = builder.and(updateStatusPredicate, builder.equal(root.get("partnerId"), partnerId));
			Predicate possibleStatusesPredicate = getPossibleStatusesPredicate(status, root, builder);
			if (possibleStatusesPredicate != null) {
				updateStatusPredicate = builder.and(updateStatusPredicate, possibleStatusesPredicate);
			}
			update.where(updateStatusPredicate);
			int numberOfEntities = entityManager.createQuery(update).executeUpdate();
			if (EnumSet.of(OrderStatus.SUCCESS, OrderStatus.NOT_SUCCESS, OrderStatus.ARBITRATION).contains(status)) {
				statisticsService.recalculateUserOrdersStatistics(partnerId);
				statisticsService.recalculatePartnersRating(partnerId);
			}
			return numberOfEntities;
		} finally {
			entityManager.close();
		}
	}

	public int changeStatus(final String orderId, final String userId, final OrderStatus status, final Comment comment) {
		if ((orderId == null) || orderId.isEmpty() || (userId == null) || userId.isEmpty()
			|| (status == null)) {
			return 0;
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Integer>() {

			@Override
			public Integer
				call(EntityManager entityManager) {
				CriteriaBuilder builder = entityManager.getCriteriaBuilder();
				CriteriaUpdate<OrderInfo> update = builder.createCriteriaUpdate(OrderInfo.class);
				Root<OrderInfo> root = update.from(OrderInfo.class);
				update.set("status", status);
				Predicate updateStatusPredicate = builder.equal(root.get("id"), orderId);
				updateStatusPredicate = builder.and(updateStatusPredicate, builder.equal(root.get("userId"), userId));
				Predicate possibleStatusesPredicate = getPossibleStatusesPredicate(status, root, builder);
				if (possibleStatusesPredicate != null) {
					updateStatusPredicate = builder.and(updateStatusPredicate, possibleStatusesPredicate);
				}
				update.where(updateStatusPredicate);
				int numberOfEntities = entityManager.createQuery(update).executeUpdate();
				if (EnumSet.of(OrderStatus.SUCCESS, OrderStatus.NOT_SUCCESS, OrderStatus.ARBITRATION).contains(status)) {
					statisticsService.recalculateUserOrdersStatistics(userId);
					statisticsService.recalculatePartnersRating(userId);
				}
				return numberOfEntities;
			}
		});
	}

	private Predicate getPossibleStatusesPredicate(OrderStatus status, Root<OrderInfo> root, CriteriaBuilder builder) {
		Predicate possibleStatusesPredicate = null;
		for (OrderStatus possibleStatus : status.prev()) {
			Predicate statusPredicate = builder.equal(root.<OrderStatus>get("status"), possibleStatus);
			if (possibleStatusesPredicate == null) {
				possibleStatusesPredicate = statusPredicate;
			} else {
				possibleStatusesPredicate = builder.or(statusPredicate, possibleStatusesPredicate);
			}
		}
		return builder.and(possibleStatusesPredicate, builder.notEqual(root.<OrderStatus>get("status"), OrderStatus.OPENED));

	}

	public Integer approveRespond(final String orderId, final String userId, final String partnerId, final Comment comment) {
		if (partnerId == null || partnerId.isEmpty()) {
			return null;
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Integer>() {

			@Override
			public Integer
				call(EntityManager entityManager) {
				CriteriaBuilder builder = entityManager.getCriteriaBuilder();
				CriteriaUpdate<OrderInfo> update = builder.createCriteriaUpdate(OrderInfo.class);
				Root<OrderInfo> root = update.from(OrderInfo.class);
				update.set("partnerId", partnerId);
				update.set("status", OrderStatus.IN_PROCESS);
				Predicate predicate = builder.and(
					builder.equal(root.get("id"), orderId),
					builder.equal(root.get("userId"), userId),
					builder.equal(root.get("status"), OrderStatus.OPENED),
					builder.isNull(root.get("partnerId")));
				update.where(predicate);
				return entityManager.createQuery(update).executeUpdate();
			}
		});
	}
}
