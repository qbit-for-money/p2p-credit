package com.qbit.p2p.credit.order.model;

/**
 * @author Alexander_Sergeev
 */
public class IntegerValueProvider implements ValueProvider {

	public Integer get(String value) {
		return Integer.parseInt(value);
	}
}
