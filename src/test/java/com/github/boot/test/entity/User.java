package com.github.boot.test.entity;

import com.github.boot.framework.jpa.entity.BaseEntity;

import javax.persistence.*;

/**
 * ${DESCRIPTION}
 *
 * @author chenjianhui
 * @create 2018/04/08
 **/
@Entity
@Table(name = "user")
public class User extends BaseEntity<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private int age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
