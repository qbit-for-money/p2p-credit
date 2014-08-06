package com.qbit.p2p.credit.order.dao.meta;

/**
 * @author Alexander_Sergeev
 */
public class StringArrayValueProvider implements ValueProvider {
	
	public static final StringArrayValueProvider INST = new StringArrayValueProvider();
	
	private StringArrayValueProvider() {
	}
	
	@Override
	public String[] get(String value) {
		if ((value == null) || value.isEmpty()) {
			return new String[0];
		}
		return value.split("\\s*,\\s*");
	}
}
