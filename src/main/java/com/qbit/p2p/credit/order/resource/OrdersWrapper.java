package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.statistics.dao.StatisticsDAO;
import com.qbit.p2p.credit.statistics.model.Statistics;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrdersWrapper {

	@XmlElement
	@XmlList
	private List<OrderWrapper> orderWrappers;
	@XmlElement
	private long length;
	@Inject
	private StatisticsDAO statisticsDAO;

	public OrdersWrapper() {
	}

	public OrdersWrapper(List<OrderInfo> orders, long length) {
		orderWrappers = new ArrayList<>();
		for (OrderInfo order : orders) {
			OrderWrapper wrapper = new OrderWrapper(order);
			Statistics statistics = statisticsDAO.find(order.getUserId());
			wrapper.setStatistics(statistics);
			orderWrappers.add(wrapper);
		}
		this.length = length;
	}
	
	public void setOrderWrappers(List<OrderWrapper> orderWrappers) {
		this.orderWrappers = orderWrappers;
	}

	public List<OrderWrapper> getOrderWrappers() {
		return orderWrappers;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}
}
