package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.model.DateAdapter;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import com.qbit.p2p.credit.user.model.UserCurrency;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
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
	private int duration;
	private DurationType durationType;

	private String userPublicKey;
	@ElementCollection
	private List<String> categories;
	@Lob
	private String orderData;
	
	private OrderStatus status;
	
	private List<Respond> responses;
	
	
	//private OrderType type;
	@ElementCollection
	private List<String> languages = new ArrayList<String>();
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private Currency givingCurrency;
	@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private Currency takingCurrency;
	@Column(precision = 10, scale = 3)
	private BigDecimal takingValue;
	@Column(precision = 10, scale = 3)
	private BigDecimal givingValue;
	//@Embedded
	//private CurrencyInterval currencyInterval;
	
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public Currency getGivingCurrency() {
		return givingCurrency;
	}

	public void setGivingCurrency(Currency givingCurrency) {
		this.givingCurrency = givingCurrency;
	}

	public Currency getTakingCurrency() {
		return takingCurrency;
	}

	public void setTakingCurrency(Currency takingCurrency) {
		this.takingCurrency = takingCurrency;
	}

	public BigDecimal getTakingValue() {
		return takingValue;
	}

	public void setTakingValue(BigDecimal takingValue) {
		this.takingValue = takingValue.setScale(3, RoundingMode.HALF_UP);
	}

	public BigDecimal getGivingValue() {
		return givingValue;
	}

	public void setGivingValue(BigDecimal givingValue) {
		this.givingValue = givingValue.setScale(3, RoundingMode.HALF_UP);
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

	public DurationType getDurationType() {
		return durationType;
	}

	public void setDurationType(DurationType durationType) {
		this.durationType = durationType;
	}
	
	public boolean isValid() {
		return (endDate != null) 
			&& (duration != 0)
			&& (durationType != null)
			&& (userPublicKey != null) 
			&& !userPublicKey.isEmpty() 
			&& (categories != null) 
			&& (languages != null) 
			&& (takingCurrency != null || givingCurrency != null) 
			&& (!BigDecimal.ZERO.equals(takingValue) || !BigDecimal.ZERO.equals(givingValue));
	}

	@Override
	public String toString() {
		return "OrderInfo{" + "id=" + id + ", creationDate=" + creationDate + ", endDate=" + endDate + ", duration=" + duration + ", durationType=" + durationType + ", userPublicKey=" + userPublicKey + ", categories=" + categories + ", orderData=" + orderData + ", status=" + status + ", languages=" + languages + ", givingCurrency=" + givingCurrency + ", takingCurrency=" + takingCurrency + ", takingValue=" + takingValue + ", givingValue=" + givingValue + ", reward=" + reward + ", responses=" + responses + '}';
	}
}
