package com.qbit.p2p.credit.order.model;

/**
 * @author Alexander_Sergeev
 */
public class OrderStatusArrayValueProvider implements ValueProvider {

	@Override
	public OrderStatus[] get(String value) {
		
		String[] statusesStr = value.split(",");
		OrderStatus[] statuses = new OrderStatus[statusesStr.length];
		for(int i = 0; i <  statusesStr.length; i++) {
			statuses[i] = OrderStatus.valueOf(statusesStr[i]);
		}
		return statuses;
	}
}
