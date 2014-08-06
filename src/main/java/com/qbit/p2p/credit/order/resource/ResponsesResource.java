package com.qbit.p2p.credit.order.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.dao.OrderFlowDAO;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.Respond;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("responses")
@Singleton
public class ResponsesResource {

	@Context
	private HttpServletRequest request;

	@Inject
	private OrderDAO orderDAO;
	
	@Inject
	private OrderFlowDAO orderFlowDAO;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo createRespond(RespondCreationRequest respondRequest) {
		String userId = AuthFilter.getUserId(request);
		if ((respondRequest == null) || (respondRequest.getOrderId() == null) || respondRequest.getOrderId().isEmpty()) {
			return null;
		}
		Respond respond = new Respond();
		respond.setUserId(userId);
		respond.setCreationDate(new Date());
		respond.setComment(respondRequest.getComment());
		OrderInfo o = orderFlowDAO.addRespond(respond, respondRequest.getOrderId());
		return o;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo approveRespond(RespondCreationRequest respondRequest) {
		OrderInfo order = orderDAO.find(respondRequest.getOrderId());
		String userId = AuthFilter.getUserId(request);
		if ((order == null) || !order.getUserId().equals(userId)) {
			return null;
		}
		if (order.getResponses() != null) {
			for (Respond respond : order.getResponses()) {
				if (respond.getUserId().equals(respondRequest.getUserId()) && (order.getApprovedUserId() == null)) {
					order.setApprovedUserId(respond.getUserId());
					order.setStatus(OrderStatus.IN_PROCESS);
					int numberOfEntities = orderFlowDAO.changeStatus(order.getId(), userId, OrderStatus.IN_PROCESS,respondRequest.getUserId(), null);
					return (numberOfEntities == 0) ? null : order;
				}
			}
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Respond getRespondByUser(@QueryParam("userId") String userId) {
		return orderFlowDAO.findRespond(userId);
	}
}
