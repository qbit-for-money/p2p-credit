package com.qbit.p2p.credit.order.resource;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
public class OrdersWrapper {

	@XmlElement
	@XmlList
	private List<OrderWrapper> orders;
	@XmlElement
	private long length;

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
