package com.qbit.p2p.credit.order.model;

import java.util.List;

/**
 * @author Alexander_Sergeev
 */
public class OrdersData {
	private long length;
	private List<OrderInfo> orders;

	public OrdersData() {
	}

	public OrdersData(long length, List<OrderInfo> orders) {
		this.length = length;
		this.orders = orders;
	}

	public OrdersData(long length) {
		this.length = length;
	}

	public OrdersData(List<OrderInfo> orders) {
		this.orders = orders;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public List<OrderInfo> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderInfo> orders) {
		this.orders = orders;
	}
}
