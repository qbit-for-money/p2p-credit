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
public class GlobalStatistics implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@XmlTransient
	private final long id = 1L;

	private long allOrdersCount;
	private long allSuccessOrdersCount;

	public long getId() {
		return id;
	}

	public long getAllOrdersCount() {
		return allOrdersCount;
	}

	public void setAllOrdersCount(long allOrdersCount) {
		this.allOrdersCount = allOrdersCount;
	}

	public long getAllSuccessOrdersCount() {
		return allSuccessOrdersCount;
	}

	public void setAllSuccessOrdersCount(long allSuccessOrdersCount) {
		this.allSuccessOrdersCount = allSuccessOrdersCount;
	}

	@Override
	public String toString() {
		return "GlobalStatistics{" + "id=" + id + ", allOrdersCount=" + allOrdersCount + ", allSuccessOrdersCount=" + allSuccessOrdersCount + '}';
	}
}
