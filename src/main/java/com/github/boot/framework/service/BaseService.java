package com.github.boot.framework.service;

import com.github.boot.framework.web.form.PageForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Service 通用基类
 * @author ChenJianhui
 * @param <T>
 */
@Transactional
public interface BaseService<T, ID extends Serializable> {
	
	/**
	 * 根据删除一个实体
	 * @param id
	 */
	void delete(ID id);
	
	/**
	 * 根据实体对象删除一个实体
	 * @param entity
	 */
	void delete(T entity) ;
	
	/**
	 * 根据实体集合批量删除实体
	 * @param entities
	 */
	void delete(Iterable<? extends T> entities);
	
	/**
	 * 分页查询
	 * @param form
	 * @return
	 */
	Page<T> page(PageForm<T> form);
	
	/**
	 * 根据ID查询一个实体
	 * @param id
	 * @return
	 */
	T findOne(ID id);
	
	/**
	 * 根据ID判断一个实体是否存在
	 * @param id
	 * @return
	 */
	boolean exists(ID id);
	
	/**
	 * 查询所有的实体
	 * @return
	 */
	List<T> findAll() ;
	
	/**
	 * 根据ID集合查询实体集合
	 * @param ids
	 * @return
	 */
	List<T> findAll(Iterable<ID> ids);
	
	/**
	 * 排序查询所有实体
	 * @param sort
	 * @return
	 */
	List<T> findAll(Sort sort) ;
	
	/**
	 * 分页查询所有的实体
	 * @param pageable
	 * @return
	 */
	Page<T> findAll(Pageable pageable);
	
	/**
	 * 按自定义条件查询一个实体
	 * @param spec
	 * @return
	 */
	T findOne(Specification<T> spec);

	/**
	 * 按自定义条件查询所有实体
	 * @param spec
	 * @return
	 */
	List<T> findAll(Specification<T> spec) ;
	
	/**
	 * 按自定义条件分页查询所有实体
	 * @param spec
	 * @param pageable
	 * @return
	 */
	Page<T> findAll(Specification<T> spec, Pageable pageable) ;
	
	/**
	 * 根据自定义条件排序查询实体
	 * @param spec
	 * @param sort
	 * @return
	 */
	List<T> findAll(Specification<T> spec, Sort sort);
	
	/**
	 * 统计所有实体的数量
	 * @return
	 */
	long count() ;
	
	/**
	 * 按条件统计实体的数量
	 * @param spec
	 * @return
	 */
	long count(Specification<T> spec) ;
	
	/**
	 * 保存后更新一个实体, 此方法不建议使用， 已经拆成成两个方法 insert()和update()
	 * @param entity
	 * @return
	 */
	@Deprecated
	T save(T entity);
	
	/**
	 * 保存一个实体
	 * @param entity
	 * @return
	 */
	T insert(T entity);
	
	/**
	 * 更新一个实体
	 * @param entity
	 * @return
	 */
	T update(T entity);
	
	/**
	 * 批量保存更新实体
	 * @param entities
	 * @return
	 */
	int save(List<T> entities);


}
