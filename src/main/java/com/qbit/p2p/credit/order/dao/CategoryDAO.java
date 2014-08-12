package com.qbit.p2p.credit.order.dao;

import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.order.model.CategoryType;
import com.qbit.p2p.credit.order.model.Category;
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
public class CategoryDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	public Category create(final String code, final CategoryType type) {
		if ((code == null) || code.isEmpty() || type == null) {
			throw new IllegalArgumentException("Category is not valid.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Category>() {

			@Override
			public Category call(EntityManager entityManager) {
				Category category = entityManager.find(Category.class, code);
				if (category != null) {
					return category;
				}
				category = new Category(code, type);
				category.setCustom(true);
				entityManager.merge(category);
				return category;
			}
		});
	}

	public List<Category> findAll() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
			Root<Category> category = criteria.from(Category.class);
			criteria.select(criteria.from(Category.class)).distinct(true);
			criteria.where(builder.equal(category.get("custom"), false));
			TypedQuery<Category> query = entityManager.createQuery(criteria);
			List<Category> c = query.getResultList();
			return c;
		} finally {
			entityManager.close();
		}
	}
}
