package com.qbit.p2p.credit.order.dao.meta;

/**
 * @author Alexander_Sergeev
 */
public interface ValueProvider {
	
	Object get(String value);
}
