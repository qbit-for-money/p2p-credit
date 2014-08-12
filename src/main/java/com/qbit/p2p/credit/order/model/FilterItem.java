package com.qbit.p2p.credit.order.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Alexander_Sergeev
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterItem {

	@XmlElement(required = true)
	private FilterOperator filterOperator;
	private String filterDataField;
	private FilterCondition filterCondition;
	private String filterValue;

	public String getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	public FilterCondition getFilterCondition() {
		return (filterCondition == null) ? FilterCondition.EQUAL : filterCondition;
	}

	public void setFilterCondition(FilterCondition filterCondition) {
		this.filterCondition = filterCondition;
	}


	public FilterOperator getFilterOperator() {
		return (filterOperator == null) ? FilterOperator.AND : filterOperator;
	}

	public void setFilterOperator(FilterOperator filterOperator) {
		this.filterOperator = filterOperator;
	}

	public String getFilterDataField() {
		return filterDataField;
	}

	public void setFilterDataField(String filterDataField) {
		this.filterDataField = filterDataField;
	}

	@Override
	public String toString() {
		return "FilterItem{" + "filterValue=" + filterValue + ", filterCondition=" + filterCondition + ", filterOperator=" + filterOperator + ", filterDataField=" + filterDataField + '}';
	}
}
