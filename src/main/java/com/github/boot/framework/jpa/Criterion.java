package com.github.boot.framework.jpa;

/**
 * 构建查询条件的接口
 * 在实现类的属性加@Condition注解， @SortProperty, @SortDirection
 * 即可实现参数绑定
 *
 * @author cjh
 * @date 2017/6/21
 */
public interface Criterion<T> {

    /**
     * 获取查询起始位置
     * @return
     */
    default Integer getPage(){
        return null;
    }

    /**
     * 获取查询的行数
     * @return
     */
    default Integer getSize(){
        return null;
    }

}
