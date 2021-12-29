package com.wmx.wmxredis.validator;

import javax.validation.GroupSequence;

/**
 * 定义 Spring Validation 校验分组，配合 spring 的 @Validated 注解使以及约束注解的 groups 属性使用。
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2021/12/29 19:54
 */
public class ValidGroup {

    /**
     * 新增使用(配合spring的@Validated功能分组使用)
     */
    public interface Insert {
    }

    /**
     * 更新使用(配合spring的@Validated功能分组使用)
     */
    public interface Update {
    }

    /**
     * 删除使用(配合spring的@Validated功能分组使用)
     */
    public interface Delete {
    }

    /**
     * 分组序列
     * 1、标记在约束注解的 groups 属性上时，表示相当于同时标记了这个序列中的全部分组
     * 2、标记在 @Validated 注解上时，同样表示相当于同时标记了这个序列中的全部分组
     * 3、比如嵌套校验时，把它标记在被关联对象的约束注解上。
     */
    @GroupSequence({Insert.class, Update.class, Delete.class})
    public interface All {

    }

}
