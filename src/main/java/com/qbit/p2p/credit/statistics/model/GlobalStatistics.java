package com.qbit.p2p.credit.statistics.model;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
public class GlobalStatistics implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@XmlTransient
	private final Long id = 0L;

	private long allTransactionsSum;
	private long allSuccessTransactionsSum;

	public Long getId() {
		return id;
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

	@Override
	public String toString() {
		return "GlobalStatistics{" + "id=" + id + ", allTransactionsSum=" + allTransactionsSum + ", allSuccessTransactionsSum=" + allSuccessTransactionsSum + '}';
	}
}
