package com.qbit.p2p.credit.comment.dao;

import com.qbit.commons.dao.util.DAOUtil;
import com.qbit.p2p.credit.comment.model.Comment;
import com.qbit.p2p.credit.comment.model.EntityPartId;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Alexander_Sergeev
 */
public class CommentDAO {

	@Inject
	private EntityManagerFactory entityManagerFactory;

	public Comment find(EntityPartId id) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DAOUtil.find(entityManagerFactory.createEntityManager(),
				Comment.class, id, null);
		} finally {
			entityManager.close();
		}
	}
}
