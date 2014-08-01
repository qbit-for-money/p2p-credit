package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import com.qbit.p2p.credit.commons.model.Currency;
import com.qbit.p2p.credit.commons.model.DateAdapter;
import com.qbit.p2p.credit.money.model.serialization.CurrencyAdapter;
import com.qbit.p2p.credit.user.model.Language;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Alexander_Sergeev
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "OrderInfo.findByPartnersRating",
			query = "SELECT t1.id FROM Statistics t0, Statistics t1 GROUP BY t0.id, t1.id HAVING t0.id IN (SELECT DISTINCT t2.userPublicKey FROM Respond t2 WHERE t2.id IN (SELECT t3.approvedResponseId FROM OrderInfo t3 WHERE (t3.status = :status) AND (t3.userPublicKey = t1.id) AND (t3.userPublicKey <> t0.id))) AND SUM(t0.summaryRating) >= :rating")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
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

	@ManyToMany(cascade = CascadeType.ALL)
	private List<OrderCategory> categories;
	@Lob
	private String orderData;

	private OrderStatus status;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = Respond.class)
	private List<Respond> responses = new ArrayList<>();

	private String approvedResponseId;

	@OneToOne(cascade = CascadeType.ALL)
	private Comment comment;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Language> languages;
	//@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private String givingCurrency;
	//@XmlJavaTypeAdapter(CurrencyAdapter.class)
	private String takingCurrency;
	@Column(precision = 10, scale = 3)
	private BigDecimal takingValue;
	@Column(precision = 10, scale = 3)
	private BigDecimal givingValue;

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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public List<OrderCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<OrderCategory> categories) {
		this.categories = categories;
	}

	public String getApprovedResponseId() {
		return approvedResponseId;
	}

	public void setApprovedResponseId(String approvedResponseId) {
		this.approvedResponseId = approvedResponseId;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
	}

	public String getGivingCurrency() {
		return givingCurrency;
	}

	public void setGivingCurrency(String givingCurrency) {
		this.givingCurrency = givingCurrency;
	}

	public String getTakingCurrency() {
		return takingCurrency;
	}

	public void setTakingCurrency(String takingCurrency) {
		this.takingCurrency = takingCurrency;
	}

	public BigDecimal getTakingValue() {
		return takingValue;
	}

	public void setTakingValue(BigDecimal takingValue) {
		if (takingValue != null) {
			this.takingValue = takingValue.setScale(3, RoundingMode.HALF_UP);
		} else {
			this.takingValue = null;
		}
	}

	public BigDecimal getGivingValue() {
		return givingValue;
	}

	public void setGivingValue(BigDecimal givingValue) {
		if (givingValue != null) {
			this.givingValue = givingValue.setScale(3, RoundingMode.HALF_UP);
		} else {
			this.givingValue = null;
		}
	}

	public String getOrderData() {
		return orderData;
	}

	public void setOrderData(String orderData) {
		this.orderData = orderData;
	}

	public List<Respond> getResponses() {
		return responses;
	}

	public void setResponses(List<Respond> responses) {
		this.responses = responses;
	}

	public DurationType getDurationType() {
		return durationType;
	}

	public void setDurationType(DurationType durationType) {
		this.durationType = durationType;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
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
		return "OrderInfo{" + "id=" + id + ", creationDate=" + creationDate + ", endDate=" + endDate + ", duration=" + duration + ", durationType=" + durationType + ", userPublicKey=" + userPublicKey + ", categories=" + categories + ", orderData=" + orderData + ", status=" + status + ", responses=" + responses + ", approvedResponseId=" + approvedResponseId + ", comment=" + comment + ", languages=" + languages + ", givingCurrency=" + givingCurrency + ", takingCurrency=" + takingCurrency + ", takingValue=" + takingValue + ", givingValue=" + givingValue + '}';
	}
}
