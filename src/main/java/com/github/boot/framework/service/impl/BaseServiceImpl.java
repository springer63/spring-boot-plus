package com.github.boot.framework.service.impl;

import com.github.boot.framework.jpa.Criterion;
import com.github.boot.framework.jpa.dao.BaseDao;
import com.github.boot.framework.jpa.spec.SpecificationParser;
import com.github.boot.framework.service.BaseService;
import com.github.boot.framework.web.result.PageWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;


/**
 * BaseService 抽象实现类
 * @author ChenJianhui
 * @param <T>
 */
@Transactional(rollbackFor = Throwable.class)
public abstract class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {
	
	@Autowired(required = false)
	protected BaseDao<T, ID> dao;
	
	protected Class<T> clazz;
	
	@SuppressWarnings("unchecked")
	public BaseServiceImpl() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		clazz = (Class<T>) type.getActualTypeArguments()[0];
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(ID id) {
		dao.delete(id);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(T entity) {
		dao.delete(entity);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void delete(Iterable<? extends T> entities) {
		dao.delete(entities);
	}

	@Override
	public T findOne(ID id){
		return dao.findOne(id);
	}

	@Override
	public boolean exists(ID id) {
		return dao.exists(id);
	}

	@Override
	public Page<T> page(Criterion<T> criterion){
		Page<T> page = this.findAll(SpecificationParser.condition(criterion), SpecificationParser.pageable(criterion));
		return PageWrap.wrap((PageImpl<T>) page);
	}

	@Override
	public List<T> findAll() {
		return dao.findAll();
	}

	@Override
	public List<T> findAll(Criterion<T> criterion) {
		Sort sort = SpecificationParser.sort(criterion);
		if(sort != null){
			return dao.findAll(SpecificationParser.condition(criterion), sort);
		}
		return dao.findAll(SpecificationParser.condition(criterion));
	}

	@Override
	public List<T> findAll(Iterable<ID> ids) {
		return dao.findAll(ids);
	}

	@Override
	public List<T> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return dao.findAll(pageable);
	}

	@Override
	public T findOne(Specification<T> spec) {
		return dao.findOne(spec);
	}

	@Override
	public List<T> findAll(Specification<T> spec) {
		return dao.findAll(spec);
	}

	@Override
	public Page<T> findAll(Specification<T> spec, Pageable pageable) {
		return dao.findAll(spec, pageable);
	}

	@Override
	public List<T> findAll(Specification<T> spec, Sort sort) {
		return dao.findAll(spec, sort);
	}

	@Override
	public long count() {
		return dao.count();
	}

	@Override
	public long count(Specification<T> spec) {
		return dao.count(spec);
	}

	@Override
	@Transactional
	public T insert(T entity) {
		return dao.insert(entity);
	}
	
	@Override
	@Transactional
	public T update(T entity) {
		return dao.update(entity);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public int save(List<T> entities) {
		return dao.batchSave(entities);
	}

}
