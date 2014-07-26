package com.qbit.p2p.credit.like.dao;

import com.qbit.commons.dao.util.DAOUtil;
import static com.qbit.commons.dao.util.DAOUtil.invokeInTransaction;
import com.qbit.commons.dao.util.TrCallable;
import com.qbit.p2p.credit.like.model.LikeS;
import com.qbit.p2p.credit.like.model.EntityPartId;
import java.util.Set;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;

/**
 *
 * @author Alex
 */
public class LikeDAO {
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	public LikeS find(EntityPartId id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
					LikeS.class, id, null);
		} finally {
			entityManager.close();
		}
	}
	
	public LikeS like(final String userPublicKey, final EntityPartId likeId) {
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
				if (!alreadyVotedUserPublicKeys.contains(userPublicKey)) {
					like.setLikeCount(like.getLikeCount() + 1);
					alreadyVotedUserPublicKeys.add(userPublicKey);
					like.setAlreadyVotedUserPublicKeys(alreadyVotedUserPublicKeys);
					entityManager.merge(like);
				}
				return like;
			}
		});
	}
	
	public LikeS dislike(final String userPublicKey, final EntityPartId likeId) {
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
				if (!alreadyVotedUserPublicKeys.contains(userPublicKey)) {
					like.setLikeCount(like.getLikeCount() + 1);
					alreadyVotedUserPublicKeys.add(userPublicKey);
					like.setAlreadyVotedUserPublicKeys(alreadyVotedUserPublicKeys);
					entityManager.merge(like);
				}
				return like;
			}
		});
	}
}
