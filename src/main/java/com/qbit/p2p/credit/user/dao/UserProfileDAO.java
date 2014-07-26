package com.qbit.p2p.credit.user.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
import com.qbit.p2p.credit.statistics.model.GlobalStatistics;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.model.UserPrivateProfile;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.model.UserType;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
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
	@Inject
	private StatisticsDAO statisticsDAO;

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

	public List<UserPublicProfile> findByRating(long rating, boolean isNoMoreThan, String sortDataField, boolean sortDesc, int offset, int limit) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserPublicProfile> criteria = builder.createQuery(UserPublicProfile.class);

			Root<UserPublicProfile> user = criteria.from(UserPublicProfile.class);
			Expression<Long> ratingExpression = user.get("rating");
			Predicate predicate;
			if (isNoMoreThan) {
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

	public List<UserPublicProfile> findByOrder(String orderId, int offset, int limit) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			String queryName;
			queryName = "UserPublicProfile.findByOrder";

			TypedQuery<UserPublicProfile> query = entityManager.createNamedQuery(queryName, UserPublicProfile.class);

			query.setParameter("orderId", orderId);
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
				userPublicProfile.setPersonalData("DEFAULT");

				entityManager.persist(userPublicProfile);
				statisticsDAO.create(publicKey);
				System.out.println("!!!!!!!!!!! " + publicKey);

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

	public UserPublicProfile updateUserPublicProfile(final UserPublicProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile
					call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, userProfile.getPublicKey());
				if (userPublicProfile == null) {
					return null;
				}

				userPublicProfile.setName(userProfile.getName());
				userPublicProfile.setMail(userProfile.getMail());
				userPublicProfile.setMailEnabled(userProfile.isMailEnabled());
				userPublicProfile.setPhone(userProfile.getPhone());
				userPublicProfile.setPhoneEnabled(userProfile.isPhoneEnabled());
				userPublicProfile.setLanguages(userProfile.getLanguages());
				userPublicProfile.setLanguagesEnabled(userProfile.isLanguagesEnabled());
				userPublicProfile.setCurrencies(userProfile.getCurrencies());
				userPublicProfile.setCurrenciesEnabled(userProfile.isCurrenciesEnabled());
				userPublicProfile.setPersonalData(userProfile.getPersonalData());
				userPublicProfile.setPersonalDataEnabled(userProfile.isPersonalDataEnabled());
				userPublicProfile.setSocialLinks(userProfile.getSocialLinks());
				userPublicProfile.setVideos(userProfile.getVideos());
				userPublicProfile.setNamesLinks(userProfile.getNamesLinks());
				userPublicProfile.setPhones(userProfile.getPhones());
				userPublicProfile.setPassportEnabled(userProfile.isPassportEnabled());
				userPublicProfile.setBkiData(userProfile.getBkiData());

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

	public Language createLanguage(final String title) {
		if ((title == null) || title.isEmpty()) {
			throw new IllegalArgumentException("Title is empty.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Language>() {

			@Override
			public Language call(EntityManager entityManager) {

				Language language = new Language(title);
				language.setWithoutParent(true);
				entityManager.persist(language);
				return language;
			}
		});
	}

	public List<Language> findAllLanguages() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Language> criteria = builder.createQuery(Language.class);
			Root<Language> language = criteria.from(Language.class);
			criteria.select(language);
			criteria.where(builder.equal(language.get("withoutParent"), "t"));
			TypedQuery<Language> query = entityManager.createQuery(criteria);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
}
