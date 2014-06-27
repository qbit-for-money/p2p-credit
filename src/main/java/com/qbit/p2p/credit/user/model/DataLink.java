package com.qbit.p2p.credit.user.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 * @author Alexander_Sergeev
 */
@Embeddable
public class DataLink  implements Serializable {
	private String title;
	private String link;
	private String id;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.link);
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
		final DataLink other = (DataLink) obj;
		if (!Objects.equals(this.link, other.link)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DataLink{" + "title=" + title + ", link=" + link + ", id=" + id + '}';
	}
}
