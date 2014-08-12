package com.qbit.p2p.credit.user.dao;

import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.user.model.Language;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class LanguageDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	public Language create(final String code) {
		if ((code == null) || code.isEmpty()) {
			throw new IllegalArgumentException("Code is empty.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Language>() {

			@Override
			public Language call(EntityManager entityManager) {
				Language language = entityManager.find(Language.class, code);
				if (language != null) {
					return language;
				}
				language = new Language(code);
				language.setCustom(false);
				entityManager.merge(language);
				return language;
			}
		});
	}

	public List<Language> findAll() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Language> criteriaQuery = builder.createQuery(Language.class);
			Root<Language> language = criteriaQuery.from(Language.class);
			criteriaQuery.select(criteriaQuery.from(Language.class)).distinct(true);
			criteriaQuery.where(builder.equal(language.get("custom"), false));
			TypedQuery<Language> query = entityManager.createQuery(criteriaQuery);
			return query.getResultList();
		} finally {
			entityManager.close();
		}
	}
}
