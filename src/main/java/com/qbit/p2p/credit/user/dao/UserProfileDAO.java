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
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.ws.rs.WebApplicationException;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class UserProfileDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	@Inject
	private UserDAO userDAO;

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

	public List<UserPublicProfile> findAll(String sortDataField, boolean sortDesc, int offset, int limit) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserPublicProfile> criteria = builder.createQuery(UserPublicProfile.class);

			Root<UserPublicProfile> user = criteria.from(UserPublicProfile.class);
			criteria.select(user);

			if (sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.desc(user.get(sortDataField)));
			} else if (!sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.asc(user.get(sortDataField)));
			}
			TypedQuery<UserPublicProfile> query = entityManager.createQuery(criteria);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			List<UserPublicProfile> allUsers = query.getResultList();
			return allUsers;
		} finally {
			entityManager.close();
		}
	}

	public long length() {
		return length(null, null);
	}

	public long length(String filterDataField, String query) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
			EntityType<UserPublicProfile> type = entityManager.getMetamodel().entity(UserPublicProfile.class);

			Root<UserPublicProfile> user = criteria.from(UserPublicProfile.class);
			criteria.select(builder.count(user));
			if (filterDataField != null && query != null) {
				criteria.where(
						builder.or(
								builder.like(
										builder.lower(
												user.get(
														type.getDeclaredSingularAttribute(filterDataField, String.class)
												)
										), "%" + query.toLowerCase() + "%"
								)
						)
				);
			}

			return entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}
	
	public long lengthWithFilterByRating(long rating, boolean isNoMoreThan) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

			Root<UserPublicProfile> user = criteria.from(UserPublicProfile.class);
			Expression<Long> ratingExpression = user.get("rating");
			Predicate predicate;
			if(isNoMoreThan) {
				predicate = builder.le(ratingExpression, rating);
			} else {
				predicate = builder.ge(ratingExpression, rating);
			}
			criteria.select(builder.count(user));
			criteria.where(predicate);		
			return entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}

	public List<UserPublicProfile> findWithFilter(String filterDataField, String queryText, String sortDataField, boolean sortDesc, int offset, int limit) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserPublicProfile> criteria = builder.createQuery(UserPublicProfile.class);
			EntityType<UserPublicProfile> type = entityManager.getMetamodel().entity(UserPublicProfile.class);

			Root<UserPublicProfile> user = criteria.from(UserPublicProfile.class);
			criteria.select(user);
			if (filterDataField != null && queryText != null) {
				criteria.where(
						builder.or(
								builder.like(
										builder.lower(
												user.get(
														type.getDeclaredSingularAttribute(filterDataField, String.class)
												)
										), "%" + queryText.toLowerCase() + "%"
								)
						)
				);
			}

			if (sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.desc(user.get(sortDataField)));
			} else if (!sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.asc(user.get(sortDataField)));
			}
			TypedQuery<UserPublicProfile> query = entityManager.createQuery(criteria);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			List<UserPublicProfile> users = query.getResultList();
			return users;
		} finally {
			entityManager.close();
		}
	}
	
		public List<UserPublicProfile> findByRating(long rating, boolean isNoMoreThan, String sortDataField, boolean sortDesc, int offset, int limit) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserPublicProfile> criteria = builder.createQuery(UserPublicProfile.class);

			Root<UserPublicProfile> user = criteria.from(UserPublicProfile.class);
			Expression<Long> ratingExpression = user.get("rating");
			Predicate predicate;
			if(isNoMoreThan) {
				predicate = builder.le(ratingExpression, rating);
			} else {
				predicate = builder.ge(ratingExpression, rating);
			}
			criteria.select(user).where(predicate);
			
			if (sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.desc(user.get(sortDataField)));
			} else if (!sortDesc && sortDataField != null && !sortDataField.isEmpty()) {
				criteria.orderBy(builder.asc(user.get(sortDataField)));
			}
			TypedQuery<UserPublicProfile> query = entityManager.createQuery(criteria);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			List<UserPublicProfile> users = query.getResultList();
			return users;
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

	public UserPublicProfile create(final String publicKey) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile call(EntityManager entityManager) {
				UserInfo user = userDAO.find(publicKey);
				if (user == null) {
					throw new WebApplicationException();
				}
				UserPublicProfile userPublicProfile = new UserPublicProfile();
				userPublicProfile.setPublicKey(publicKey);
				userPublicProfile.setRating(0L);
				userPublicProfile.setPersonalPageData("DEFAULT");

				entityManager.persist(userPublicProfile);

				return userPublicProfile;
			}
		}
		);
	}

	public UserPrivateProfile createUserPrivateProfile(final String publicKey) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPrivateProfile>() {

			@Override
			public UserPrivateProfile call(EntityManager entityManager) {
				UserInfo user = userDAO.find(publicKey);
				if (user == null) {
					throw new WebApplicationException();
				}
				UserPrivateProfile userPrivateProfile = new UserPrivateProfile();
				userPrivateProfile.setPublicKey(publicKey);

				entityManager.persist(userPrivateProfile);

				return userPrivateProfile;
			}
		}
		);
	}

	public UserPublicProfile updateUserPublicProfile(UserPublicProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException();
		}

		return updateUserPublicProfile(userProfile.getPublicKey(), userProfile.getFirstName(), userProfile.getLastName(), userProfile.getCountry(),
				userProfile.isCountryEnabled(), userProfile.getCity(), userProfile.isCityEnabled(), userProfile.getBirthDate(),
				userProfile.isAgeEnabled(), userProfile.getGender(), userProfile.getRating(), userProfile.getHobby(),
				userProfile.isHobbyEnabled(), userProfile.getPersonalPageData());

	}

	public UserPublicProfile updateUserPublicProfile(final String publicKey, final String firstName, final String lastName,
			final String country, final boolean countryEnabled, final String city, final boolean cityEnabled,
			final Date birthDate, final boolean ageEnabled, final GenderType gender, final long rating,
			final String hobby, final boolean hobbyEnabled, final String personalPageData) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile
					call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, publicKey);
				if (userPublicProfile == null) {
					return null;
				}

				userPublicProfile.setRating(rating);
				userPublicProfile.setFirstName(firstName);
				userPublicProfile.setLastName(lastName);
				userPublicProfile.setGender(gender);
				userPublicProfile.setCountry(country);
				userPublicProfile.setCountryEnabled(countryEnabled);
				userPublicProfile.setCity(city);
				userPublicProfile.setCityEnabled(cityEnabled);
				userPublicProfile.setBirthDate(birthDate);
				userPublicProfile.setAgeEnabled(ageEnabled);
				userPublicProfile.setHobby(hobby);
				userPublicProfile.setHobbyEnabled(hobbyEnabled);
				userPublicProfile.setPersonalPageData(personalPageData);

				return userPublicProfile;
			}
		});
	}

	public UserPrivateProfile updateUserPrivateProfile(UserPrivateProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException();
		}
		return updateUserPrivateProfile(userProfile.getPublicKey(), userProfile.getPhone(), userProfile.isPhoneEnabled(), userProfile.isPhoneVisible());

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
