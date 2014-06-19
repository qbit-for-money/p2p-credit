package com.qbit.p2p.credit.commons.model;

/**
 * @author Alexander_Sergeev
 */
public enum Currency {
	
	YANDEX_RUB("RUR", 400), 
	BITCOIN("BTC", 20),
	LITECOIN("LTC", 300);
	
	private final String code;
	private final int maxValue;
	private Currency(String code, int maxValue) {
		this.code = code;
		this.maxValue = maxValue;
	}
	
	public String getCode() {
		return code;
	}
	
	public int getMaxValue() {
		return maxValue;
	}
}
