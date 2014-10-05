package com.qbit.p2p.credit.env;

import com.qbit.commons.user.UserDAO;
import com.qbit.p2p.credit.like.dao.LikeDAO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("env")
@Singleton
public class EnvResource {

	@Inject
	private Env env;
	@Inject
	private UserDAO userDAO;
	
	@Inject
	private LikeDAO likeDAO;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Env get() {
		return env;
	}
}
