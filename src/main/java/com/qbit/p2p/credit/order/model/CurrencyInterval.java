package com.qbit.p2p.credit.order.model;

import javax.persistence.Embeddable;

/**
 * @author Alexander_Sergeev
 */
@Embeddable
public class CurrencyInterval {
	private int startValue;
	private int endValue;

	public CurrencyInterval() {
	}

	public CurrencyInterval(int startValue, int endValue) {
		this.startValue = startValue;
		this.endValue = endValue;
	}

	public int getStartValue() {
		return startValue;
	}

	public void setStartValue(int startValue) {
		this.startValue = startValue;
	}

	public int getEndValue() {
		return endValue;
	}

	public void setEndValue(int endValue) {
		this.endValue = endValue;
	}
}
