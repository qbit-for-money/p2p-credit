package com.qbit.p2p.credit.statistics.resource;

import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
import com.qbit.p2p.credit.statistics.model.GlobalStatistics;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * @author Alexander_Sergeev
 */
@Path("statistics")
@Singleton
public class StatisticsResource {

	@Inject
	private Env env;
	@Inject
	private UserProfileDAO userProfileDAO;
	@Inject
	private OrderDAO orderDAO;
	@Inject
	private StatisticsDAO statisticsDAO;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Statistics getById(@PathParam("id") String id) {
		Statistics statistics = statisticsDAO.find(id);
		return statistics;
	}

	public long getOpenessRating(String publicKey) {
		long openessRating = 0;

		UserPublicProfile user = userProfileDAO.find(publicKey);
		if (user == null) {
			throw new WebApplicationException();
		}
		if ((user.getName() != null) && !user.getName().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if (user.isPassportEnabled()) {
			openessRating = openessRating + 5;
		}
		if (user.isPersonalDataEnabled()) {
			openessRating = openessRating + 5;
		}
		if (user.isMailEnabled() && (user.getMail() != null) && !user.getMail().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if (user.isPhoneEnabled() && (user.getPhone() != null) && !user.getPhone().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if ((user.getBkiData() != null) && !user.getBkiData().isEmpty()) {
			openessRating = openessRating + 5;
		}
		if (user.getVideos() != null) {
			openessRating = openessRating + user.getVideos().size() * 2;
		}
		if (user.getPhones() != null) {
			openessRating = openessRating + user.getPhones().size() * 3;
		}
		if (user.getSocialLinks() != null) {
			openessRating = openessRating + user.getSocialLinks().size() * 3;
		}
		if (user.getNamesLinks() != null) {
			openessRating = openessRating + user.getNamesLinks().size() * 3;
		}
		return openessRating;
	}

	public long getSummaryRating(String publicKey) {

		long allTransactions = 0;
		SearchRequest filter = new SearchRequest();
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		allTransactions = orderDAO.getLengthWithFilter(publicKey, filter, null);
		return (long) (getOpenessRating(publicKey) * env.getUserOpenessRatingFactor())
				+ (long) (allTransactions * env.getUserAllTransactionsFactor());
	}

	public Statistics getUserOrdersStatistics(String publicKey) {
		Statistics statistic = new Statistics(publicKey);
		long transactionsRating = 0;
		long allOrders = 0;
		long allTransactions = 0;
		long allSuccessTransactions = 0;

		long openessRating = 0;
		UserPublicProfile user = userProfileDAO.find(publicKey);
		if (user == null) {
			return null;
		}
		openessRating = getOpenessRating(publicKey);
		SearchRequest filter = new SearchRequest();
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterValue("SUCCESS");
		filterItem.setFilterOperator(FilterOperator.AND);
		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filter.setFilterItems(Arrays.asList(filterItem));
		long successLength = orderDAO.getLengthWithFilter(publicKey, filter, null);

		filterItem.setFilterValue("NOT_SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long notSuccessLength = orderDAO.getLengthWithFilter(publicKey, filter, null);
		transactionsRating = successLength - notSuccessLength;
		statistic.setTransactionsRating(transactionsRating);
		allOrders = orderDAO.getLengthWithFilter(publicKey, null, null);

		statistic.setOrdersSumValue(allOrders);

		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		allTransactions = orderDAO.getLengthWithFilter(publicKey, filter, null);

		statistic.setTransactionsSum(allTransactions);
		statistic.setSummaryRating((long) (openessRating * env.getUserOpenessRatingFactor()) + (long) (allTransactions * env.getUserAllTransactionsFactor()));

		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		allSuccessTransactions = orderDAO.getLengthWithFilter(publicKey, filter, null);

		statistic.setSuccessTransactionsSum(allSuccessTransactions);

		return statistic;
	}

	public GlobalStatistics getGlobalStatistics() {
		GlobalStatistics statistics = new GlobalStatistics();
		SearchRequest filter = new SearchRequest();
		long allUsersTransactions = 0;
		long allUsersSuccessTransactions = 0;
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		allUsersTransactions = orderDAO.getLengthWithFilter(null, filter, null);

		statistics.setAllTransactionsSum(allUsersTransactions);

		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		allUsersSuccessTransactions = orderDAO.getLengthWithFilter(null, filter, null);

		statistics.setAllSuccessTransactionsSum(allUsersSuccessTransactions);

		return statistics;
	}
}
