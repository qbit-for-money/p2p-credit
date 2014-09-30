package com.qbit.p2p.credit.order.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.xss.util.XSSRequestFilter;
import com.qbit.p2p.credit.order.dao.OrderFlowDAO;
import com.qbit.p2p.credit.order.model.Comment;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
	private OrderFlowDAO orderFlowDAO;

	@Inject
	private UserProfileDAO userProfileDAO;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo createRespond(RespondCreationRequest respondRequest) {
		String userId = AuthFilter.getUserId(request);
		if ((respondRequest == null) || (respondRequest.getOrderId() == null) || respondRequest.getOrderId().isEmpty()) {
			return null;
		}
		if (userProfileDAO.find(userId) == null) {
			userProfileDAO.create(userId);
		}
		Respond respond = respondRequest.toRespond();
		respond.setComment(XSSRequestFilter.stripXSS(respond.getComment()));
		respond.setUserId(userId);
		OrderInfo o = orderFlowDAO.addRespond(respond, respondRequest.getOrderId());
		return o;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public int approveRespond(RespondApprovalRequest respondRequest) {
		String userId = AuthFilter.getUserId(request);
		Comment comment = new Comment(userId, respondRequest.getComment());
		System.out.println("!!! APPROVE: " + respondRequest);
		return orderFlowDAO.approveRespond(respondRequest.getOrderId(), userId, respondRequest.getPartnerId(), comment);
	}
}
