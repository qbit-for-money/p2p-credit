package com.qbit.p2p.credit.order.model;

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
public class SearchRequest {

	@XmlElement
	@XmlList
	private List<FilterItem> filterItems;
	private SortOrder sortOrder;
	private String sortDataField;
	private int pageNumber;
	private int pageSize;
	private int recordstartindex;
	private int recordendindex;

	public List<FilterItem> getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(List<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortDataField() {
		return sortDataField;
	}

	public void setSortDataField(String sortDataField) {
		this.sortDataField = sortDataField;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRecordstartindex() {
		return recordstartindex;
	}

	public void setRecordstartindex(int recordstartindex) {
		this.recordstartindex = recordstartindex;
	}

	public int getRecordendindex() {
		return recordendindex;
	}

	public void setRecordendindex(int recordendindex) {
		this.recordendindex = recordendindex;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public String toString() {
		return "SearchRequest{" + "filterItems=" + filterItems + ", sortOrder=" + sortOrder + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", recordstartindex=" + recordstartindex + ", recordendindex=" + recordendindex + ", sortDataField=" + sortDataField + '}';
	}
}
