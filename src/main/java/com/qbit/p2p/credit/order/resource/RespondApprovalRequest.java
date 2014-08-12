package com.qbit.p2p.credit.order.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RespondApprovalRequest {
	private String orderId;
	private String partnerId;
	private String comment;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "RespondApprovalRequest{" + "orderId=" + orderId + ", partnerId=" + partnerId + ", comment=" + comment + '}';
	}
}
