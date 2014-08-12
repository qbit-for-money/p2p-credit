package com.qbit.p2p.credit.statistics.model;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
	private long ordersRating;
	private long ordersCount;
	private long successOrdersCount;
	private long partnersRating;
	
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

	public long getOrdersRating() {
		return ordersRating;
	}

	public void setOrdersRating(long ordersRating) {
		this.ordersRating = ordersRating;
	}

	public long getOrdersCount() {
		return ordersCount;
	}

	public void setOrdersCount(long ordersCount) {
		this.ordersCount = ordersCount;
	}

	public long getSuccessOrdersCount() {
		return successOrdersCount;
	}

	public void setSuccessOrdersCount(long successOrdersCount) {
		this.successOrdersCount = successOrdersCount;
	}

	public long getPartnersRating() {
		return partnersRating;
	}

	public void setPartnersRating(long partnersRating) {
		this.partnersRating = partnersRating;
	}

	@Override
	public String toString() {
		return "Statistics{" + "id=" + id + ", opennessRating=" + opennessRating + ", ordersRating=" + ordersRating + ", ordersCount=" + ordersCount + ", successOrdersCount=" + successOrdersCount + ", partnersRating=" + partnersRating + '}';
	}
}
