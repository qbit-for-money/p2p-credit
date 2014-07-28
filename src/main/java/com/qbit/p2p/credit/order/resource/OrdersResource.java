package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.commons.auth.AuthFilter;
import static com.qbit.commons.rest.util.RESTUtil.toDate;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.util.DateUtil;
import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.Comment;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.OrderCategory;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.order.model.Respond;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
import com.qbit.p2p.credit.statistics.model.GlobalStatistics;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.statistics.resource.StatisticsResource;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.Language;
import com.qbit.p2p.credit.user.model.UserCurrency;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.io.File;
import java.io.IOException;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Alexander_Sergeev
 */
@Path("orders")
@Singleton
public class OrdersResource {

	@XmlRootElement
	public static class CategoriesWrapper {

		@XmlElement
		@XmlList
		private List<OrderCategory> categories;

		public CategoriesWrapper() {
		}

		public CategoriesWrapper(List<OrderCategory> categories) {
			this.categories = categories;
		}

		public List<OrderCategory> getCategories() {
			return categories;
		}
	}

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

		public void setLength(long length) {
			this.length = length;
		}
	}

	public static class OrderWrapper {

		private OrderInfo order;
		private String id;
		private long summaryRating;
		private long opennessRating;
		private long ordersSumValue;
		private long successTransactionsSum;
		private long partnersRating;
		private String successValue;
		private String userName;
		private String userPhone;
		private String userMail;
		@XmlJavaTypeAdapter(CurrencyAdapter.class)
		private List<Currency> userCurrencies;
		private List<String> userLanguages;

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

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public long getSummaryRating() {
			return summaryRating;
		}

		public void setSummaryRating(long summaryRating) {
			this.summaryRating = summaryRating;
		}

		public long getOpennessRating() {
			return opennessRating;
		}

		public void setOpennessRating(long opennessRating) {
			this.opennessRating = opennessRating;
		}

		public long getOrdersSumValue() {
			return ordersSumValue;
		}

		public void setOrdersSumValue(long ordersSumValue) {
			this.ordersSumValue = ordersSumValue;
		}

		public long getSuccessTransactionsSum() {
			return successTransactionsSum;
		}

		public void setSuccessTransactionsSum(long successTransactionsSum) {
			this.successTransactionsSum = successTransactionsSum;
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

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getUserPhone() {
			return userPhone;
		}

		public void setUserPhone(String userPhone) {
			this.userPhone = userPhone;
		}

		public List<Currency> getUserCurrencies() {
			return userCurrencies;
		}

		public void setUserCurrencies(List<Currency> userCurrencies) {
			this.userCurrencies = userCurrencies;
		}

		public List<String> getUserLanguages() {
			return userLanguages;
		}

		public void setUserLanguages(List<String> userLanguages) {
			this.userLanguages = userLanguages;
		}

		public String getUserMail() {
			return userMail;
		}

		public void setUserMail(String userMail) {
			this.userMail = userMail;
		}

		@Override
		public String toString() {
			return "OrderWrapper{" + "order=" + order + ", summaryRating=" + summaryRating + ", opennessRating=" + opennessRating + ", ordersSumValue=" + ordersSumValue + ", successTransactionsSum=" + successTransactionsSum + ", partnersRating=" + partnersRating + ", successValue=" + successValue + ", userName=" + userName + ", userPhone=" + userPhone + ", userCurrencies=" + userCurrencies + ", userLanguages=" + userLanguages + '}';
		}
	}

	@XmlRootElement
	public static class ResponseRequest {

		private String orderId;
		private String comment;
		private String userId;

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		@Override
		public String toString() {
			return "ResponseRequest{" + "orderId=" + orderId + ", comment=" + comment + ", userId=" + userId + '}';
		}
	}

	@XmlRootElement
	public static class OrderStatusRequest {

		private String orderId;
		private String comment;
		private OrderStatus status;

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public OrderStatus getStatus() {
			return status;
		}

		public void setStatus(OrderStatus status) {
			this.status = status;
		}
	}

	@Context
	private HttpServletRequest request;

	@Inject
	private OrderDAO orderDAO;

	@Inject
	private UserProfileDAO profileDAO;

	@Inject
	private StatisticsDAO statisticsDAO;

	@Inject
	private StatisticsResource statisticsResource;

	@Inject
	private Env env;

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

	@POST
	@Path("last")
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper getLast(SearchRequest ordersRequest) {
		writeLastOrders();
		return readLastOrders();
	}

	public void writeLastOrders() {
		SearchRequest ordersRequest = new SearchRequest();
		ordersRequest.setPageNumber(0);
		ordersRequest.setPageSize(4);
		OrdersWrapper wrapper = getOrdersFromDB(ordersRequest, null);
		wrapper.setLength(4);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File(env.getLastOrdersPathFolder() + "LAST_ORDERS.json"), wrapper);
		} catch (IOException ex) {
			throw new WebApplicationException(ex);
		}
	}

	private OrdersWrapper readLastOrders() {
		SearchRequest ordersRequest = new SearchRequest();
		ordersRequest.setPageNumber(0);
		ordersRequest.setPageSize(4);
		ObjectMapper mapper = new ObjectMapper();
		OrdersWrapper wrapper = null;
		try {
			wrapper = mapper.readValue(new File(env.getLastOrdersPathFolder() + "LAST_ORDERS.json"), OrdersWrapper.class);
		} catch (IOException ex) {
			throw new WebApplicationException(ex);
		}
		return wrapper;
	}

	@POST
	@Path("current/withFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper getForCurrentWithFilter(SearchRequest ordersRequest) {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@")) {
			return null;
		}
		if (ordersRequest.getFilterItems() == null) {
			ordersRequest.setFilterItems(new ArrayList<FilterItem>());
		}
		List<OrderInfo> orders = null;
		UserPublicProfile profile = profileDAO.find(userId);

		orders = orderDAO.findWithFilter(userId, ordersRequest, profile);

		long length = orderDAO.getLengthWithFilter(userId, ordersRequest, profile);

		List<OrderWrapper> ordersWrappers = new ArrayList<>();
		for (OrderInfo order : orders) {
			OrderWrapper wrapper = new OrderWrapper(order);
			wrapper.setId(order.getId());
			ordersWrappers.add(wrapper);
		}

		System.out.println(ordersWrappers);
		return new OrdersWrapper(ordersWrappers, length);
	}

	@POST
	@Path("withFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper getWithFilter(SearchRequest ordersRequest) {

		UserPublicProfile profile = null;
		if (AuthFilter.getUserId(request) != null) {
			profile = profileDAO.find(AuthFilter.getUserId(request));
		}

		return getOrdersFromDB(ordersRequest, profile);
	}

	private OrdersWrapper getOrdersFromDB(SearchRequest ordersRequest, UserPublicProfile profile) {

		if (ordersRequest == null) {
			return new OrdersWrapper(null, 0);
		}
		if (ordersRequest.getFilterItems() == null) {
			ordersRequest.setFilterItems(new ArrayList<FilterItem>());
		}

		FilterItem openedOrders = new FilterItem();
		openedOrders.setFilterDataField("status");
		openedOrders.setFilterValue("OPENED");
		openedOrders.setFilterOperator(FilterOperator.AND);
		ordersRequest.getFilterItems().add(openedOrders);

		FilterItem greaterThanCurrentDate = new FilterItem();
		greaterThanCurrentDate.setFilterDataField("endDate");
		greaterThanCurrentDate.setFilterValue(DateUtil.dateToString(new Date()));
		greaterThanCurrentDate.setFilterCondition(FilterCondition.GREATER_THAN_OR_EQUAL);
		greaterThanCurrentDate.setFilterOperator(FilterOperator.AND);
		ordersRequest.getFilterItems().add(greaterThanCurrentDate);

		List<OrderInfo> orders = orderDAO.findWithFilter(null, ordersRequest, profile);

		long length = orderDAO.getLengthWithFilter(null, ordersRequest, profile);

		List<OrderWrapper> ordersWrappers = new ArrayList<>();
		for (OrderInfo order : orders) {
			UserPublicProfile profileValue = profileDAO.find(order.getUserPublicKey());
			Statistics statistics = statisticsResource.getById(order.getUserPublicKey());
			//List<OrderInfo> partnersOrders = orderDAO.findWithFilter(null, ordersRequest, profile);

			OrderWrapper wrapper = new OrderWrapper(order);
			wrapper.setId(order.getId());
			if (statistics != null) {
				wrapper.setSummaryRating(statistics.getSummaryRating());
				wrapper.setOpennessRating(statistics.getOpennessRating());
				wrapper.setSuccessTransactionsSum(statistics.getSuccessTransactionsSum());
				wrapper.setOrdersSumValue(statistics.getOrdersSumValue());
			}
			wrapper.setPartnersRating(orderDAO.getPartnersRating(order.getUserPublicKey()));
			if (profileValue.isPhoneEnabled()) {
				wrapper.setUserPhone(profileValue.getPhone());
			}
			if (profileValue.isMailEnabled()) {
				wrapper.setUserMail(profileValue.getMail());
			}
			if (profileValue.isCurrenciesEnabled()) {
				wrapper.setUserCurrencies(profileValue.getCurrencies());
			}
			if (profileValue.isLanguagesEnabled()) {
				if (profileValue.getLanguages() != null) {
					List<String> userLanguages = new ArrayList<>();
					for (Language language : profileValue.getLanguages()) {
						userLanguages.add(language.getTitle());
					}
					wrapper.setUserLanguages(userLanguages);
				}
			}
			wrapper.setUserName(profileValue.getName());

			String ordersSuccessSizeSum = "";
			if ((profileValue.getCurrencies() != null) && !profileValue.getCurrencies().isEmpty()) {
				for (Currency currency : profileValue.getCurrencies()) {
					long ordersSuccessSize = 0;
					SearchRequest filter = new SearchRequest();
					FilterItem item = new FilterItem();
					item.setFilterDataField("status");
					item.setFilterValue("SUCCESS");
					item.setFilterOperator(FilterOperator.AND);

					FilterItem currencyItem = new FilterItem();
					currencyItem.setFilterDataField("givingCurrency");
					currencyItem.setFilterValue(currency.name());
					item.setFilterOperator(FilterOperator.AND);

					filter.setFilterItems(Arrays.asList(item, currencyItem));
					ordersSuccessSize = orderDAO.getLengthWithFilter(profileValue.getPublicKey(), filter, null);
					ordersSuccessSizeSum += (currency.getCode() + ": " + ordersSuccessSize + " / ");
				}
				ordersSuccessSizeSum = ordersSuccessSizeSum.substring(0, ordersSuccessSizeSum.length() - 3);
			}
			wrapper.setSuccessValue(ordersSuccessSizeSum);
			ordersWrappers.add(wrapper);
		}
		return new OrdersWrapper(ordersWrappers, length);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo create(OrderInfo order) {
		String id = AuthFilter.getUserId(request);
		order.setUserPublicKey(id);

		if (!order.isValid()) {
			return null;
		}
		OrderInfo o = orderDAO.create(order);
		Statistics statistics = statisticsResource.calculateUserOrdersStatistics(id);
		if (statistics != null) {
			statisticsDAO.setUserOrdersStatistics(statistics);
		}
		//profileDAO.setUserOrdersStatistics(statisticsResource.getUserOrdersStatistics(id))
		return o;
	}

	@POST
	@Path("addResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo addResponse(ResponseRequest responseRequest) {
		String id = AuthFilter.getUserId(request);
		if ((responseRequest == null) || (responseRequest.getOrderId() == null) || responseRequest.getOrderId().isEmpty()) {
			return null;
		}
		OrderInfo order = orderDAO.find(responseRequest.getOrderId());
		if ((order == null) || order.getUserPublicKey().equals(id)) {
			return null;
		}
		if (order.getResponses() != null) {
			for (Respond orderResponse : order.getResponses()) {
				if (orderResponse.getUserPublicKey().equals(id)) {
					return null;
				}
			}
		}

		Respond respond = new Respond();
		respond.setUserPublicKey(id);
		respond.setCreationDate(new Date());
		respond.setComment(responseRequest.getComment());
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
	@Path("approveResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo approveResponse(ResponseRequest responseRequest) {
		OrderInfo order = orderDAO.find(responseRequest.getOrderId());
		String id = AuthFilter.getUserId(request);
		if ((order == null) || !order.getUserPublicKey().equals(id)) {
			return null;
		}
		if (order.getResponses() != null) {
			for (Respond respond : order.getResponses()) {
				if (respond.getUserPublicKey().equals(responseRequest.getUserId()) && (order.getApprovedResponseId() == null)) {
					order.setApprovedResponseId(respond.getId());

					order.setStatus(OrderStatus.IN_PROCESS);
					orderDAO.update(order);
				}
			}
		}
		return null;
	}

	@GET
	@Path("response")
	@Produces(MediaType.APPLICATION_JSON)
	public Respond getByUser(@QueryParam("id") String id) {
		return orderDAO.findResponse(id);
	}

	@POST
	@Path("changeStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrderInfo changeOrderStatus(OrderStatusRequest statusRequest) {
		OrderInfo order = orderDAO.find(statusRequest.getOrderId());
		if (order == null) {
			return null;
		}
		String id = AuthFilter.getUserId(request);
		if (!order.getUserPublicKey().equals(id) || isСompleted(order.getStatus()) || !isСompleted(statusRequest.getStatus())) {
			return null;
		}
		order.setStatus(statusRequest.getStatus());
		order.setComment(new Comment(statusRequest.getComment()));
		return orderDAO.update(order);
	}

	private boolean isСompleted(OrderStatus status) {
		return ((status == OrderStatus.SUCCESS)
			|| (status == OrderStatus.NOT_SUCCESS)
			|| (status == OrderStatus.ARBITRATION));
	}

	@GET
	@Path("categories")
	@Produces(MediaType.APPLICATION_JSON)
	public CategoriesWrapper getOrdersCategories() {
		return new CategoriesWrapper(orderDAO.findAllCategories());
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

		UserCurrency c1 = new UserCurrency(Currency.BITCOIN, 10, 500);
		order.setOrderData("DEFAULT");

		List<String> l = new ArrayList<>();
		Collections.addAll(l, "English", "Arabic");
		//order.setLanguages(l);

		return orderDAO.create(order);
	}

	@GET
	@Path("test-category")
	@Produces(MediaType.APPLICATION_JSON)
	public OrderCategory createTestCategories() {
		System.out.println("!!! TEST");
		String c = "Кредит под процент";
		orderDAO.createCategory(c);
		c = "Кредит под залог";
		orderDAO.createCategory(c);
		c = "Кредит на образование";
		orderDAO.createCategory(c);
		c = "Кредит";
		orderDAO.createCategory(c);
		c = "Обмен";
		orderDAO.createCategory(c);
		c = "Безвозвратно";
		orderDAO.createCategory(c);
		c = "Без %";
		orderDAO.createCategory(c);
		c = "Доля";
		return orderDAO.createCategory(c);
	}
}
