package com.github.boot.framework.jpa.strategy;

import com.github.boot.framework.util.AopUtils;
import com.github.boot.framework.jpa.NativeQuery;
import com.github.boot.framework.util.ReflectionUtils;
import org.hibernate.SQLQuery;
import org.hibernate.jpa.internal.QueryImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParameters;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.query.Parameter;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 原生SQL查询
 * Created by cjh on 2017/7/15.
 */
@SuppressWarnings("rawtypes")
public class NativeSqlQuery extends AbstractJpaQuery {

    private NativeQuery nativeQuery;

    private Class<?> resultType;

    public NativeSqlQuery(JpaQueryMethod method, EntityManager em, NativeQuery nativeQuery) {
        super(method, em);
        this.nativeQuery = nativeQuery;
        if(nativeQuery.resultType() == Void.class){
            resultType = method.getReturnedObjectType();
            if(resultType.isAssignableFrom(Collection.class)){
                ClassTypeInformation<?> ctif = ClassTypeInformation.from(resultType);
                TypeInformation<?> actualType = ctif.getActualType();
                resultType = actualType.getType();
            }
        }else{
            resultType = nativeQuery.resultType();
        }
    }

    @Override
    protected Query doCreateQuery(Object[] values) {
        String nativeQuery = this.nativeQuery.value();
        JpaParameters parameters = getQueryMethod().getParameters();
        ParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
        String sortedQueryString = QueryUtils.applySorting(nativeQuery, accessor.getSort(), QueryUtils.detectAlias(nativeQuery));
        Query query = bind(createJpaQuery(sortedQueryString), values);
        if (parameters.hasPageableParameter()) {
            Pageable pageable = (Pageable) (values[parameters.getPageableIndex()]);
            if (pageable != null) {
                query.setFirstResult(pageable.getOffset());
                query.setMaxResults(pageable.getPageSize());
            }
        }
        return query;
    }

    private Map<String, Object> getParams(Object[] values) {
        JpaParameters parameters = getQueryMethod().getParameters();
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < parameters.getNumberOfParameters(); i++) {
            Object value = values[i];
            Parameter parameter = parameters.getParameter(i);
            if (value != null && canBindParameter(parameter)) {
                if (!QueryBuilder.isValidValue(value)) {
                    continue;
                }
                if (ReflectionUtils.isComplexType( value.getClass())) {
                    params.putAll(QueryBuilder.toParams(value));
                } else {
                    params.put(parameter.getName(), value);
                }
            }
        }
        return params;
    }

    public QueryImpl createJpaQuery(String queryString) {
        Class<?> objectType = getQueryMethod().getReturnedObjectType();
        QueryImpl query;
        if (getQueryMethod().isQueryForEntity()) {
            return AopUtils.getTarget(getEntityManager().createNativeQuery(queryString, objectType));
        }
        query = AopUtils.getTarget(getEntityManager().createNativeQuery(queryString));
        QueryBuilder.transform(query.getHibernateQuery(), this.resultType);
        return query;
    }

	@Override
	@SuppressWarnings("unchecked")
    protected TypedQuery<Long> doCreateCountQuery(Object[] values) {
        QueryImpl nativeQuery = AopUtils.getTarget(getEntityManager().createNativeQuery(QueryBuilder.toCountQuery(this.nativeQuery.value())));
        return bind(nativeQuery, values);
    }

    public QueryImpl bind(QueryImpl query, Object[] values) {
        SQLQuery sqlQuery = (SQLQuery) query.getHibernateQuery();
        Map<String, Object> params = getParams(values);
        if (!CollectionUtils.isEmpty(params)) {
            QueryBuilder.setParams(sqlQuery, params);
        }
        return query;
    }

    protected boolean canBindParameter(Parameter parameter) {
        return parameter.isBindable();
    }
}

