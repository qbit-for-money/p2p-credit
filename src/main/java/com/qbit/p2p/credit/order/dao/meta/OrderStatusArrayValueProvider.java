package com.qbit.p2p.credit.order.dao.meta;

import com.qbit.p2p.credit.order.model.OrderStatus;

/**
 * @author Alexander_Sergeev
 */
public class OrderStatusArrayValueProvider implements ValueProvider {

	public static final OrderStatusArrayValueProvider INST = new OrderStatusArrayValueProvider();
	
	private OrderStatusArrayValueProvider() {
	}
	
	@Override
	public OrderStatus[] get(String value) {
		if ((value == null) || value.isEmpty()) {
			return new OrderStatus[0];
		}
		String[] statuses = value.split("\\s*,\\s*");
		OrderStatus[] result = new OrderStatus[statuses.length];
		for(int i = 0; i <  statuses.length; i++) {
			result[i] = OrderStatus.valueOf(statuses[i].toUpperCase());
		}
		return result;
	}
}
