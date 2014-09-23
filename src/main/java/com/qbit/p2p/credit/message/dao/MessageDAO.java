package com.qbit.p2p.credit.message.dao;

import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.commons.user.UserInfo;
import com.qbit.p2p.credit.message.model.Message;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Alexander_Sergeev
 */
@Singleton
public class MessageDAO {
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	public Message create(final Message message) {
		if ((message == null) || !message.isValid()) {
			throw new IllegalArgumentException("Message is not valid.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Message>() {

			@Override
			public Message call(EntityManager entityManager) {
				UserInfo user = entityManager.find(UserInfo.class, message.getUserId());
				UserInfo partner = entityManager.find(UserInfo.class, message.getPartnerId());
				System.out.println("@@ " + message + " : " + user + " : " + partner);
				if ((user == null) || (partner == null)) {
					return null;
				}
				message.setCreationDate(new Date());
				return entityManager.merge(message);
			}
		});
	}
	
	public Message createMessageForAdmin(final Message message) {
		if (message == null) {
			throw new IllegalArgumentException("Message is not valid.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<Message>() {

			@Override
			public Message call(EntityManager entityManager) {
				message.setCreationDate(new Date());
				System.out.println("%%% " + message);
				return entityManager.merge(message);
			}
		});
	}
	
	public List<Message> findAll(int pageNumber, int pageSize) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(message);
			TypedQuery<Message> query = entityManager.createQuery(criteria);
			query.setFirstResult(pageNumber);
			query.setMaxResults(pageSize);
			List<Message> m = query.getResultList();
			return m;
		} finally {
			entityManager.close();
		}
	}
	
	public List<Message> findByUserId(String userId, int pageNumber, int pageSize) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(message);
			criteria.where(builder.equal(message.<String>get("userId"), userId));
			TypedQuery<Message> query = entityManager.createQuery(criteria);
			query.setFirstResult(pageNumber);
			query.setMaxResults(pageSize);
			List<Message> m = query.getResultList();
			return m;
		} finally {
			entityManager.close();
		}
	}
	
	public long getLengthByUserId(String userId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(builder.countDistinct(message));
			criteria.where(builder.equal(message.<String>get("userId"), userId));
			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}
	
	public List<Message> findByUserIdAndPartnerId(String userId, String partnerId, int pageNumber, int pageSize) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(message);
			
			Predicate fromUserPredicate = builder.equal(message.<String>get("userId"), userId);
			Predicate toPartnerPredicate = builder.equal(message.<String>get("partnerId"), partnerId);
			
			Predicate fromPartnerPredicate = builder.equal(message.<String>get("userId"), partnerId);
			Predicate toUserPredicate = builder.equal(message.<String>get("partnerId"), userId);
			
			Predicate fromUserToPartner = builder.and(fromUserPredicate, toPartnerPredicate);
			Predicate fromPartnerrToUser = builder.and(fromPartnerPredicate, toUserPredicate);
			criteria.where(builder.or(fromUserToPartner, fromPartnerrToUser));
			criteria.orderBy(builder.desc(message.get("creationDate")));
			TypedQuery<Message> query = entityManager.createQuery(criteria);
			query.setFirstResult(pageNumber * pageSize);
			query.setMaxResults(pageSize);
			List<Message> m = query.getResultList();
			return m;
		} finally {
			entityManager.close();
		}
	}
	
	public long getLengthByUserIdAndPartnerId(String userId, String partnerId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(builder.countDistinct(message));
			
			Predicate fromUserPredicate = builder.equal(message.<String>get("userId"), userId);
			Predicate toPartnerPredicate = builder.equal(message.<String>get("partnerId"), partnerId);
			
			Predicate fromPartnerPredicate = builder.equal(message.<String>get("userId"), partnerId);
			Predicate toUserPredicate = builder.equal(message.<String>get("partnerId"), userId);
			
			Predicate fromUserToPartner = builder.and(fromUserPredicate, toPartnerPredicate);
			Predicate fromPartnerrToUser = builder.and(fromPartnerPredicate, toUserPredicate);
			criteria.where(builder.or(fromUserToPartner, fromPartnerrToUser));

			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}
	
	public List<Message> findLaterThan(String userId, String partnerId, Date date) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(message);
			criteria.where(builder.greaterThan(message.<Date>get("creationDate"), date));
			
			Predicate fromUserPredicate = builder.equal(message.<String>get("userId"), userId);
			Predicate toPartnerPredicate = builder.equal(message.<String>get("partnerId"), partnerId);
			
			Predicate fromPartnerPredicate = builder.equal(message.<String>get("userId"), partnerId);
			Predicate toUserPredicate = builder.equal(message.<String>get("partnerId"), userId);
			
			Predicate fromUserToPartner = builder.and(fromUserPredicate, toPartnerPredicate);
			Predicate fromPartnerrToUser = builder.and(fromPartnerPredicate, toUserPredicate);
			criteria.where(builder.and(builder.or(fromUserToPartner, fromPartnerrToUser), 
					builder.greaterThan(message.<Date>get("creationDate"), date)));
			criteria.orderBy(builder.desc(message.get("creationDate")));
			TypedQuery<Message> query = entityManager.createQuery(criteria);
			List<Message> m = query.getResultList();
			return m;
		} finally {
			entityManager.close();
		}
	}
	
	public List<String> findPartnersIds(String userId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Tuple> criteria = builder.createQuery(Tuple.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.multiselect(message.<String>get("userId"), message.<String>get("partnerId"));
			//criteria.select(message.<String>get("partnerId"));
			Predicate fromUserPredicate = builder.equal(message.<String>get("userId"), userId);
			Predicate toUserPredicate = builder.equal(message.<String>get("partnerId"), userId);
			
			criteria.where(builder.or(fromUserPredicate, toUserPredicate));
			criteria.orderBy(builder.desc(message.get("creationDate")));
			TypedQuery<Tuple> query = entityManager.createQuery(criteria);
			//query.setFirstResult(pageNumber);
			//query.setMaxResults(pageSize);
			List<Tuple> ids = query.getResultList();
			List<String> partnersIds = new ArrayList<>();
			for(Tuple id : ids) {
				String tupleUserId = id.get(0, String.class);
				String tuplePartnerId = id.get(1, String.class);
				if(userId.equals(tupleUserId)) {
					if(!partnersIds.contains(tuplePartnerId)) {
						partnersIds.add(tuplePartnerId);
					}
				} else if(!partnersIds.contains(tupleUserId)){
					partnersIds.add(tupleUserId);
				}
			}
			System.out.println("%%%: " + partnersIds);
			return partnersIds;
		} finally {
			entityManager.close();
		}
	}
	
	public long getLengthByPartnersIds(String userId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(builder.countDistinct(message.<String>get("partnerId")));			
			Predicate fromUserPredicate = builder.equal(message.<String>get("userId"), userId);
			Predicate toUserPredicate = builder.equal(message.<String>get("partnerId"), userId);			
			criteria.where(builder.or(fromUserPredicate, toUserPredicate));	

			return (Long) entityManager.createQuery(criteria).getSingleResult();
		} finally {
			entityManager.close();
		}
	}
	
	/*public List<Message> findPartnersLastMessages(String userId) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Message> criteria = builder.createQuery(Message.class);
			Root<Message> message = criteria.from(Message.class);
			criteria.select(message);
			
			Predicate fromUserPredicate = builder.equal(message.<String>get("userId"), userId);
			Predicate toPartnerPredicate = builder.equal(message.<String>get("partnerId"), partnerId);
			
			Predicate fromPartnerPredicate = builder.equal(message.<String>get("userId"), partnerId);
			Predicate toUserPredicate = builder.equal(message.<String>get("partnerId"), userId);
			
			Predicate fromUserToPartner = builder.and(fromUserPredicate, toPartnerPredicate);
			Predicate fromPartnerrToUser = builder.and(fromPartnerPredicate, toUserPredicate);
			criteria.where(builder.or(fromUserToPartner, fromPartnerrToUser));
			criteria.orderBy(builder.desc(message.get("creationDate")));
			TypedQuery<Message> query = entityManager.createQuery(criteria);
			query.setFirstResult(pageNumber * pageSize);
			query.setMaxResults(pageSize);
			List<Message> m = query.getResultList();
			return m;
		} finally {
			entityManager.close();
		}
	}*/
}
