package com.qbit.p2p.credit.like.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.like.model.LikeS;
import com.qbit.p2p.credit.like.model.EntityPartId;
import com.qbit.commons.log.model.OperationType;
import com.qbit.commons.log.service.LogScheduler;
import com.qbit.commons.user.UserInfo;
import java.util.Collection;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 *
 * @author Alex
 */
@Singleton
public class LikeDAO {
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
	@Inject
	private LogScheduler logScheduler;
	
	public LikeS find(EntityPartId id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					LikeS.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public LikeS like(final String userId, final EntityPartId likeId) {
		if (likeId == null) {
			throw new IllegalArgumentException("Like ID is NULL.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<LikeS>() {

			@Override
			public LikeS call(EntityManager entityManager) {
				LikeS like = entityManager.find(LikeS.class, likeId, LockModeType.PESSIMISTIC_WRITE);
				if (like == null) {
					like = new LikeS(likeId);
				}
				Set<String> alreadyVotedUserPublicKeys = like.getAlreadyVotedUserPublicKeys();
				if (!alreadyVotedUserPublicKeys.contains(userId)) {
					like.setLikeCount(like.getLikeCount() + 1);
					alreadyVotedUserPublicKeys.add(userId);
					like.setAlreadyVotedUserPublicKeys(alreadyVotedUserPublicKeys);
					LikeS mergedLike = entityManager.merge(like);
					logScheduler.createLog(OperationType.LIKE_DISLIKE,
							userId, likeId.getEntityId(), "LIKE_COUNT", String.valueOf(mergedLike.getLikeCount()));
				}
				return like;
			}
		});
	}
	
	public LikeS dislike(final String userId, final EntityPartId likeId) {
		if (likeId == null) {
			throw new IllegalArgumentException("Like ID is NULL.");
		}
		return invokeInTransaction(entityManagerFactory, new TrCallable<LikeS>() {

			@Override
			public LikeS call(EntityManager entityManager) {
				LikeS like = entityManager.find(LikeS.class, likeId, LockModeType.PESSIMISTIC_WRITE);
				if (like == null) {
					like = new LikeS(likeId);
				}
				Set<String> alreadyVotedUserPublicKeys = like.getAlreadyVotedUserPublicKeys();
				if (!alreadyVotedUserPublicKeys.contains(userId)) {
					like.setDislikeCount(like.getLikeCount() + 1);
					alreadyVotedUserPublicKeys.add(userId);
					like.setAlreadyVotedUserPublicKeys(alreadyVotedUserPublicKeys);
					LikeS mergedLike = entityManager.merge(like);
					logScheduler.createLog(OperationType.LIKE_DISLIKE,
							userId, likeId.getEntityId(), "DISLIKE_COUNT", String.valueOf(mergedLike.getDislikeCount()));
				}
				return like;
			}
		});
	}
	
	public long getAdditionalIdCount(final String publicKey) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery criteria = builder.createQuery(Long.class);

			Root<UserInfo> user = criteria.from(UserInfo.class);

			criteria.select(builder.countDistinct(user));
			//Expression<String> userIdExpression = user.get("publicKey");
			//Predicate publicKeyPredicate = builder.equal(userIdExpression, publicKey);
			//criteria.where(publicKeyPredicate);
			//long publicKeyCount = (Long) entityManager.createQuery(criteria).getSingleResult();
			Expression<Collection> idsExpression = user.get("additionalIds");
			Predicate containsIdsPredicate = builder.isMember(publicKey, idsExpression);
			//Predicate predicate = builder.or(publicKeyPredicate, containsIdsPredicate);
			criteria.where(containsIdsPredicate);
			long idsCount = (Long) entityManager.createQuery(criteria).getSingleResult();
			return idsCount;
		} finally {
			entityManager.close();
		}
	}
}
