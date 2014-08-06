package com.qbit.p2p.credit.order.model;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * @author Alexander_Sergeev
 */
public enum OrderStatus {
	OPENED, IN_PROCESS, SUCCESS, NOT_SUCCESS, ARBITRATION;
	
	private static final EnumMap<OrderStatus, EnumSet<OrderStatus>> VALID_STATUSES_MAP;
	static {	
		VALID_STATUSES_MAP = new EnumMap<>(OrderStatus.class);
		EnumSet<OrderStatus> opened = EnumSet.of(IN_PROCESS, SUCCESS, NOT_SUCCESS, ARBITRATION);
		VALID_STATUSES_MAP.put(OPENED, opened);
		EnumSet<OrderStatus> inProcess = EnumSet.of(SUCCESS, NOT_SUCCESS, ARBITRATION);
		VALID_STATUSES_MAP.put(IN_PROCESS, inProcess);
		EnumSet<OrderStatus> success = EnumSet.of(NOT_SUCCESS, ARBITRATION);
		VALID_STATUSES_MAP.put(SUCCESS, success);
		EnumSet<OrderStatus> notSuccess = EnumSet.of(SUCCESS, ARBITRATION);
		VALID_STATUSES_MAP.put(NOT_SUCCESS, notSuccess);
		EnumSet<OrderStatus> arbitration = EnumSet.of(SUCCESS, NOT_SUCCESS);
		VALID_STATUSES_MAP.put(ARBITRATION, arbitration);
	}
	
	public boolean isValidNewStatus(OrderStatus newStatus) {
		return VALID_STATUSES_MAP.get(this).contains(newStatus);
	}
}
