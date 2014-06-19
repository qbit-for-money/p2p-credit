package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import com.qbit.p2p.credit.commons.model.DateAdapter;
import com.qbit.p2p.credit.user.model.UserCurrency;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Alexander_Sergeev
 */
@Entity
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderInfo implements Identifiable<String>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private String id;
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	private String userPublicKey;
	private String userName;
	
	private OrderStatus status;
	
	private List<String> languages;
	private List<UserCurrency> currencies;
	
	private int reward;
	

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}

	public List<UserCurrency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(List<UserCurrency> currencies) {
		this.currencies = currencies;
	}

	public int getReward() {
		return reward;
	}

	public void setReward(int reward) {
		this.reward = reward;
	}

	@Override
	public String toString() {
		return "OrderInfo{" + "id=" + id + ", creationDate=" + creationDate + ", endDate=" + endDate + ", userPublicKey=" + userPublicKey + ", userName=" + userName + ", status=" + status + ", languages=" + languages + ", currencies=" + currencies + ", reward=" + reward + '}';
	}
}
