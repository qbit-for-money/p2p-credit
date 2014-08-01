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
	private StatisticsDAO statisticsDAO;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Statistics getById(@PathParam("id") String id) {
		Statistics statistics = statisticsDAO.find(id);
		return statistics;
	}
	
	@GET
	@Path("global")
	@Produces(MediaType.APPLICATION_JSON)
	public GlobalStatistics getGlobalStatistics() {
		GlobalStatistics statistics = statisticsDAO.getGlobalStatistics();
		return statistics;
	}
}
