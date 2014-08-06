package com.qbit.p2p.credit.order.dao.meta;

/**
 * @author Alexander_Sergeev
 */
public class IntegerValueProvider implements ValueProvider {
	
	public static final IntegerValueProvider INST = new IntegerValueProvider();
	
	private IntegerValueProvider() {
	}

	@Override
	public Integer get(String value) {
		return Integer.parseInt(value);
	}
}
