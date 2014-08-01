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
	private final long id = 1L;

	private long allTransactionsCount;
	private long allSuccessTransactionsCount;

	public long getId() {
		return id;
	}

	public long getAllTransactionsCount() {
		return allTransactionsCount;
	}

	public void setAllTransactionsCount(long allTransactionsCount) {
		this.allTransactionsCount = allTransactionsCount;
	}

	public long getAllSuccessTransactionsCount() {
		return allSuccessTransactionsCount;
	}

	public void setAllSuccessTransactionsCount(long allSuccessTransactionsCount) {
		this.allSuccessTransactionsCount = allSuccessTransactionsCount;
	}

	@Override
	public String toString() {
		return "GlobalStatistics{" + "id=" + id + ", allTransactionsSum=" + allTransactionsCount + ", allSuccessTransactionsSum=" + allSuccessTransactionsCount + '}';
	}
}
