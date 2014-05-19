package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Alexander_Sergeev
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "OrderInfo.findByUser",
			query = "SELECT o FROM OrderInfo o WHERE o.userPublicKey = :userPublicKey"),
	@NamedQuery(name = "OrderInfo.findByUserType",
			query = "SELECT o FROM OrderInfo o, UserPublicProfile u WHERE "
					+ "o.userPublicKey = u.publicKey AND u.type = :userType"),
	@NamedQuery(name = "OrderInfo.findByUserAndTimestamp",
			query = "SELECT o FROM OrderInfo o WHERE o.userPublicKey = :userPublicKey"
			+ " AND o.creationDate = :creationDate"),
	@NamedQuery(name = "OrderInfo.findMedianVolumeOfSuccess",
			query = "SELECT o FROM OrderInfo o WHERE o.status = com.qbit.p2p.credit.order.model.OrderStatus.SUCCESS")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderInfo implements Identifiable<String>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private String id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	private String userPublicKey;
	
	private OrderStatus status;

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getUserPublicKey() {
		return userPublicKey;
	}

	public void setUserPublicKey(String userPublicKey) {
		this.userPublicKey = userPublicKey;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
}
