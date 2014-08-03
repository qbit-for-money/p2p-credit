package com.qbit.p2p.credit.user.model;

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
public class Language implements Identifiable<String>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private String code;
	
	private boolean custom;

	public Language() {
	}

	public Language(String code) {
		this.code = code;
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

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.code);
		hash = 71 * hash + (this.custom ? 1 : 0);
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
		final Language other = (Language) obj;
		if (!Objects.equals(this.code, other.code)) {
			return false;
		}
		if (this.custom != other.custom) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Language{" + "code=" + code + ", custom=" + custom + '}';
	}
}
