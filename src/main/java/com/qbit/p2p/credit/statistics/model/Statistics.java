package com.qbit.p2p.credit.statistics.model;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Alexander_Sergeev
 */
@Entity
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Statistics implements Serializable {

	@Id
	private String id;
	private long opennessRating;
	private long transactionsRating;
	private long ordersValue;
	private long transactionsCount;
	private long successTransactionsCount;
	private long summaryRating;

	public Statistics() {
	}

	public Statistics(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public long getOrdersValue() {
		return ordersValue;
	}

	public void setOrdersValue(long ordersValue) {
		this.ordersValue = ordersValue;
	}

	public long getTransactionsCount() {
		return transactionsCount;
	}

	public void setTransactionsCount(long transactionsCount) {
		this.transactionsCount = transactionsCount;
	}

	public long getSuccessTransactionsCount() {
		return successTransactionsCount;
	}

	public void setSuccessTransactionsCount(long successTransactionsCount) {
		this.successTransactionsCount = successTransactionsCount;
	}

	public long getSummaryRating() {
		return summaryRating;
	}

	public void setSummaryRating(long summaryRating) {
		this.summaryRating = summaryRating;
	}

	@Override
	public String toString() {
		return "Statistics{" + "id=" + id + ", opennessRating=" + opennessRating + ", transactionsRating=" + transactionsRating + ", ordersSumValue=" + ordersValue + ", transactionsSum=" + transactionsCount + ", successTransactionsSum=" + successTransactionsCount + ", summaryRating=" + summaryRating + '}';
	}
}
