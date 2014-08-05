package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import com.qbit.p2p.credit.commons.model.DateAdapter;
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author Alexander_Sergeev
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "OrderInfo.findByPartnersRating",
		query = "SELECT t1.id FROM Statistics t0, Statistics t1 GROUP BY t0.id, t1.id HAVING t0.id IN (SELECT DISTINCT t2.userId FROM Respond t2 WHERE t2.id IN (SELECT t3.approvedRespondId FROM OrderInfo t3 WHERE (t3.status = :status) AND (t3.userId = t1.id) AND (t3.userId <> t0.id))) AND SUM(t0.opennessRating * :openessFactor + t0.ordersRating * :transactionsFactor) >= :rating")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderInfo implements Identifiable<String>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	private String userId;
	private OrderStatus status;
	@Column(precision = 10, scale = 3)
	private BigDecimal incomingAmount;
	private String incomingCurrency;
	@Column(precision = 10, scale = 3)
	private BigDecimal outcomingAmout;
	private String outcomingCurrency;
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	@XmlJavaTypeAdapter(DateAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	private Date bookingDeadline;
	private int duration;
	private DurationType durationType;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<Category> categories;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<Language> languages;
	@Lob
	private String orderData;

	@OneToMany(cascade = CascadeType.ALL, targetEntity = Respond.class)
	private List<Respond> responses = new ArrayList<>();
	private String approvedUserId;

	@OneToOne(cascade = CascadeType.ALL)
	private Comment comment;

	@Override
	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getIncomingAmount() {
		return incomingAmount;
	}

	public void setIncomingAmount(BigDecimal incomingAmount) {
		if (incomingAmount != null) {
			this.incomingAmount = incomingAmount.setScale(3, RoundingMode.HALF_UP);
		} else {
			this.incomingAmount = null;
		}
	}

	public String getIncomingCurrency() {
		return incomingCurrency;
	}

	public void setIncomingCurrency(String incomingCurrency) {
		this.incomingCurrency = incomingCurrency;
	}

	public BigDecimal getOutcomingAmout() {
		return outcomingAmout;
	}

	public void setOutcomingAmout(BigDecimal outcomingAmout) {
		if (outcomingAmout != null) {
			this.outcomingAmout = outcomingAmout.setScale(3, RoundingMode.HALF_UP);
		} else {
			this.outcomingAmout = null;
		}
	}

	public String getOutcomingCurrency() {
		return outcomingCurrency;
	}

	public void setOutcomingCurrency(String outcomingCurrency) {
		this.outcomingCurrency = outcomingCurrency;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getBookingDeadline() {
		return bookingDeadline;
	}

	public void setBookingDeadline(Date bookingDeadline) {
		this.bookingDeadline = bookingDeadline;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public DurationType getDurationType() {
		return durationType;
	}

	public void setDurationType(DurationType durationType) {
		this.durationType = durationType;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(List<Language> languages) {
		this.languages = languages;
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

	public String getApprovedUserId() {
		return approvedUserId;
	}

	public void setApprovedUserId(String approvedUserId) {
		this.approvedUserId = approvedUserId;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public boolean isValid() {
		return (bookingDeadline != null)
			&& (duration >= 0)
			&& (durationType != null)
			&& (userId != null)
			&& !userId.isEmpty()
			&& (categories != null)
			&& (languages != null)
			&& (incomingCurrency != null || outcomingCurrency != null)
			&& (!BigDecimal.ZERO.equals(incomingAmount) || !BigDecimal.ZERO.equals(outcomingAmout));
	}

	@Override
	public String toString() {
		return "OrderInfo{" + "id=" + id + ", userId=" + userId + ", status=" + status + ", incomingAmount=" + incomingAmount + ", incomingCurrency=" + incomingCurrency + ", outcomingAmout=" + outcomingAmout + ", outcomingCurrency=" + outcomingCurrency + ", creationDate=" + creationDate + ", bookingDeadline=" + bookingDeadline + ", duration=" + duration + ", durationType=" + durationType + ", categories=" + categories + ", languages=" + languages + ", orderData=" + orderData + ", responses=" + responses + ", approvedUserId=" + approvedUserId + ", comment=" + comment + '}';
	}
}
