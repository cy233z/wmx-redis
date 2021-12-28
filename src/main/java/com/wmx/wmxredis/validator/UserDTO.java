package com.wmx.wmxredis.validator;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 人员信息实体类
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/28 11:11
 */
public class UserDTO implements Serializable {

    private static final long serialVersionUID = -4874404701375841686L;

    private String uid;

    @javax.validation.constraints.NotNull
    @org.hibernate.validator.constraints.Length(min = 4, max = 16)
    private String userName;
    @NotNull
    @Length(min = 6, max = 18)
    private String password;
    private Date birthday;
    private Float salary;
    private Boolean marry;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public Boolean getMarry() {
        return marry;
    }

    public void setMarry(Boolean marry) {
        this.marry = marry;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", salary=" + salary +
                ", marry=" + marry +
                '}';
    }
}
