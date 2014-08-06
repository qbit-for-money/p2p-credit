package com.qbit.p2p.credit.order.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
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
	@Inject
	private OrderDAO orderDAO;

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

	public int changeStatus(String orderId, String userId, OrderStatus status, String partnerId, OrderStatus counteragentStatus) {
		OrderInfo order = orderDAO.find(orderId);
		if (!order.getStatus().isValidNewStatus(status)) {
			return 0;
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaUpdate<OrderInfo> update = builder.createCriteriaUpdate(OrderInfo.class);
			Root<OrderInfo> root = update.from(OrderInfo.class);
			if (OrderStatus.IN_PROCESS == status) {
				update.set("approvedUserId", partnerId);
			}
			if (counteragentStatus != null) {
				update.set("status", counteragentStatus);
			} else {
				update.set("status", status);
			}
			update.where(builder.equal(root.get("id"), orderId));
			update.where(builder.equal(root.get("userId"), userId));
			int numberOfEntities = entityManager.createQuery(update).executeUpdate();
			statisticsService.recalculatePartnersRating(userId);
			if (OrderStatus.SUCCESS == status) {
				statisticsService.recalculatePartnersRating(partnerId);
			}
			return numberOfEntities;
		} finally {
			entityManager.close();
		}
	}
}
