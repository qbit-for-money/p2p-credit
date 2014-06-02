package com.qbit.p2p.credit.material.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.material.model.ExternalMaterials;
import com.qbit.p2p.credit.material.model.MaterialType;
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
	
	@Inject
	private UserDAO userDAO;
	
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
	
	public List<Materials> findByUser(String userId, int offset, int limit) {
		if ((userId == null) || userId.isEmpty()) {
			return Collections.emptyList();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<Materials> query = entityManager.createNamedQuery("Materials.findByUser", Materials.class);
			query.setParameter("userId", userId);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public List<Materials> findByUserAndType(String userId, MaterialType materialType, int offset, int limit) {
		if ((userId == null) || userId.isEmpty() || materialType == null) {
			return Collections.emptyList();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<Materials> query = entityManager.createNamedQuery("Materials.findByUserAndType", Materials.class);
			query.setParameter("userId", userId);
			query.setParameter("type", materialType);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public Materials create(Materials materials) {
		if (materials == null) {
			throw new IllegalArgumentException("Material is NULL.");
		}
		return create(materials.getUserId(), materials.getType(), materials.getTitle(), materials.getAuthor(),materials.getDescription(),
				materials.getPhysicalSize(), materials.getExternalMaterials());
	}

	public Materials create(final String userId, final MaterialType type, final String title, final String author, final String description,
			final long size, final ExternalMaterials externalMaterials) {
		if ((userId == null) || userId.isEmpty() || (type == null)) {
			throw new IllegalArgumentException("Material is inconsistent.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Materials>() {

			@Override
			public Materials call(EntityManager entityManager) {
				UserInfo userInfo = userDAO.find(userId);
				if (userInfo == null) {
					return null;
				}
				Materials material = new Materials();
				material.setUserId(userId);
				material.setType(type);
				material.setTitle(title);
				material.setAuthor(author);
				material.setDescription(description);
				material.setPhysicalSize(size);
				material.setExternalMaterials(externalMaterials);
				entityManager.persist(material);
				return material;
			}
		});
	}
	
	public Materials update(Materials materials) {
		if (materials == null) {
			throw new IllegalArgumentException("Material is NULL.");
		}
		return update(materials.getId(), materials.getUserId(), materials.getType(), materials.getTitle(), materials.getAuthor(),materials.getDescription(),
				materials.getPhysicalSize(), materials.getExternalMaterials());
	}
	
	public Materials update(final String id, final String userId, final MaterialType type, final String title, final String author, final String description,
			final long size, final ExternalMaterials externalMaterials) {
		if ((userId == null) || userId.isEmpty() || (type == null)) {
			throw new IllegalArgumentException("Material is inconsistent.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Materials>() {

			@Override
			public Materials call(EntityManager entityManager) {
				Materials material = entityManager.find(Materials.class, id);
				if (material == null) {
					return null;
				}
				material.setUserId(userId);
				material.setType(type);
				material.setTitle(title);
				material.setAuthor(author);
				material.setDescription(description);
				material.setPhysicalSize(size);
				material.setExternalMaterials(externalMaterials);
				return material;
			}
		});
	}
}
