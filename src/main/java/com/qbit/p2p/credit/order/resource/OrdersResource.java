package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.FilterCriteriaValue;
import com.qbit.commons.auth.AuthFilter;
import static com.qbit.commons.rest.util.RESTUtil.toDate;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.CurrencyInterval;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderType;
import com.qbit.p2p.credit.order.model.OrdersData;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserCurrency;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@Path("orders")
@Singleton
public class OrdersResource {

	@XmlRootElement
	public static class OrdersWrapper {

		@XmlElement
		@XmlList
		private List<OrderWrapper> orders;
		@XmlElement
		long length;

		public OrdersWrapper() {
		}

		public OrdersWrapper(List<OrderWrapper> orders, long length) {
			this.orders = orders;
			this.length = length;
		}

		public List<OrderWrapper> getOrders() {
			return orders;
		}

		public long getLength() {
			return length;
		}
	}
	
	public static class OrderWrapper {
		private OrderInfo order;
		private long rating;
		private long success;
		private long partnersRating;
		private String successValue;

		public OrderWrapper() {
		}

		public OrderWrapper(OrderInfo order) {
			this.order = order;
		}

		public OrderInfo getOrder() {
			return order;
		}

		public void setOrder(OrderInfo order) {
			this.order = order;
		}

		public long getRating() {
			return rating;
		}

		public void setRating(long rating) {
			this.rating = rating;
		}

		public long getSuccess() {
			return success;
		}

		public void setSuccess(long success) {
			this.success = success;
		}

		public long getPartnersRating() {
			return partnersRating;
		}

		public void setPartnersRating(long partnersRating) {
			this.partnersRating = partnersRating;
		}

		public String getSuccessValue() {
			return successValue;
		}

		public void setSuccessValue(String successValue) {
			this.successValue = successValue;
		}

		@Override
		public String toString() {
			return "OrderWrapper{" + "order=" + order + ", rating=" + rating + ", success=" + success + ", partnersRating=" + partnersRating + ", successValue=" + successValue + '}';
		}
	}

	@Context
	private HttpServletRequest request;

	@Inject
	private OrderDAO orderDAO;

	@Inject
	private UserProfileDAO profileDAO;

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
	public List<OrderInfo> getByOrderType(@QueryParam("userType") OrderType orderType, @QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return orderDAO.findByType(orderType, offset, limit);
	}

	@POST
	@Path("last")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper getLast(FilterCriteriaValue ordersRequest) {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@")) {
			return null;
		}
		List<OrderInfo> orders = null;
		UserPublicProfile profile = profileDAO.find(userId);
		OrdersData ordersData = orderDAO.findWithFilter(userId, ordersRequest, profile, false);
		if (ordersData != null) {
			orders = ordersData.getOrders();
		}
		List<OrderWrapper> ordersWrappers = new ArrayList<>();
		for(OrderInfo order : orders) {
			//UserPublicProfile profile = profileDAO.find(order.getUserPublicKey());
			OrderWrapper wrapper = new OrderWrapper(order);
			wrapper.setSuccess(profile.getStatistic().getSuccessTransactionsSum());
		}
		
