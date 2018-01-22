package com.github.boot.framework.jpa;

/**
 * 操作符
 *
 * @author cjh
 * @date 2017/3/15
 */
public enum Operator {

    /**
     * 相等
     */
    EQ ,
    /**
     * 大于
     */
    GT ,
    /**
     * 大于等于
     */
    GE ,
    /**
     * 小于
     */
    LT ,
    /**
     * 小于等于
     */
    LE,
    /**
     * 不等于
     */
    NQ,
    /**
     * 通配
     */
    LIKE,
    /**
     * 在集合之内
     */
    IN,
    /**
     * 不在集合之内
     */
    NOT_IN,
    /**
     * 范围下限
     */
    BETWEEN_LOWER,

    /**
     * 范围上限
     */
    BETWEEN_UPPER;
}
