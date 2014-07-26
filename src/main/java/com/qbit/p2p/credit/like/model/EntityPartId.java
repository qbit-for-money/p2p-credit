package com.qbit.p2p.credit.like.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alex
 */
@Embeddable
@Access(AccessType.FIELD)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityPartId implements Serializable {
	
	private String entityType;
	private String entityId;
	private String entityField;

	public EntityPartId() {
	}

	public EntityPartId(String entityType, String entityId, String entityField) {
		if (((entityType == null) || (entityId == null) || entityId.isEmpty()
				|| (entityField == null) || entityField.isEmpty())) {
			throw new IllegalArgumentException();
		}
		this.entityType = entityType;
		this.entityId = entityId;
		this.entityField = entityField;
	}
	
	public boolean isValid() {
		return ((entityType != null) && (entityId != null) && !entityId.isEmpty()
				&& (entityField != null) && !entityField.isEmpty());
	}

	public String getEntityType() {
		return entityType;
	}

	public String getEntityId() {
		return entityId;
	}

	public String getEntityField() {
		return entityField;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.entityType);
		hash = 79 * hash + Objects.hashCode(this.entityId);
		hash = 79 * hash + Objects.hashCode(this.entityField);
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
		final EntityPartId other = (EntityPartId) obj;
		if (!Objects.equals(this.entityType, other.entityType)) {
			return false;
		}
		if (!Objects.equals(this.entityId, other.entityId)) {
			return false;
		}
		if (!Objects.equals(this.entityField, other.entityField)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "LikeId{" + "entityType=" + entityType + ", entityId=" + entityId + ", entityField=" + entityField + '}';
	}
}
