package com.qbit.p2p.credit.material.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.material.model.Materials;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.OrderInfo;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 * @author Alexander_Sergeev
 */
public class MaterialDAO {
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	@Inject
	private OrderDAO orderDAO;
	
	public Materials find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					Materials.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public List<Materials> findByOrder(String orderId, int offset, int limit) {
		if ((orderId == null) || orderId.isEmpty()) {
			return Collections.emptyList();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<Materials> query = entityManager.createNamedQuery("Materials.findByOrder", Materials.class);
			query.setParameter("orderId", orderId);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public Materials create(Materials materials) {
		if (materials == null) {
			throw new IllegalArgumentException("Order is NULL.");
		}
		return create(materials.getOrderId());
	}

	public Materials create(final String orderId) {
		if (orderId == null || orderId.isEmpty()) {
			throw new IllegalArgumentException("Order is inconsistent.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Materials>() {

			@Override
			public Materials call(EntityManager entityManager) {
				OrderInfo orderInfo = orderDAO.find(orderId);
				if (orderInfo == null) {
					return null;
				}
				Materials material = new Materials();
				material.setOrderId(orderId);
				entityManager.persist(material);
				return material;
			}
		});
	}
}
