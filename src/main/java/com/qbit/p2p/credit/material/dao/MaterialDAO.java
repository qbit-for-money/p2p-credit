package com.qbit.p2p.credit.material.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.material.model.ExternalMaterial;
import com.qbit.p2p.credit.material.model.MaterialType;
import com.qbit.p2p.credit.material.model.Material;
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
	
	public Material find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					Material.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public List<Material> findByUser(String userId, int offset, int limit) {
		if ((userId == null) || userId.isEmpty()) {
			return Collections.emptyList();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<Material> query = entityManager.createNamedQuery("Material.findByUser", Material.class);
			query.setParameter("userId", userId);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public List<Material> findByUserAndType(String userId, MaterialType materialType, int offset, int limit) {
		if ((userId == null) || userId.isEmpty() || (materialType == null)) {
			return Collections.emptyList();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<Material> query = entityManager.createNamedQuery("Material.findByUserAndType", Material.class);
			query.setParameter("userId", userId);
			query.setParameter("type", materialType);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public Material create(Material material) {
		if ((material == null) || (material.getUserId() == null)) {
			throw new IllegalArgumentException("Material is NULL.");
		}
		return create(material.getUserId(), material.getType(), material.getTitle(), material.getDescription(),
				material.getPhysicalSize(), material.getExternalMaterials());
	}

	public Material create(final String userId, final MaterialType type, final String title, final String description,
			final long size, final ExternalMaterial externalMaterials) {
		if ((userId == null) || userId.isEmpty() || (type == null)) {
			throw new IllegalArgumentException("Material is inconsistent.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Material>() {

			@Override
			public Material call(EntityManager entityManager) {
				UserInfo userInfo = userDAO.find(userId);
				if (userInfo == null) {
					return null;
				}
				Material material = new Material();
				material.setUserId(userId);
				material.setType(type);
				material.setTitle(title);
				material.setDescription(description);
				material.setPhysicalSize(size);
				material.setExternalMaterials(externalMaterials);
				return entityManager.merge(material);
			}
		});
	}
}
