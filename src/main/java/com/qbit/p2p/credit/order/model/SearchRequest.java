package com.qbit.p2p.credit.order.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Alexander_Sergeev
 */
@XmlRootElement
public class SearchRequest {

	public static class FilterItem {

		@XmlElement(required = true)
		private FilterOperator filterOperator;
		private String filterDataField;
		private String filterCondition;
		private String filterValue;

		public String getFilterValue() {
			return filterValue;
		}

		public void setFilterValue(String filterValue) {
			this.filterValue = filterValue;
		}

		public String getFilterCondition() {
			return filterCondition;
		}

		public void setFilterCondition(String filterCondition) {
			this.filterCondition = filterCondition;
		}

		public FilterOperator getFilterOperator() {
			return filterOperator;
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
	@XmlElement
	@XmlList
	private List<FilterItem> filterItems;
	private String sortOrder;
	private int pageNumber;
	private int pageSize;
	private int recordstartindex;
	private int recordendindex;
	private String sortDataField;
	private OrderType orderType;

	public List<FilterItem> getFilterItems() {
		return filterItems;
	}

	public void setFilterItems(List<FilterItem> filterItems) {
		this.filterItems = filterItems;
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

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getSortDataField() {
		return sortDataField;
	}

	public void setSortDataField(String sortDataField) {
		this.sortDataField = sortDataField;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	@Override
	public String toString() {
		return "OrdersRequest{" + "filterItems=" + filterItems + ", sortOrder=" + sortOrder + ", pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", recordstartindex=" + recordstartindex + ", recordendindex=" + recordendindex + ", sortDataField=" + sortDataField + ", orderType=" + orderType + '}';
	}
}
