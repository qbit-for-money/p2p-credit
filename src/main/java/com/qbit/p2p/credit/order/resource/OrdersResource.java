package com.qbit.p2p.credit.order.resource;

import com.qbit.commons.auth.AuthFilter;
import static com.qbit.commons.rest.util.RESTUtil.toDate;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.user.model.UserType;
import java.text.ParseException;
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
@Path("orders")
@Singleton
public class OrdersResource {
	
	@Context
	private HttpServletRequest request;
	
	@Inject
	private OrderDAO orderDAO;

	@GET
	@Path("active")
	@Produces(MediaType.APPLICATION_JSON)
	public List<OrderInfo> getByUser(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return orderDAO.findByUser(AuthFilter.getUserId(request), offset, limit);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<OrderInfo> getByTimestamp(@QueryParam("creationDate") String creationDateStr, @QueryParam("offset") int offset, @QueryParam("limit") int limit) throws ParseException {
		return orderDAO.findByUserAndTimestamp(AuthFilter.getUserId(request), toDate(creationDateStr), offset, limit);
	}
	
	@GET
	@Path("by-user-type")
	@Produces(MediaType.APPLICATION_JSON)
	public List<OrderInfo> getByUser(@QueryParam("userType") UserType userType, @QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return orderDAO.findByUserType(userType, offset, limit);
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo create(OrderInfo order) {
		return orderDAO.create(AuthFilter.getUserId(request));
	}
}
