package com.qbit.p2p.credit.order.model;

/**
 * @author Alexander_Sergeev
 */
public class OrderStatusValueProvider implements ValueProvider {
	@Override
	public OrderStatus get(String value) {
		return OrderStatus.valueOf(value);
	}
}
