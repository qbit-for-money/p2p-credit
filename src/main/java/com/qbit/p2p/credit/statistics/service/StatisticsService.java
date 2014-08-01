package com.qbit.p2p.credit.statistics.service;

import com.qbit.p2p.credit.env.Env;
import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.FilterOperator;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.statistics.model.GlobalStatistics;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.dao.UserProfileDAO;
import com.qbit.p2p.credit.user.model.UserPublicProfile;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class StatisticsService {

	public final int NAME_RATING = 5;
	public final int PASSPORT_ENABLED_RATING = 7;
	public final int MAIL_RATING = 4;
	public final int PHONE_RATING = 5;
	public final int BKI_RATING = 6;
	public final int PERSONAL_DATA_RATING = 5;
	public final int VIDEOS_RATING = 3;
	public final int SOCIAL_LINKS_RATING = 2;
	@Inject
	private UserProfileDAO userProfileDAO;
	@Inject
	private Env env;
	@Inject
	private OrderDAO orderDAO;

	public long getOpenessRating(String publicKey) {
		UserPublicProfile user = userProfileDAO.find(publicKey);
		if (user == null) {
			throw new WebApplicationException();
		}
		long openessRating = 0;
		if ((user.getName() != null) && !user.getName().isEmpty()) {
			openessRating = openessRating + NAME_RATING;
		}
		if (user.isPassportEnabled()) {
			openessRating = openessRating + PASSPORT_ENABLED_RATING;
		}
		if (user.isPersonalDataEnabled()) {
			openessRating = openessRating + PERSONAL_DATA_RATING;
		}
		if (user.isMailEnabled() && (user.getMail() != null) && !user.getMail().isEmpty()) {
			openessRating = openessRating + MAIL_RATING;
		}
		if (user.isPhoneEnabled() && (user.getPhone() != null) && !user.getPhone().isEmpty()) {
			openessRating = openessRating + PHONE_RATING;
		}
		if ((user.getBkiData() != null) && !user.getBkiData().isEmpty()) {
			openessRating = openessRating + BKI_RATING;
		}
		if (user.getVideos() != null) {
			openessRating = openessRating + user.getVideos().size() * VIDEOS_RATING;
		}
		if (user.getSocialLinks() != null) {
			openessRating = openessRating + user.getSocialLinks().size() * SOCIAL_LINKS_RATING;
		}
		return openessRating;
	}

	public long getSummaryRating(String publicKey) {	
		SearchRequest filter = new SearchRequest();
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		long allTransactions = orderDAO.getLengthWithFilter(publicKey, filter, null);
		return (long) (getOpenessRating(publicKey) * env.getUserRatingOpenessFactor())
			+ (long) (allTransactions * env.getUserRatingTransactionsFactor());
	}

	public Statistics calculateUserOrdersStatistics(String publicKey) {
		Statistics statistic = new Statistics(publicKey);

		UserPublicProfile user = userProfileDAO.find(publicKey);
		if (user == null) {
			return null;
		}
		long openessRating = getOpenessRating(publicKey);
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
		long transactionsRating = successLength - notSuccessLength;
		statistic.setTransactionsRating(transactionsRating);
		long allOrders = orderDAO.getLengthWithFilter(publicKey, null, null);

		statistic.setOrdersValue(allOrders);

		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		long transactionsCount = orderDAO.getLengthWithFilter(publicKey, filter, null);

		statistic.setTransactionsCount(transactionsCount);
		statistic.setSummaryRating((long) (openessRating * env.getUserRatingOpenessFactor()) + (long) (transactionsCount * env.getUserRatingTransactionsFactor()));

		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long allSuccessTransactions = orderDAO.getLengthWithFilter(publicKey, filter, null);

		statistic.setSuccessTransactionsCount(allSuccessTransactions);

		return statistic;
	}

	public GlobalStatistics calculateGlobalStatistics() {
		SearchRequest filter = new SearchRequest();
		
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		long allUsersTransactions = orderDAO.getLengthWithFilter(null, filter, null);
		GlobalStatistics statistics = new GlobalStatistics();
		statistics.setAllTransactionsCount(allUsersTransactions);

		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long allUsersSuccessTransactions = orderDAO.getLengthWithFilter(null, filter, null);

		statistics.setAllSuccessTransactionsCount(allUsersSuccessTransactions);

		return statistics;
	}
}
