package com.qbit.p2p.credit.order.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander_Sergeev
 */
public enum OrderStatus {
	OPENED, IN_PROCESS, SUCCESS, NOT_SUCCESS, ARBITRATION;
	
	private static final EnumMap<OrderStatus, EnumSet<OrderStatus>> VALID_STATUSES_MAP;
	static {	
		VALID_STATUSES_MAP = new EnumMap<>(OrderStatus.class);
		VALID_STATUSES_MAP.put(OPENED, EnumSet.of(IN_PROCESS));
		VALID_STATUSES_MAP.put(IN_PROCESS, EnumSet.of(SUCCESS, NOT_SUCCESS, ARBITRATION));
		VALID_STATUSES_MAP.put(SUCCESS, EnumSet.of(NOT_SUCCESS, ARBITRATION));
		VALID_STATUSES_MAP.put(NOT_SUCCESS, EnumSet.of(SUCCESS, ARBITRATION));
		VALID_STATUSES_MAP.put(ARBITRATION, EnumSet.noneOf(OrderStatus.class));
	}
	
	public boolean isValidNewStatus(OrderStatus newStatus) {
		return VALID_STATUSES_MAP.get(this).contains(newStatus);
	}
	
	public EnumSet<OrderStatus> prev() {
		List<OrderStatus> result = new ArrayList<>();
		for (Map.Entry<OrderStatus, EnumSet<OrderStatus>> entry : VALID_STATUSES_MAP.entrySet()) {
			if (entry.getValue().contains(this)) {
				result.add(entry.getKey());
			}
		}
		return EnumSet.copyOf(result);
	}
}
