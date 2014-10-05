package com.qbit.p2p.credit.like.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.p2p.credit.like.dao.LikeDAO;
import com.qbit.p2p.credit.like.model.LikeS;
import com.qbit.p2p.credit.like.model.EntityPartId;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Alex
 */
@Path("likes")
@Singleton
public class LikesResource {

	@Inject
	private LikeDAO likeDAO;
	
	@Context
	private HttpServletRequest request;
	
	@GET
	@Path("{type}/{id}/{field}")
	@Produces(MediaType.APPLICATION_JSON)
	public LikeS get(@PathParam("type") String entityType, @PathParam("id") String entityId,  @PathParam("field") String entityField)
			throws ClassNotFoundException {
		return likeDAO.find(new EntityPartId(entityType, entityId, entityField));
	}
	
	@PUT
	@Path("{type}/{id}/{field}")
	@Produces(MediaType.APPLICATION_JSON)
	public LikeS like(@PathParam("type") String entityType, @PathParam("id") String entityId,  @PathParam("field") String entityField)
			throws ClassNotFoundException {
		String userPublicKey = AuthFilter.getUserId(request);
		if(!isNotCaptcha(userPublicKey)) {
			return null;
		}
		if("user.model.UserPublicProfile".equals(entityType) && userPublicKey.equals(entityId)) {
			return null;
		}
		return likeDAO.like(userPublicKey, new EntityPartId(entityType, entityId, entityField));
	}
	
	@DELETE
	@Path("{type}/{id}/{field}")
	@Produces(MediaType.APPLICATION_JSON)
	public LikeS dislike(@PathParam("type") String entityType, @PathParam("id") String entityId,  @PathParam("field") String entityField)
			throws ClassNotFoundException {
		String userPublicKey = AuthFilter.getUserId(request);
		if(!isNotCaptcha(userPublicKey)) {
			return null;
		}
		if("user.model.UserPublicProfile".equals(entityType) && userPublicKey.equals(entityId)) {
			return null;
		}
		return likeDAO.dislike(userPublicKey, new EntityPartId(entityType, entityId, entityField));
	}
	
	private boolean isNotCaptcha(String userId) {
		return (userId != null) && !userId.isEmpty() && (userId.contains("vk-") || userId.contains("@") || userId.contains("fb-"));
	}
}
