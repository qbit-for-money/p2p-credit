package com.qbit.p2p.credit.order.dao;

import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.order.model.CategoryType;
import com.qbit.p2p.credit.order.model.OrderCategory;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class OrderCategoryDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	public OrderCategory createCategory(final String code, final CategoryType type) {
		if ((code == null) || code.isEmpty()) {
			throw new IllegalArgumentException("Code is empty.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<OrderCategory>() {

			@Override
			public OrderCategory call(EntityManager entityManager) {
				OrderCategory category = entityManager.find(OrderCategory.class, code);
				if (category != null) {
					return category;
				}
				category = new OrderCategory(code, type);
				category.setCustom(true);
				entityManager.merge(category);
				return category;
			}
		});
	}

	public List<OrderCategory> findAllCategories() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<OrderCategory> criteria = builder.createQuery(OrderCategory.class);
			criteria.select(criteria.from(OrderCategory.class));
			//criteria.where(builder.equal(category.get("custom"), "t"));
			TypedQuery<OrderCategory> query = entityManager.createQuery(criteria);
			List<OrderCategory> c = query.getResultList();
			return c;
		} finally {
			entityManager.close();
		}
	}
}