		return new OrdersWrapper(ordersWrappers, 4);
	}

	@POST
	@Path("current/withFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper getForCurrentWithFilter(FilterCriteriaValue ordersRequest) {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@")) {
			return null;
		}
		if (ordersRequest.getFilterItems() == null) {
			ordersRequest.setFilterItems(new ArrayList<FilterCriteriaValue.FilterItem>());
		}
		List<OrderInfo> orders = null;
		UserPublicProfile profile = profileDAO.find(userId);

		OrdersData ordersData = orderDAO.findWithFilter(userId, ordersRequest, profile, false);
		if (ordersData != null) {
			orders = ordersData.getOrders();
		}

		OrdersData ordersSize = orderDAO.findWithFilter(userId, ordersRequest, profile, true);
		long length = 0;
		if (ordersSize != null) {
			length = ordersSize.getLength();
		}
		List<OrderWrapper> ordersWrappers = new ArrayList<>();
		for(OrderInfo order : orders) {
			OrderWrapper wrapper = new OrderWrapper(order);
			ordersWrappers.add(wrapper);
		}
		
		System.out.println(ordersWrappers);
		return new OrdersWrapper(ordersWrappers, length);
	}

	@POST
	@Path("withFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper getWithFilter(FilterCriteriaValue ordersRequest) {
		if (ordersRequest == null) {
			return new OrdersWrapper(null, 0);
		}
		if (ordersRequest.getFilterItems() == null) {
			ordersRequest.setFilterItems(new ArrayList<FilterCriteriaValue.FilterItem>());
		}

		List<OrderInfo> orders = null;
		UserPublicProfile profile = profileDAO.find(AuthFilter.getUserId(request));
		OrdersData ordersData = orderDAO.findWithFilter(null, ordersRequest, profile, false);
		if (ordersData != null) {
			orders = ordersData.getOrders();
		}

		OrdersData ordersSize = orderDAO.findWithFilter(null, ordersRequest, profile, true);
		long length = 0;
		if (ordersSize != null) {
			length = ordersSize.getLength();
		}
		
		List<OrderWrapper> ordersWrappers = new ArrayList<>();
		for(OrderInfo order : orders) {
			UserPublicProfile profileValue = profileDAO.find(order.getUserPublicKey());
			OrderWrapper wrapper = new OrderWrapper(order);
			wrapper.setRating(profileValue.getStatistic().getTransactionsRating());
			wrapper.setSuccess(profileValue.getStatistic().getSuccessTransactionsSum());
			String ordersSuccessSizeSum = "";
			
			for(Currency currency : profileValue.getCurrencies()) {
				long ordersSuccessSize = 0;
				FilterCriteriaValue filter = new FilterCriteriaValue();
				FilterCriteriaValue.FilterItem item = new FilterCriteriaValue.FilterItem();
				item.setFilterDataField("status");
				item.setFilterValue("SUCCESS");
				item.setFilterOperator(FilterOperator.AND);
				
				FilterCriteriaValue.FilterItem currencyItem = new FilterCriteriaValue.FilterItem();
				currencyItem.setFilterDataField("currency");
				currencyItem.setFilterValue(currency.name());
				item.setFilterOperator(FilterOperator.AND);
				
				filter.setFilterItems(Arrays.asList(item, currencyItem));
				OrdersData ordersSuccessSizeData = orderDAO.findWithFilter(profileValue.getPublicKey(), filter, null, true);
				if(ordersSuccessSizeData != null) {
					ordersSuccessSize = ordersSuccessSizeData.getLength();
				}
				ordersSuccessSizeSum += (currency.getCode() + ": " + ordersSuccessSize + " ");
			}
			wrapper.setSuccessValue(ordersSuccessSizeSum);
			//List<UserPublicProfile> responsesProfiles = profileDAO.findByOrder(order.getId(), 0, 0);
			
			//wrapper.setSuccess(profileValue.getStatistic().getSuccessTransactionsSum());
			ordersWrappers.add(wrapper);
		}
		return new OrdersWrapper(ordersWrappers, length);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo create(OrderInfo order) {
		return orderDAO.create(AuthFilter.getUserId(request));
	}

	@GET
	@Path("test")
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo createTest() {
		Random rand = new Random();
		OrderInfo order = new OrderInfo();
		String userId = AuthFilter.getUserId(request);
		order.setUserPublicKey(userId);
		order.setCreationDate(new Date());
		order.setEndDate(new Date());
		order.setReward(String.valueOf(rand.nextInt(1000)));
		UserPublicProfile profile = profileDAO.find(userId);
		if ((profile != null) && (profile.getName() != null) && !profile.getName().isEmpty()) {
			order.setUserName(profile.getName());
		}

		UserCurrency c1 = new UserCurrency(Currency.BITCOIN, 10, 500);
		//UserCurrency c2 = new UserCurrency(Currency.LITECOIN, 10, 500);
		//List<UserCurrency> c = new ArrayList<>();
		//Collections.addAll(c, c1, c2);
		order.setCurrency(Currency.YANDEX_RUB);
		order.setCurrencyInterval(new CurrencyInterval(80, 250));
		order.setType(OrderType.CREDIT);
		order.setTitle("Other");
		order.setOrderData("DEFAULT");

		List<String> l = new ArrayList<>();
		Collections.addAll(l, "English", "Arabic");
		order.setLanguages(l);

		return orderDAO.create(order);
	}
}
