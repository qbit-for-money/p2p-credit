package com.qbit.p2p.credit.user.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.user.model.GenderType;
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
	
	public UserPrivateProfile findPrivateProfile(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					UserPrivateProfile.class, id, null);
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
			if (isNoMoreThan) {
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
			if (isNoMoreThan) {
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

	/*public UserPublicProfile create(UserPublicProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException();
		}
		return create(userProfile.getPublicKey(), userProfile.getPersonalPageData());
	}*/

	public UserPublicProfile create(final String publicKey) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile call(EntityManager entityManager) {
				UserInfo user = userDAO.find(publicKey);
				System.out.println("!!! CURRENT " + user);
				if (user == null) {
					return null;
				}
				UserPublicProfile userPublicProfile = new UserPublicProfile();
				userPublicProfile.setPublicKey(publicKey);
				userPublicProfile.setRating(0L);
				userPublicProfile.setPersonalPageData("DEFAULT");
				//userPublicProfile.setUser(user);

				UserPrivateProfile userPrivateProfile = new UserPrivateProfile();
				userPrivateProfile.setPublicKey(publicKey);
				//userPrivateProfile.setUser(user);

				entityManager.persist(userPublicProfile);
				entityManager.persist(userPrivateProfile);

				return userPublicProfile;
			}
		}
		);
	}

	public UserPublicProfile updateUserPublicProfile(UserPublicProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException();
		}

		return updateUserPublicProfile(userProfile.getPublicKey(), userProfile.getFirstName(),  userProfile.getLastName(),  userProfile.getCountry(),
				 userProfile.isCountryEnabled(),  userProfile.getCity(),  userProfile.isCityEnabled(),  userProfile.getAge(),
				  userProfile.isAgeEnabled(),  userProfile.getGender(),  userProfile.getRating(),  userProfile.getHobby(),  
				  userProfile.isHobbyEnabled(), userProfile.getPersonalPageData());

	}

	public UserPublicProfile updateUserPublicProfile(final String publicKey, final String firstName, final String lastName,
			final String country, final boolean countryEnabled, final String city, final boolean cityEnabled, 
			final int age, final boolean ageEnabled, final GenderType gender, final long rating,
			final String hobby, final boolean hobbyEnabled, final String personalPageData) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile
					call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, publicKey);
				if (userPublicProfile == null) {
					return null;
				}

				//userPublicProfile.setRating(rating);
				userPublicProfile.setFirstName(firstName);
				userPublicProfile.setLastName(lastName);
				userPublicProfile.setGender(gender);
				userPublicProfile.setCountry(country);
				userPublicProfile.setCity(city);
				userPublicProfile.setAge(age);
				userPublicProfile.setHobby(hobby);
				userPublicProfile.setPersonalPageData(personalPageData);

				return userPublicProfile;
			}
		});
	}
	
	public UserPrivateProfile updateUserPrivateProfile(UserPrivateProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException();
		}
		return updateUserPrivateProfile(userProfile.getPublicKey(), userProfile.getPhone(),  userProfile.isPhoneEnabled(),  userProfile.isPhoneVisible());

	}
	
	public UserPrivateProfile updateUserPrivateProfile(final String publicKey, final String phone, final boolean phoneEnabled, final boolean phoneVisible) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPrivateProfile>() {

			@Override
			public UserPrivateProfile
					call(EntityManager entityManager) {
				UserPrivateProfile userPrivateProfile = entityManager.find(UserPrivateProfile.class, publicKey);
				if (userPrivateProfile == null) {
					return null;
				}

				userPrivateProfile.setPhone(phone);
				userPrivateProfile.setPhoneEnabled(phoneEnabled);
				userPrivateProfile.setPhoneVisible(phoneVisible);

				return userPrivateProfile;
			}
		});
	}
}
