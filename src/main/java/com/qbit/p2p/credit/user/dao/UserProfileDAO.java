package com.qbit.p2p.credit.user.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserDAO;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.user.model.DataLink;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
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
	@Inject
	private LanguageDAO languageDAO;

	public UserPublicProfile find(String id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					UserPublicProfile.class, id, null);
		} finally {
			entityManager.close();
		}
	}

	public List<UserPublicProfile> findAll(String sortDataField, boolean sortDesc, int offset, int limit) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<UserPublicProfile> criteria = builder.createQuery(UserPublicProfile.class);
			Root<UserPublicProfile> userSelection = criteria.from(UserPublicProfile.class);
			criteria.select(userSelection);
			if ((sortDataField != null) && !sortDataField.isEmpty()) {
				if (sortDesc) {
					criteria.orderBy(builder.desc(userSelection.get(sortDataField)));
				} else {
					criteria.orderBy(builder.asc(userSelection.get(sortDataField)));
				}
			}
			TypedQuery<UserPublicProfile> query = entityManager.createQuery(criteria);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
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

	public UserPublicProfile create(final String userId) {
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, userId);
				if (userPublicProfile == null) {
					UserInfo user = userDAO.find(userId);
					if (user == null) {
						throw new WebApplicationException();
					}
					userPublicProfile = new UserPublicProfile(userId);
				}
				entityManager.merge(userPublicProfile);
				return userPublicProfile;
			}
		});
	}

	public UserPublicProfile updateUserMainAttributes(final UserPublicProfile userProfile) {
		if (userProfile == null) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, userProfile.getUserId());
				if (userPublicProfile == null) {
					return null;
				}
				System.out.println("&&&&&&&&&&&&&&77");
				userPublicProfile.setName(userProfile.getName());
				userPublicProfile.setMail(userProfile.getMail());
				userPublicProfile.setMailEnabled(userProfile.isMailEnabled());
				userPublicProfile.setPhone(userProfile.getPhone());
				userPublicProfile.setPhoneEnabled(userProfile.isPhoneEnabled());
				List<Language> languages = userProfile.getLanguages();
				if ((languages != null) && !languages.isEmpty()) {
					List<Language> notCustomLanguages = languageDAO.findAll();
					if (notCustomLanguages != null) {
						for (Language language : languages) {
							if (!notCustomLanguages.contains(language)) {
								language.setCustom(true);
							} else {
								language.setCustom(false);
							}
						}
					}
				}
				userPublicProfile.setLanguages(languages);
				userPublicProfile.setLanguagesEnabled(userProfile.isLanguagesEnabled());
				userPublicProfile.setCurrencies(userProfile.getCurrencies());
				userPublicProfile.setCurrenciesEnabled(userProfile.isCurrenciesEnabled());
				userPublicProfile.setPersonalData(userProfile.getPersonalData());
				userPublicProfile.setPersonalDataEnabled(userProfile.isPersonalDataEnabled());
				return entityManager.merge(userPublicProfile);
			}
		});
	}

	public UserPublicProfile updateUserSocialLinks(final String userId, final List<DataLink> socialLinks) {
		if ((userId == null) || userId.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile
					call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, userId);
				if (userPublicProfile == null) {
					return null;
				}
				userPublicProfile.setSocialLinks(socialLinks);
				return userPublicProfile;
			}
		});
	}

	public UserPublicProfile updateUserVideos(final String userId, final List<DataLink> videos) {
		if ((userId == null) || userId.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, userId);
				if (userPublicProfile == null) {
					return null;
				}
				userPublicProfile.setVideos(videos);
				return userPublicProfile;
			}
		});
	}

	public UserPublicProfile updatePassportEnabled(final String userId, final boolean isPassportEnabled) {
		if ((userId == null) || userId.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<UserPublicProfile>() {

			@Override
			public UserPublicProfile
					call(EntityManager entityManager) {
				UserPublicProfile userPublicProfile = entityManager.find(UserPublicProfile.class, userId);
				if (userPublicProfile == null) {
					return null;
				}
				userPublicProfile.setPassportEnabled(isPassportEnabled);
				return userPublicProfile;
			}
		});
	}
}
