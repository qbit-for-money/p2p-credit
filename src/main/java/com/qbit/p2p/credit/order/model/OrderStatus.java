package com.qbit.p2p.credit.order.model;

import java.util.EnumMap;
import java.util.EnumSet;

/**
 * @author Alexander_Sergeev
 */
public enum OrderStatus {
	OPENED, IN_PROCESS, SUCCESS, NOT_SUCCESS, ARBITRATION;
	public static EnumMap<OrderStatus, EnumSet<OrderStatus>> validStatusesMap;
	static {	
		validStatusesMap = new EnumMap<>(OrderStatus.class);
		EnumSet<OrderStatus> opened = EnumSet.of(IN_PROCESS, SUCCESS, NOT_SUCCESS, ARBITRATION);
		validStatusesMap.put(OPENED, opened);
		EnumSet<OrderStatus> inProcess = EnumSet.of(SUCCESS, NOT_SUCCESS, ARBITRATION);
		validStatusesMap.put(IN_PROCESS, inProcess);
		EnumSet<OrderStatus> success = EnumSet.of(NOT_SUCCESS, ARBITRATION);
		validStatusesMap.put(SUCCESS, success);
		EnumSet<OrderStatus> notSuccess = EnumSet.of(SUCCESS, ARBITRATION);
		validStatusesMap.put(NOT_SUCCESS, notSuccess);
		EnumSet<OrderStatus> arbitration = EnumSet.of(SUCCESS, NOT_SUCCESS);
		validStatusesMap.put(ARBITRATION, arbitration);
	}
	
	public boolean isValidNewStatus(OrderStatus newStatus) {
		return validStatusesMap.get(this).contains(newStatus);
	}
}
