package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.OrderStatus;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderChangeStatusRequest {

	private String orderId;
	private String comment;
	private OrderStatus status;
	private boolean byPartner;

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

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public boolean isByPartner() {
		return byPartner;
	}

	public void setByPartner(boolean byPartner) {
		this.byPartner = byPartner;
	}

	@Override
	public String toString() {
		return "OrderChangeStatusRequest{" + "orderId=" + orderId + ", comment=" + comment + ", status=" + status + ", byPartner=" + byPartner + '}';
	}
}
