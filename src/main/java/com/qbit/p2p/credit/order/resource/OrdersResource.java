package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.commons.auth.AuthFilter;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.Comment;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.statistics.service.StatisticsService;
import java.util.List;
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
@Path("orders")
@Singleton
public class OrdersResource {

	@Context
	private HttpServletRequest request;
	@Inject
	private OrderDAO orderDAO;
	@Inject
	private StatisticsService statisticsService;

	@POST
	@Path("search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper search(SearchRequest ordersRequest) {
		String userId = AuthFilter.getUserId(request);
		List<OrderInfo> orders = orderDAO.findWithFilter(userId, ordersRequest);
		long length = orderDAO.lengthWithFilter(userId, ordersRequest);
		return new OrdersWrapper(orders, length);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo create(OrderInfo order) {
		String userId = AuthFilter.getUserId(request);
		if (userId == null) {
			return null;
		}
		order.setUserId(userId);
		if (!order.isValid()) {
			return null;
		}
		OrderInfo newOrder = orderDAO.create(order);
		statisticsService.recalculateUserOrdersStatistics(userId);
		return newOrder;
	}

	@POST
	@Path("status")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int changeOrderStatus(OrderChangeStatusRequest statusRequest) {
		OrderInfo order = new OrderInfo();
		
		String userId = AuthFilter.getUserId(request);
		
		order.setStatus(statusRequest.getStatus());
		order.setComment(new Comment(statusRequest.getComment()));
		return orderDAO.changeStatus(order, statusRequest.getOrderId(), userId);
	}

	
}
