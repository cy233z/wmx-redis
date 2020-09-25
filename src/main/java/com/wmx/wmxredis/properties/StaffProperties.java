package com.wmx.wmxredis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangMaoXiong
 * Created by Administrator on 2018/7/11 0011.
 * <p>
 * 用户···实体
 */
@ConfigurationProperties(prefix = "staff")
public class StaffProperties {
    private Integer id;
    /**
     * 如果 lastName 属性名称改为"name"时，注入的时候会强制变成计算机名称，而导致自己的值无法注入（原因未知）
     */
    private String lastName;
    private Integer age = 18;
    private Date birthday;
    private List<String> colorList;
    private Map<String, String> cityMap;
    /**
     * 关联的 Dog 对象
     */
    private Dog dog;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public List<String> getColorList() {
        return colorList;
    }

    public void setColorList(List<String> colorList) {
        this.colorList = colorList;
    }

    public Map<String, String> getCityMap() {
        return cityMap;
    }

    public void setCityMap(Map<String, String> cityMap) {
        this.cityMap = cityMap;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    @Override
    public String toString() {
        return "PersonProperties{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", birthday=" + birthday +
                ", colorList=" + colorList +
                ", cityMap=" + cityMap +
                ", dog=" + dog +
                '}';
    }
}