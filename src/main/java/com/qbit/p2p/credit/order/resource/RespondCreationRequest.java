package com.qbit.p2p.credit.order.resource;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexander_Sergeev
 */
@XmlRootElement
public class RespondCreationRequest {

	private String orderId;
	private String comment;
	private String userId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "ResponseRequest{" + "orderId=" + orderId + ", comment=" + comment + ", userId=" + userId + '}';
	}
}
