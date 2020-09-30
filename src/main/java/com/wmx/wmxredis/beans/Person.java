package com.wmx.wmxredis.beans;

import java.util.Date;

/**
 * 人员实体
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2020/9/24 17:05
 */
public class Person {
    private Integer id;
    private String name;
    private Date birthday;

    public Person() {
    }

    public Person(Integer id, String name, Date birthday) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
