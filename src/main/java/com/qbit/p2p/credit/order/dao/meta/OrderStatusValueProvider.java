package com.qbit.p2p.credit.order.dao.meta;

import com.qbit.p2p.credit.order.model.OrderStatus;

/**
 * @author Alexander_Sergeev
 */
public class OrderStatusValueProvider implements ValueProvider {
	
	public static final OrderStatusValueProvider INST = new OrderStatusValueProvider();
	
	private OrderStatusValueProvider() {
	}
	
	@Override
	public OrderStatus get(String value) {
		if ((value == null) || value.isEmpty()) {
			return null;
		}
		return OrderStatus.valueOf(value.toUpperCase());
	}
}
