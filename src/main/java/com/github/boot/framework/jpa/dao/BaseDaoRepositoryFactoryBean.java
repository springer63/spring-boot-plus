package com.github.boot.framework.jpa.dao;

import com.github.boot.framework.jpa.strategy.GenericQueryLookupStrategy;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 定义 RepositoryFactoryBean 的实现类
 * 
 * @author ChenJianhui
 * @param <R>
 * @param <T>
 * @param <ID>
 */
public class BaseDaoRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, ID> {

	public BaseDaoRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new BaseDaoFactory(entityManager);
	}

	/**
	 * 自定义DaoFactory类
	 * 
	 * @author ChenJianhui
	 * @param <S>
	 * @param <ID>
	 */
	private static class BaseDaoFactory<S, ID extends Serializable> extends JpaRepositoryFactory {

		private EntityManager entityManager;

		private QueryExtractor extractor;

		public BaseDaoFactory(EntityManager entityManager) {
			super(entityManager);
			this.entityManager = entityManager;
			this.extractor = PersistenceProvider.fromEntityManager(entityManager);
		}

		@Override
		protected <T, ID2 extends Serializable> org.springframework.data.jpa.repository.support.SimpleJpaRepository<?, ?> getTargetRepository(
				RepositoryInformation information, EntityManager entityManager) {
			return new BaseDaoImpl<>(this.getEntityInformation(information.getDomainType()), entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return BaseDao.class;
		}

		@Override
		protected QueryLookupStrategy getQueryLookupStrategy(QueryLookupStrategy.Key key, EvaluationContextProvider evaluationContextProvider) {
			QueryLookupStrategy queryLookupStrategy = GenericQueryLookupStrategy.create(this.entityManager, this.extractor, key, evaluationContextProvider) ;
			return queryLookupStrategy;
		}
	}

}
