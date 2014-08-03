package com.qbit.p2p.credit.order.resource;

import com.qbit.p2p.credit.order.model.OrderCategory;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Alexander_Sergeev
 */
@XmlRootElement
public class CategoriesWrapper {

	@XmlElement
	@XmlList
	private List<OrderCategory> categories;

	public CategoriesWrapper() {
	}

	public CategoriesWrapper(List<OrderCategory> categories) {
		this.categories = categories;
	}

	public List<OrderCategory> getCategories() {
		return categories;
	}
}
