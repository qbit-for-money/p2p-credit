package com.qbit.p2p.credit.user.dao;

import com.qbit.commons.dao.util.DAOUtil;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.model.UserType;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class UserDAO {
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	public UserPublicProfile find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					UserPublicProfile.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public List<UserPublicProfile> findByType(UserType type) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<UserPublicProfile> query = entityManager.createNamedQuery("UserPublicProfile.findByType", UserPublicProfile.class);
			query.setParameter("type", type);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public List<UserPublicProfile> findByOrders(long number, boolean isNoMoreThan) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			String queryName;
			if(isNoMoreThan) {
				queryName = "UserPublicProfile.findByOrdersNoMoreThan";
			} else {
				queryName = "UserPublicProfile.findByOrdersNoLessThan";
			}
			TypedQuery<UserPublicProfile> query = entityManager.createNamedQuery(queryName, UserPublicProfile.class);

			query.setParameter("number", number);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public List<UserPublicProfile> findByRating(long rating, boolean isNoMoreThan) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			String queryName;
			if(isNoMoreThan) {
				queryName = "UserPublicProfile.findByRatingNoMoreThan";
			} else {
				queryName = "UserPublicProfile.findByRatingNoLessThan";
			}
			TypedQuery<UserPublicProfile> query = entityManager.createNamedQuery(queryName, UserPublicProfile.class);

			query.setParameter("rating", rating);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
}
