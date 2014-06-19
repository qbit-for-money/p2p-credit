package com.qbit.p2p.credit.user.model;

import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import java.io.Serializable;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
public class UserCurrency implements Serializable {
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private Currency currency;
	private int startValue;
	private int endValue;

	public UserCurrency() {
	}

	public UserCurrency(Currency currency, int startValue, int endValue) {
		this.currency = currency;
		this.startValue = startValue;
		this.endValue = endValue;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
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
