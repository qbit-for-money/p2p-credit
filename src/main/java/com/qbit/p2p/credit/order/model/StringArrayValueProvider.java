package com.qbit.p2p.credit.order.model;

/**
 * @author Alexander_Sergeev
 */
public class StringArrayValueProvider implements ValueProvider {
	@Override
	public String[] get(String value) {
		return value.split(",");
	}
}
