package com.qbit.p2p.credit.order.model;

import com.qbit.p2p.credit.commons.model.DateAdapter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@Embeddable
public class Respond implements Serializable {

	private String userPublicKey;
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	@Lob
	private String comment;
	RespondStatus status;

	public String getUserPublicKey() {
		return userPublicKey;
	}

	public void setUserPublicKey(String userPublicKey) {
		this.userPublicKey = userPublicKey;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public RespondStatus getStatus() {
		return status;
	}

	public void setStatus(RespondStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Respond{" + "userPublicKey=" + userPublicKey + ", creationDate=" + creationDate + ", comment=" + comment + ", status=" + status + '}';
	}
}
