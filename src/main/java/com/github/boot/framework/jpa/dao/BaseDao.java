package com.github.boot.framework.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DAO基类
 * @author ChenJianhui
 * @param <T>
 */
@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>{


	/**
	 * 根据多个ID查询实体
	 * @param ids
	 * @return
	 */
	List<T> findByIds(List<ID> ids);

	/**
	 * 根据多个ID以及其他条件查询实体
	 * @param ids
	 * @return
	 */
	List<T> findByIds(List<ID> ids, Map<String, Object> conditions);

	/**
	 * 清除当前的持久化上下文
	 */
	void clear();

	/**
	 * 根据实体统计查询
	 * @param t
	 * @return
	 */
	long count(T t);

	/**
	 * 插入实体
	 * @return
	 */
	T insert(T t);

	/**
	 * 更新实体
	 * @return
	 */
	T update(T t);

	/**
	 * 更新实体类的指定字段
	 * @param entity
	 * @param properties
	 * @return
	 */
	int update(T entity, String... properties);


	/**
	 * 批量插入
	 * @param entites
	 * @return
	 */
    int batchSave(List<T> entites);

	/**
	 * 批量更新
	 * @param sql
	 * @param params
	 * @return
	 */
	int executeBatchBySQL(String sql, List<Object[]> params);

}
