package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.xss.util.XSSRequestFilter;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.dao.OrderFlowDAO;
import com.qbit.p2p.credit.order.model.Comment;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.service.OrderStatisticsScheduler;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
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
	private OrderFlowDAO orderFlowDAO;
	@Inject
	private StatisticsService statisticsService;
	@Inject
	private StatisticsDAO statisticsDAO;
	@Inject
	private OrderStatisticsScheduler scheduler;

	@POST
	@Path("search")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper search(SearchRequest ordersRequest) {
		final String userId = AuthFilter.getUserId(request);
		
		System.out.println("!! " + ordersRequest);
		List<OrderInfo> orders = orderDAO.findWithFilter(userId, ordersRequest);
		System.out.println("!! " + orders);
		long length = orderDAO.lengthWithFilter(userId, ordersRequest);
		return new OrdersWrapper(orders, length, statisticsDAO);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo create(OrderInfo order) {
		if(order == null) {
			return null;
		}
		order.setDescription(XSSRequestFilter.stripXSS(order.getDescription()));
		order.setOrderData(XSSRequestFilter.stripXSS(order.getOrderData()));
		order.setUserId(XSSRequestFilter.stripXSS(order.getUserId()));
		//
		final String userId = AuthFilter.getUserId(request);
		order.setUserId(userId);
		System.out.println("@@ " + order);
		if (!order.isValid()) {
			return null;
		}
		OrderInfo newOrder = orderDAO.create(order);
		scheduler.putTask(new Runnable() {

			@Override
			public void run() {
				statisticsService.recalculateUserOrdersStatistics(userId);
			}
		}, "recalculateUserOrdersStatistics", userId);
		return newOrder;
	}

	@POST
	@Path("{id}/status")
	@Consumes(MediaType.APPLICATION_JSON)
	public int changeOrderStatus(OrderChangeStatusRequest statusRequest) {
		System.out.println("!! " + statusRequest);
		if ((statusRequest == null) || (statusRequest.getOrderId() == null) || statusRequest.getOrderId().isEmpty()) {
			return 0;
		}
		String userId = AuthFilter.getUserId(request);
		Comment comment = new Comment(userId, statusRequest.getComment());

		if (!statusRequest.isByPartner()) {
			return orderFlowDAO.changeStatus(statusRequest.getOrderId(), userId, statusRequest.getStatus(), comment);
		} else {
			return orderFlowDAO.changeStatusByPartner(statusRequest.getOrderId(), userId, statusRequest.getStatus(), comment);
		}
	}
}
