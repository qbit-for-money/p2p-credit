package com.qbit.p2p.credit.statistics.service;

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
		statisticsDAO.updateOpennessRating(user.getUserId(), openessRating);
		return openessRating;
	}

	public Statistics recalculateUserOrdersStatistics(String userId) {
		UserPublicProfile user = userProfileDAO.find(userId);
		if (user == null) {
			throw new WebApplicationException();
		}
		
		Statistics statistics = new Statistics(userId);
		SearchRequest filter = new SearchRequest();
		FilterItem filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterCondition(FilterCondition.EQUAL);
		filterItem.setFilterValue("SUCCESS");
		
		FilterItem userIdFilterItem = new FilterItem();
		userIdFilterItem.setFilterDataField("userId");
		userIdFilterItem.setFilterCondition(FilterCondition.EQUAL);
		userIdFilterItem.setFilterValue(userId);
		userIdFilterItem.setFilterOperator(FilterOperator.AND);
		filter.setFilterItems(Arrays.asList(filterItem, userIdFilterItem));
		long successLength = orderDAO.lengthWithFilter(filter);
		FilterItem partnerIdFilterItem = new FilterItem();
		partnerIdFilterItem.setFilterDataField("partnerId");
		partnerIdFilterItem.setFilterCondition(FilterCondition.EQUAL);
		partnerIdFilterItem.setFilterValue(userId);
		partnerIdFilterItem.setFilterOperator(FilterOperator.AND);
		filter.setFilterItems(Arrays.asList(filterItem, partnerIdFilterItem));
		successLength += orderDAO.lengthWithFilter(filter);
		statistics.setSuccessOrdersCount(successLength);
		
		filterItem.setFilterValue("NOT_SUCCESS");
		filter.setFilterItems(Arrays.asList(filterItem, userIdFilterItem));
		long notSuccessLength = orderDAO.lengthWithFilter(filter);
		
		filter.setFilterItems(Arrays.asList(filterItem, partnerIdFilterItem));
		notSuccessLength += orderDAO.lengthWithFilter(filter);
		
		statistics.setOrdersRating(successLength - notSuccessLength);

		filterItem = new FilterItem();
		filterItem.setFilterDataField("status");
		filterItem.setFilterCondition(FilterCondition.NOT_EQUAL);
		filterItem.setFilterValue("OPENED");
		filter.setFilterItems(Arrays.asList(filterItem, userIdFilterItem));
		long ordersCount = orderDAO.lengthWithFilter(filter);
		filter.setFilterItems(Arrays.asList(filterItem, partnerIdFilterItem));
		ordersCount += orderDAO.lengthWithFilter(filter);
		statistics.setOrdersCount(ordersCount);

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
	
	public long recalculatePartnersRating(String userId) {
		UserPublicProfile user = userProfileDAO.find(userId);
		if (user == null) {
			throw new WebApplicationException();
		}
		long partnersRating = statisticsDAO.calculatePartnersRating(userId);
		statisticsDAO.updatePartnersRating(userId, partnersRating);
		return partnersRating;
	}
}
