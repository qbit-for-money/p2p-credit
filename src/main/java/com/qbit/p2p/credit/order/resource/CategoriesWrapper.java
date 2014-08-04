package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.Category;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoriesWrapper {

	@XmlElement
	@XmlList
	private List<Category> categories;

	public CategoriesWrapper() {
	}

	public CategoriesWrapper(List<Category> categories) {
		this.categories = categories;
	}

	public List<Category> getCategories() {
		return categories;
	}
}
