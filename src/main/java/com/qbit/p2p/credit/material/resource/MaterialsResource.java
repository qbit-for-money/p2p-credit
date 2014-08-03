package com.qbit.p2p.credit.material.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.p2p.credit.material.dao.MaterialDAO;
import com.qbit.p2p.credit.material.model.Material;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("materials")
@Singleton
public class MaterialsResource {
	
	@Context
	private HttpServletRequest request;
	
	@Inject
	private MaterialDAO materialDAO;
	
	@GET
	@Path("byUser")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Material> getByUser(@QueryParam("userId") String userId, @QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return materialDAO.findByUser(userId, offset, limit);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Material create(Material material) {
		String userId = AuthFilter.getUserId(request);
		if (userId == null) {
			return null;
		}
		material.setUserId(userId);
		return materialDAO.create(material);
	}
}
