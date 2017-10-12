package com.github.boot.framework.jpa.strategy;

import com.github.boot.framework.jpa.NativeQuery;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;

/**
 * 方法查询策略
 * Created by cjh on 2017/7/15.
 */
public class GenericQueryLookupStrategy implements QueryLookupStrategy {

    private QueryExtractor extractor;

    private EntityManager entityManager;

    private QueryLookupStrategy jpaQueryLookupStrategy;

    private GenericQueryLookupStrategy(EntityManager entityManager, QueryExtractor extractor, Key key, EvaluationContextProvider evaluationContextProvider) {
        this.jpaQueryLookupStrategy = JpaQueryLookupStrategy.create(entityManager, key, extractor, evaluationContextProvider);
        this.entityManager = entityManager;
        this.extractor = extractor;
    }

    /**
     * 创建方法查询策略
     * @param entityManager
     * @param key
     * @param evaluationContextProvider
     * @return
     */
    public static QueryLookupStrategy create(EntityManager entityManager, QueryExtractor extractor, Key key, EvaluationContextProvider evaluationContextProvider) {
        return new GenericQueryLookupStrategy(entityManager, extractor, key, evaluationContextProvider);
    }

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries namedQueries) {
        NativeQuery nativeQuery = method.getAnnotation(NativeQuery.class);
        if (method.getAnnotation(NativeQuery.class) != null){
            return new NativeSqlQuery(new JpaQueryMethod(method, metadata, factory, extractor), entityManager, nativeQuery);
        }
        return jpaQueryLookupStrategy.resolveQuery(method, metadata, factory, namedQueries);
    }
}