package com.qbit.p2p.credit.material.model;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author Alexander_Sergeev
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "Materials.findByUser",
			query = "SELECT m FROM Materials m WHERE m.userId = :userId"),
	@NamedQuery(name = "Materials.findByUserAndType",
			query = "SELECT m FROM Materials m WHERE m.userId = :userId AND m.type = :type")})
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Materials implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	private String id;

	private String userId;
	private String title;
	private String author;
	private String description;
	private long physicalSize;
	private ExternalMaterials externalMaterials;
	private MaterialType type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getPhysicalSize() {
		return physicalSize;
	}

	public void setPhysicalSize(long physicalSize) {
		this.physicalSize = physicalSize;
	}

	public ExternalMaterials getExternalMaterials() {
		return externalMaterials;
	}

	public void setExternalMaterials(ExternalMaterials externalMaterials) {
		this.externalMaterials = externalMaterials;
	}

	public MaterialType getType() {
		return type;
	}

	public void setType(MaterialType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Materials{" + "id=" + id + ", userId=" + userId + ", title=" + title + ", author=" + author + ", description=" + description + ", physicalSize=" + physicalSize + ", externalMaterials=" + externalMaterials + ", type=" + type + '}';
	}
}
