package com.qbit.p2p.credit.user.model;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 * @author Alexander_Sergeev
 */
@Embeddable
public class Statistic implements Serializable {
	private long opennessRating;
	private long transactionsRating;
	private long ordersSumValue;
	private long transactionsSum;
	private long successTransactionsSum;
	private long allTransactionsSum;
	private long allSuccessTransactionsSum;
	private long summaryRating;

	public long getOpennessRating() {
		return opennessRating;
	}

	public void setOpennessRating(long opennessRating) {
		this.opennessRating = opennessRating;
	}

	public long getTransactionsRating() {
		return transactionsRating;
	}

	public void setTransactionsRating(long transactionsRating) {
		this.transactionsRating = transactionsRating;
	}

	public long getOrdersSumValue() {
		return ordersSumValue;
	}

	public void setOrdersSumValue(long ordersSumValue) {
		this.ordersSumValue = ordersSumValue;
	}

	public long getTransactionsSum() {
		return transactionsSum;
	}

	public void setTransactionsSum(long transactionsSum) {
		this.transactionsSum = transactionsSum;
	}

	public long getSuccessTransactionsSum() {
		return successTransactionsSum;
	}

	public void setSuccessTransactionsSum(long successTransactionsSum) {
		this.successTransactionsSum = successTransactionsSum;
	}

	public long getAllTransactionsSum() {
		return allTransactionsSum;
	}

	public void setAllTransactionsSum(long allTransactionsSum) {
		this.allTransactionsSum = allTransactionsSum;
	}

	public long getAllSuccessTransactionsSum() {
		return allSuccessTransactionsSum;
	}

	public void setAllSuccessTransactionsSum(long allSuccessTransactionsSum) {
		this.allSuccessTransactionsSum = allSuccessTransactionsSum;
	}

	public long getSummaryRating() {
		return summaryRating;
	}

	public void setSummaryRating(long summaryRating) {
		this.summaryRating = summaryRating;
	}
	
	@Override
	public String toString() {
		return "Statistic{" + "opennessRating=" + opennessRating + ", transactionsRating=" + transactionsRating + ", ordersSumValue=" + ordersSumValue + ", transactionsSum=" + transactionsSum + ", successTransactionsSum=" + successTransactionsSum + ", allTransactionsSum=" + allTransactionsSum + ", allSuccessTransactionsSum=" + allSuccessTransactionsSum + ", summaryRating=" + summaryRating + '}';
	}
}
