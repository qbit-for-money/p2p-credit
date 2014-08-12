package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.dao.CategoryDAO;
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
		return new CategoriesWrapper(categoryDAO.findAll());
	}
}
