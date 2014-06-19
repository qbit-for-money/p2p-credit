package com.qbit.p2p.credit.order.resource;

import com.qbit.commons.auth.AuthFilter;
import static com.qbit.commons.rest.util.RESTUtil.toDate;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.order.model.OrderStatus;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserCurrency;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import com.qbit.p2p.credit.user.model.UserType;
import java.text.ParseException;
import java.util.ArrayList;
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
		private List<OrderInfo> orders;
		@XmlElement
		long length;

		public OrdersWrapper() {
		}

		public OrdersWrapper(List<OrderInfo> orders, long length) {
			this.orders = orders;
			this.length = length;
		}

		public List<OrderInfo> getOrders() {
			return orders;
		}

		public long getLength() {
			return length;
		}
	}

	public static class OrdersRequest {

		@XmlElement(name = "publicKeyoperator")
		private String publicKeyOperator;
		@XmlElement(name = "filtervalue0")
		private String filterValue0;
		@XmlElement(name = "filtervalue1")
		private String filterValue1;
		@XmlElement(name = "filtervalue2")
		private String filterValue2;
		@XmlElement(name = "filtercondition0")
		private String filterCondition0;
		@XmlElement(name = "filtercondition1")
		private String filterCondition1;
		private String filteroperator0;
		@XmlElement(name = "filterdatafield0")
		private String filterDatafield0;
		@XmlElement(name = "filterdatafield1")
		private String filterDatafield1;

		private String filter;
		@XmlElement(name = "sortorder")
		private String sortOrder;
		private int filterscount;
		@XmlElement(name = "pagenum")
		private int pageNumber;
		@XmlElement(name = "pagesize")
		private int pageSize;
		private int recordstartindex;
		private int recordendindex;
		@XmlElement(name = "sortdatafield")
		private String sortDataField;
		@XmlElement(name = "filterscount")
		private String filtersCount;

		public String getFilteroperator0() {
			return filteroperator0;
		}

		public void setFilteroperator0(String filteroperator0) {
			this.filteroperator0 = filteroperator0;
		}

		public int getFilterscount() {
			return filterscount;
		}

		public void setFilterscount(int filterscount) {
			this.filterscount = filterscount;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getRecordstartindex() {
			return recordstartindex;
		}

		public void setRecordstartindex(int recordstartindex) {
			this.recordstartindex = recordstartindex;
		}

		public int getRecordendindex() {
			return recordendindex;
		}

		public void setRecordendindex(int recordendindex) {
			this.recordendindex = recordendindex;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public String getPublicKeyOperator() {
			return publicKeyOperator;
		}

		public void setPublicKeyOperator(String publicKeyOperator) {
			this.publicKeyOperator = publicKeyOperator;
		}

		public String getFilterValue0() {
			return filterValue0;
		}

		public void setFilterValue0(String filterValue0) {
			this.filterValue0 = filterValue0;
		}

		public String getFilterCondition0() {
			return filterCondition0;
		}

		public void setFilterCondition0(String filterCondition0) {
			this.filterCondition0 = filterCondition0;
		}

		public String getFilterCondition1() {
			return filterCondition1;
		}

		public void setFilterCondition1(String filterCondition1) {
			this.filterCondition1 = filterCondition1;
		}

		public String getFilterDatafield0() {
			return filterDatafield0;
		}

		public void setFilterDatafield0(String filterDatafield0) {
			this.filterDatafield0 = filterDatafield0;
		}

		public String getFilterDatafield1() {
			return filterDatafield1;
		}

		public void setFilterDatafield1(String filterDatafield1) {
			this.filterDatafield1 = filterDatafield1;
		}

		public String getFilterValue1() {
			return filterValue1;
		}

		public void setFilterValue1(String filterValue1) {
			this.filterValue1 = filterValue1;
		}

		public String getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(String sortOrder) {
			this.sortOrder = sortOrder;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getSortDataField() {
			return sortDataField;
		}

		public void setSortDataField(String sortDataField) {
			this.sortDataField = sortDataField;
		}

		public String getFiltersCount() {
			return filtersCount;
		}

		public void setFiltersCount(String filtersCount) {
			this.filtersCount = filtersCount;
		}

		@Override
		public String toString() {
			return "OrdersRequest{" + "publicKeyOperator=" + publicKeyOperator + ", filterValue0=" + filterValue0 + ", filterCondition0=" + filterCondition0 + ", filterCondition1=" + filterCondition1 + ", filteroperator0=" + filteroperator0 + ", filterDatafield0=" + filterDatafield0 + ", filterDatafield1=" + filterDatafield1 + ", filterValue1=" + filterValue1 + ", filter=" + filter + ", sortOrder=" + sortOrder + ", filterscount=" + filterscount + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", recordstartindex=" + recordstartindex + ", recordendindex=" + recordendindex + ", sortDataField=" + sortDataField + ", filtersCount=" + filtersCount + '}';
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
	public List<OrderInfo> getByUserType(@QueryParam("userType") UserType userType, @QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		return orderDAO.findByUserType(userType, offset, limit);
	}

	@POST
	@Path("current/withFilter")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OrdersWrapper getForCurrentWithFilter(OrdersRequest ordersRequest) {
		String userId = AuthFilter.getUserId(request);
		if (!userId.contains("@")) {
			return null;
		}
		boolean sortDesc = false;
		if (ordersRequest.getSortOrder() != null && ordersRequest.getSortOrder().equals("desc")) {
			sortDesc = true;
		}
		List<OrderInfo> orders;
		orders = orderDAO.findWithFilter(null, ordersRequest.getFilterValue0(), ordersRequest.getFilterDatafield0(),
			ordersRequest.getFilterValue1(), ordersRequest.getFilterDatafield1(), ordersRequest.getSortDataField(), sortDesc, ordersRequest.getPageNumber() * ordersRequest.getPageSize(), ordersRequest.getPageSize());
		return new OrdersWrapper(orders, orderDAO.length(null, ordersRequest.getFilterDatafield0(), ordersRequest.getFilter()));
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
		order.setEndDate(new Date(3045));
		order.setReward(rand.nextInt(1000));
		order.setStatus(OrderStatus.OPENED);
		UserPublicProfile profile = profileDAO.find(userId);
		if ((profile != null) && (profile.getName() != null) && !profile.getName().isEmpty()) {
			order.setUserName(profile.getName());
		}

		UserCurrency c1 = new UserCurrency(Currency.BITCOIN, 10, 500);
		UserCurrency c2 = new UserCurrency(Currency.LITECOIN, 10, 500);
		List<UserCurrency> c = new ArrayList<>();
		Collections.addAll(c, c1, c2);
		order.setCurrencies(c);

		List<String> l = new ArrayList<>();
		Collections.addAll(l, "Russian", "English");
		order.setLanguages(l);

		return orderDAO.create(order);
	}
}
