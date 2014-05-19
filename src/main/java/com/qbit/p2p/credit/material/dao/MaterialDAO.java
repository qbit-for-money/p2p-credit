package com.qbit.p2p.credit.material.dao;

import com.qbit.commons.dao.util.DAOUtil;
import com.qbit.p2p.credit.material.model.Materials;
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
	
	public Materials find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					Materials.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public List<Materials> findByOrder(String orderId) {
		if ((orderId == null) || orderId.isEmpty()) {
			return Collections.emptyList();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<Materials> query = entityManager.createNamedQuery("Materials.findByOrder", Materials.class);
			query.setParameter("orderId", orderId);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
}
