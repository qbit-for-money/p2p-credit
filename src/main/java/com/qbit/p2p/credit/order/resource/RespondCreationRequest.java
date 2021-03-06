package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.Respond;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexander_Sergeev
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RespondCreationRequest {

	private String orderId;
	private String comment;

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
	
	public Respond toRespond() {
		Respond respond = new Respond();
		respond.setCreationDate(new Date());
		respond.setComment(comment);
		return respond;
	}

	@Override
	public String toString() {
		return "RespondCreationRequest{" + "orderId=" + orderId + ", comment=" + comment + '}';
	}
}
