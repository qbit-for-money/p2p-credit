package com.qbit.p2p.credit.order.model;

import com.qbit.commons.model.Identifiable;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@Entity
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Category implements Identifiable<String>, Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String code;
	
	private boolean custom;
	
	private CategoryType type;

	public Category() {
	}

	public Category(String code, CategoryType type) {
		this.code = code;
		this.type = type;
	}

	@Override
	public String getId() {
		return code;
	}

	public String getCode() {
		return code;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	public CategoryType getType() {
		return type;
	}

	public void setType(CategoryType type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.code);
		hash = 13 * hash + (this.custom ? 1 : 0);
		hash = 13 * hash + Objects.hashCode(this.type);
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
		final Category other = (Category) obj;
		if (!Objects.equals(this.code, other.code)) {
			return false;
		}
		if (this.custom != other.custom) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "OrderCategory{" + "code=" + code + ", custom=" + custom + ", type=" + type + '}';
	}
}
