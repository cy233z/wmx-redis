package com.wmx.wmxredis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangMaoXiong
 * Created by Administrator on 2018/7/11 0011.
 * <p>
 * 用户···实体
 * @ConfigurationProperties 表示 告诉 Spring Boot 将本类中的所有属性和配置文件中相关的配置进行绑定；
 * prefix = "user" 表示 将配置文件中 key 为 user 的下面所有的属性与本类属性进行一一映射注入值，如果配置文件中
 * 不存在 "user" 的key，则不会为 POJO 注入值，属性值仍然为默认值
 * <p/>
 * @ConfigurationProperties (prefix = " user ") 默认从全局配置文件中获取值然后进行注入
 * @Component 将本类标识为一个Spring 组件，因为只有是容器中的组件，容器才会为 @ConfigurationProperties 提供此注入功能
 */
@Component
@ConfigurationProperties(prefix = "user")
public class UserProperties {
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
     * 关联的 Dog 对象可以不加 @ConfigurationProperties 也会自动注入
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
        return "UserProperties{" +
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