package com.github.boot.framework.jpa.dao;

import com.github.boot.framework.jpa.spec.PredicateBuilder;
import com.github.boot.framework.jpa.spec.Specifications;
import com.github.boot.framework.util.ReflectionUtils;
import com.github.boot.framework.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * BaseDao基于JPA规范的实现类
 * @author ChenJianhui
 * @param <T, ID>
 */
@NoRepositoryBean
public class BaseDaoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseDao<T, ID> {

	private final String BATCH_INSERT_SQL_PREFIX;

	private final String BATCH_INSERT_SQL_SUFFIX;

	protected Class<T> entityClass;

	private EntityManager entityManager;

	private JpaEntityInformation<T, ?> entityInfo;

	private List<SingularAttribute<? super T, ?>> attributes = new ArrayList<>();

	public BaseDaoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager){
		super(entityInformation, entityManager);
		this.entityInfo = entityInformation;
		this.entityClass = entityInformation.getJavaType();
		this.entityManager = entityManager;
		String tableName = entityClass.getAnnotation(Table.class).name();
		StringBuffer buffer = new StringBuffer("INSERT INTO ").append(tableName).append(" (");
		StringBuffer suffix = new StringBuffer("(");
		Metamodel metamodel = entityManager.getMetamodel();
		for(Attribute<? super T, ?> attr : metamodel.entity(entityClass).getAttributes()){
			if(!(attr instanceof SingularAttribute)){
				continue;
			}
			SingularAttribute<? super T, ?> sa = (SingularAttribute<? super T, ?>) attr;
			if(sa.isId()){
				Field field;
				try {
					field = entityClass.getDeclaredField(sa.getName());
				} catch (NoSuchFieldException e) {
					throw new RuntimeException(e);
				}
				GeneratedValue generate = field.getAnnotation(GeneratedValue.class);
				if(generate != null && generate.strategy() ==  GenerationType.IDENTITY){
					continue;
				}
			}
			attributes.add((SingularAttribute<? super T, ?>) attr);
			buffer.append(StringUtils.toUnderlineName(attr.getName())).append(',');
			suffix.append('?').append(',');
		}
		BATCH_INSERT_SQL_SUFFIX = suffix.deleteCharAt(suffix.length() - 1).append(')').toString();
		BATCH_INSERT_SQL_PREFIX = buffer.deleteCharAt(buffer.length() - 1).append(") VALUES ").toString();
	}

	@Override
	public void clear(){
		this.entityManager.clear();
	}

	@Override
	public List<T> findByIds(List<ID> ids) {
		return this.findAll(ids);
	}

	@Override
	@SuppressWarnings("unchecked")
    public List<T> findByIds(List<ID> ids, Map<String, Object> conditions) {
        CriteriaQuery<T> query = (CriteriaQuery<T>) entityManager.getCriteriaBuilder().createQuery();
        TypedQuery<T> q = entityManager.createQuery(query);
        for (Map.Entry<String, Object> e : conditions.entrySet()){
            q.setParameter(e.getKey(), e.getValue());
        }
        return q.getResultList();
    }

	@Override
	public long count(T t){
		if(t == null){
			return 0;
		}
		this.count(buildSpec(t));
		return 0;
	}

	@Override
	public T insert(T t) {
		entityManager.persist(t);
		return t;
	}

	@Override
	public <S extends T> S save(S entity) {
		super.save(entity);
		super.flush();
		return entity;
	}

	@Override
	public T update(T entity){
		CriteriaUpdate<T> criteriaUpdate = entityManager.getCriteriaBuilder().createCriteriaUpdate(getDomainClass());
		Root<T> root = criteriaUpdate.from(getDomainClass()) ;
		for(SingularAttribute<? super T, ?> attr : attributes){
			try {
				if(attr.isId()){
					continue;
				}
				PropertyDescriptor propertyDescriptor = new PropertyDescriptor(attr.getName(), entity.getClass());
				criteriaUpdate.set(attr.getName(), propertyDescriptor.getReadMethod().invoke(entity));
			} catch (Exception e) {
				continue;
			}
		}
		Iterator<String> iterator = entityInfo.getIdAttributeNames().iterator();
		List<Predicate> conditions = new ArrayList<>(2);
		while (iterator.hasNext()){
			conditions.add(entityManager.getCriteriaBuilder().equal(root.get(iterator.next()) , entityInfo.getId(entity)));
		}
		criteriaUpdate.where(conditions.toArray(new Predicate[conditions.size()])) ;
		entityManager.createQuery(criteriaUpdate).executeUpdate();
		return entity;
	}

	@Override
	public int update(T entity, String... properties){
		CriteriaUpdate<T> criteriaUpdate = entityManager.getCriteriaBuilder().createCriteriaUpdate(getDomainClass());
		Root<T> root = criteriaUpdate.from(getDomainClass()) ;
		for (String fieldName : properties) {
			try {
				PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, entity.getClass());
				criteriaUpdate.set(fieldName, propertyDescriptor.getReadMethod().invoke(entity));
			} catch (Exception e) {
				continue;
			}
		}
		Iterator<String> iterator = entityInfo.getIdAttributeNames().iterator();
		List<Predicate> conditions = new ArrayList<>(2);
        while (iterator.hasNext()){
            conditions.add(entityManager.getCriteriaBuilder().equal(root.get(iterator.next()) , entityInfo.getId(entity)));
		}
		criteriaUpdate.where(conditions.toArray(new Predicate[conditions.size()])) ;
		return entityManager.createQuery(criteriaUpdate).executeUpdate();
	}

	@Override
	public int batchSave(List<T> entities) {
		if(entities.size() == 1){
			this.save(entities.get(0));
			return 1;
		}
		StringBuffer buffer = new StringBuffer(BATCH_INSERT_SQL_PREFIX);
		Object[] params = new Object[attributes.size() * entities.size()];
		int index = 0;
		for (T t : entities){
			buffer.append(BATCH_INSERT_SQL_SUFFIX).append(",");
			for(Attribute<? super T, ?> attr : attributes){
				params[index] = ReflectionUtils.getFieldValue(t, attr.getName());
				index++;
			}
		}
		Query q = entityManager.createNativeQuery(buffer.deleteCharAt(buffer.length() - 1).append(';').toString());
		for(int i = 0 ; i < params.length ; i ++){
			q.setParameter(i+1,  params[i]);
		}
		return q.executeUpdate();
	}

	private Specification<T> buildSpec(T t){
		Field[] fields = t.getClass().getDeclaredFields();
		PredicateBuilder<T> builder = Specifications.<T>and();
		PropertyDescriptor propertyDescriptor;
        for (Field f : fields){
            if(f.getAnnotation(Transient.class) != null){
                continue;
            }
            Object value;
            try {
                propertyDescriptor = new PropertyDescriptor(f.getName(), t.getClass());
                value = propertyDescriptor.getReadMethod().invoke(t);
            } catch (Exception e) {
                continue;
            }
            if(value == null){
                continue;
            }
            builder.eq(f.getName(), value);
        }
        return builder.build();
    }

}
