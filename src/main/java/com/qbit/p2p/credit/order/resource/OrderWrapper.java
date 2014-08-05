package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.OrderInfo;
import com.qbit.p2p.credit.statistics.model.Statistics;
import com.qbit.p2p.credit.user.model.ShortProfile;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Alexander_Sergeev
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderWrapper {

	private OrderInfo order;
	private ShortProfile shortProfile;
	private Statistics statistics;

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

	public ShortProfile getShortProfile() {
		return shortProfile;
	}

	public void setShortProfile(ShortProfile shortProfile) {
		this.shortProfile = shortProfile;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}

	@Override
	public String toString() {
		return "OrderWrapper{" + "order=" + order + ", shortProfile=" + shortProfile + ", statistics=" + statistics + '}';
	}
}
