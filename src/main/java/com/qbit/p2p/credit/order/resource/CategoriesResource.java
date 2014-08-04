package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.dao.CategoryDAO;
import com.qbit.p2p.credit.order.model.Category;
import com.qbit.p2p.credit.order.model.CategoryType;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("categories")
@Singleton
public class CategoriesResource {
	
	@Inject
	private CategoryDAO categoryDAO;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CategoriesWrapper getAllCategories() {
		return new CategoriesWrapper(categoryDAO.findAllCategories());
	}

	@GET
	@Path("test-category")
	@Produces(MediaType.APPLICATION_JSON)
	public Category createTestCategories() {
		String c = "Кредит под процент";
		categoryDAO.createCategory(c, CategoryType.CREDIT);
		c = "Кредит под залог";
		categoryDAO.createCategory(c, CategoryType.CREDIT);
		c = "Кредит на образование";
		categoryDAO.createCategory(c, CategoryType.CREDIT);
		c = "Кредит";
		categoryDAO.createCategory(c, CategoryType.CREDIT);
		c = "Обмен";
		categoryDAO.createCategory(c, CategoryType.EXCHANGE);
		c = "Безвозвратно";
		categoryDAO.createCategory(c, CategoryType.CREDIT);
		c = "Без %";
		categoryDAO.createCategory(c, CategoryType.CREDIT);
		c = "Доля";
		return categoryDAO.createCategory(c, CategoryType.BORROW);
	}
}
