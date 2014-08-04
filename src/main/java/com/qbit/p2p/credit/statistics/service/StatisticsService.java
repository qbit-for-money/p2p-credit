package com.qbit.p2p.credit.statistics.service;

import com.qbit.p2p.credit.order.dao.OrderDAO;
import com.qbit.p2p.credit.order.model.FilterCondition;
import com.qbit.p2p.credit.order.model.FilterItem;
import com.qbit.p2p.credit.order.model.SearchRequest;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
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
	public final int PERSONAL_DATA_RATING = 5;
	public final int VIDEOS_RATING = 3;
	public final int SOCIAL_LINKS_RATING = 2;
	
	@Inject
	private UserProfileDAO userProfileDAO;
	@Inject
	private StatisticsDAO statisticsDAO;
	@Inject
	private OrderDAO orderDAO;

	public long recalculateOpenessRating(String publicKey) {
		UserPublicProfile user = userProfileDAO.find(publicKey);
		if (user == null) {
			throw new WebApplicationException();
		}
		
		long openessRating = 0;
		if ((user.getName() != null) && !user.getName().isEmpty()) {
			openessRating += NAME_RATING;
		}
		if (user.isPassportEnabled()) {
			openessRating += PASSPORT_ENABLED_RATING;
		}
		if (user.isPersonalDataEnabled()) {
			openessRating += PERSONAL_DATA_RATING;
		}
		if (user.isMailEnabled() && (user.getMail() != null) && !user.getMail().isEmpty()) {
			openessRating += MAIL_RATING;
		}
		if (user.isPhoneEnabled() && (user.getPhone() != null) && !user.getPhone().isEmpty()) {
			openessRating += PHONE_RATING;
		}
		if (user.getVideos() != null) {
			openessRating += user.getVideos().size() * VIDEOS_RATING;
		}
		if (user.getSocialLinks() != null) {
			openessRating += user.getSocialLinks().size() * SOCIAL_LINKS_RATING;
		}
		
		statisticsDAO.updateOpennessRating(user.getPublicKey(), openessRating);
		
		return openessRating;
	}

	public Statistics recalculateUserOrdersStatistics(String publicKey) {
		UserPublicProfile user = userProfileDAO.find(publicKey);
		if (user == null) {
			return null;
		}
		
		Statistics statistics = new Statistics(publicKey);
		SearchRequest filter = new SearchRequest();
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long successLength = orderDAO.lengthWithFilter(publicKey, filter, null);
		filterItem.setFilterValue("NOT_SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long notSuccessLength = orderDAO.lengthWithFilter(publicKey, filter, null);
		statistics.setOrdersRating(successLength - notSuccessLength);
		
		statistics.setOrdersValue(0); // TODO

		filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		long transactionsCount = orderDAO.lengthWithFilter(publicKey, filter, null);
		statistics.setOrdersCount(transactionsCount);

		filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long allSuccessTransactions = orderDAO.lengthWithFilter(publicKey, filter, null);
		statistics.setSuccessOrdersCount(allSuccessTransactions);

		statisticsDAO.updateUserOrdersStatistics(statistics);
		
		return statistics;
	}

	public GlobalStatistics recalculateGlobalStatistics() {
		GlobalStatistics statistics = new GlobalStatistics();
		
		SearchRequest filter = new SearchRequest();
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem));
		long allOrdersCount = orderDAO.lengthWithFilter(filter);
		statistics.setAllOrdersCount(allOrdersCount);

		filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem));
		long allSuccessOrdersCount = orderDAO.lengthWithFilter(filter);
		statistics.setAllSuccessOrdersCount(allSuccessOrdersCount);

		statisticsDAO.updateGlobalStatistics(statistics);
		
		return statistics;
	}
}
