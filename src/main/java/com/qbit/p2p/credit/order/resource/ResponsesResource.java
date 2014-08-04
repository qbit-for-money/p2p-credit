package com.qbit.p2p.credit.order.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.p2p.credit.order.dao.OrderDAO;
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

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo createRespond(RespondCreationRequest respondRequest) {
		String id = AuthFilter.getUserId(request);
		if ((respondRequest == null) || (respondRequest.getOrderId() == null) || respondRequest.getOrderId().isEmpty()) {
			return null;
		}
		OrderInfo order = orderDAO.find(respondRequest.getOrderId());
		if ((order == null) || order.getUserId().equals(id)) {
			return null;
		}
		if (order.getResponses() != null) {
			for (Respond orderResponse : order.getResponses()) {
				if (orderResponse.getUserId().equals(id)) {
					return null;
				}
			}
		}

		Respond respond = new Respond();
		respond.setUserId(id);
		respond.setCreationDate(new Date());
		respond.setComment(respondRequest.getComment());
		List<Respond> responses = order.getResponses();
		if (responses == null) {
			responses = new ArrayList<>();
			order.setResponses(responses);
		}
		responses.add(respond);
		OrderInfo o = orderDAO.update(order);
		return o;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo approveRespond(RespondCreationRequest respondRequest) {
		OrderInfo order = orderDAO.find(respondRequest.getOrderId());
		String id = AuthFilter.getUserId(request);
		if ((order == null) || !order.getUserId().equals(id)) {
			return null;
		}
		if (order.getResponses() != null) {
			for (Respond respond : order.getResponses()) {
				if (respond.getUserId().equals(respondRequest.getUserId()) && (order.getApprovedRespondId() == null)) {
					order.setApprovedRespondId(respond.getId());

					order.setStatus(OrderStatus.IN_PROCESS);
					orderDAO.update(order);
				}
			}
		}
		return null;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Respond getRespondByUser(@QueryParam("userId") String userId) {
		return orderDAO.findRespond(userId);
	}
}
