package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Alexander_Sergeev
 */
@Entity
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderCategory implements Identifiable<String>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private String id;

	private String title;
	
	boolean categoryForOrder;

	public OrderCategory() {
	}

	public OrderCategory(String title) {
		this.title = title;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isCategoryForOrder() {
		return categoryForOrder;
	}

	public void setCategoryForOrder(boolean categoryForOrder) {
		this.categoryForOrder = categoryForOrder;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + Objects.hashCode(this.title);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final OrderCategory other = (OrderCategory) obj;
		if (!Objects.equals(this.title, other.title)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "OrderCategory{" + "id=" + id + ", title=" + title + '}';
	}
}
