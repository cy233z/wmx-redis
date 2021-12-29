package com.wmx.wmxredis.validator;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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

    @NotBlank(groups = ValidGroup.Update.class)
    private String uid;

    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Length(min = 4, max = 16, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String userName;

    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @Length(min = 6, max = 18, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private String password;

    private Date birthday;

    private Boolean marry;

    @Min(value = 100, groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
    private Float salary;

    @MobileNumber(groups = {ValidGroup.Insert.class, ValidGroup.Update.class}, dec = "自定义Spring-Validation约束注解,校验手机号格式是否正确.")
    private String mobileNumber;

    @Valid
    @NotNull(groups = {ValidGroup.All.class})
    private UserCardDTO userCardDTO;

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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Boolean getMarry() {
        return marry;
    }

    public void setMarry(Boolean marry) {
        this.marry = marry;
    }

    public UserCardDTO getUserCardDTO() {
        return userCardDTO;
    }

    public void setUserCardDTO(UserCardDTO userCardDTO) {
        this.userCardDTO = userCardDTO;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "uid='" + uid + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                ", marry=" + marry +
                ", salary=" + salary +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", userCardDTO=" + userCardDTO +
                '}';
    }

    public class UserCardDTO {

        @NotBlank(groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
        private String userCardNum;

        @NotNull(groups = {ValidGroup.Insert.class, ValidGroup.Update.class})
        private Date publishTime;

        public String getUserCardNum() {
            return userCardNum;
        }

        public void setUserCardNum(String userCardNum) {
            this.userCardNum = userCardNum;
        }

        public Date getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(Date publishTime) {
            this.publishTime = publishTime;
        }

        @Override
        public String toString() {
            return "StuCardDTO{" +
                    "userCardNum='" + userCardNum + '\'' +
                    ", publishTime=" + publishTime +
                    '}';
        }
    }
}
