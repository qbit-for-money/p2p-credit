package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.model.DateAdapter;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import com.qbit.p2p.credit.user.model.UserCurrency;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
	private String title;
	@Lob
	private String orderData;
	
	private OrderStatus status;
	
	private OrderType type;
	@ElementCollection
	private List<String> languages;
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private Currency currency;
	@Embedded
	private CurrencyInterval currencyInterval;
	
	private String reward;
	private long responses;
	

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
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

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public CurrencyInterval getCurrencyInterval() {
		return currencyInterval;
	}

	public void setCurrencyInterval(CurrencyInterval currencyInterval) {
		this.currencyInterval = currencyInterval;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getOrderData() {
		return orderData;
	}

	public void setOrderData(String orderData) {
		this.orderData = orderData;
	}

	public long getResponses() {
		return responses;
	}

	public void setResponses(long responses) {
		this.responses = responses;
	}
	
	@Override
	public String toString() {
		return "OrderInfo{" + "id=" + id + ", creationDate=" + creationDate + ", endDate=" + endDate + ", userPublicKey=" + userPublicKey + ", userName=" + userName + ", title=" + title + ", orderData=" + orderData + ", status=" + status + ", type=" + type + ", languages=" + languages + ", currency=" + currency + ", currencyInterval=" + currencyInterval + ", reward=" + reward + ", responses=" + responses + '}';
	}
}
