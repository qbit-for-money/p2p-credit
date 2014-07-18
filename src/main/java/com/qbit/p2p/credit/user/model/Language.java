package com.qbit.p2p.credit.user.model;

import com.qbit.commons.model.Identifiable;
import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class Language implements Identifiable<String>, Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private String id;
	
	private String title;
	
	private boolean withoutParent;

	public Language() {
	}

	public Language(String title) {
		this.title = title;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isWithoutParent() {
		return withoutParent;
	}

	public void setWithoutParent(boolean withoutParent) {
		this.withoutParent = withoutParent;
	}

	@Override
	public String toString() {
		return "Language{" + "id=" + id + ", title=" + title + '}';
	}
}
