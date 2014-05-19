package com.qbit.p2p.credit.user.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.user.model.UserPrivateProfile;
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
public class UserProfileDAO {
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	@Inject
	UserDAO userDAO;
	
	public UserPublicProfile find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					UserPublicProfile.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public List<UserPublicProfile> findByType(UserType type, int offset, int limit) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			TypedQuery<UserPublicProfile> query = entityManager.createNamedQuery("UserPublicProfile.findByType", UserPublicProfile.class);
			query.setParameter("type", type);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public List<UserPublicProfile> findByOrders(long number, boolean isNoMoreThan, int offset, int limit) {
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
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public List<UserPublicProfile> findByRating(long rating, boolean isNoMoreThan, int offset, int limit) {
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
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
	
	public UserPublicProfile create(final String publicKey, final String login, final String password, final String firstName, final String lastName, final UserType type) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile call(EntityManager entityManager) {
				UserInfo user = userDAO.create(publicKey);
				
				UserPublicProfile userPublicProfile = new UserPublicProfile();
				userPublicProfile.setPublicKey(publicKey);
				userPublicProfile.setLogin(login);
				userPublicProfile.setRating(0L);
				userPublicProfile.setFirstName(firstName);
				userPublicProfile.setLastName(lastName);
				userPublicProfile.setType(type);
				userPublicProfile.setUser(user);
				
				UserPrivateProfile userPrivateProfile = new UserPrivateProfile();
				userPrivateProfile.setPublicKey(publicKey);
				userPrivateProfile.setPassword(password);
				userPrivateProfile.setUser(user);
				
				entityManager.persist(userPublicProfile);
				entityManager.persist(userPrivateProfile);
				return userPublicProfile;
			}
		});
	}
}
