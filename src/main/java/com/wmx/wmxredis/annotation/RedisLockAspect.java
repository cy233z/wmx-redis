package com.wmx.wmxredis.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * AOP 切面拦截 自定义 Redis 分布式锁注解
 * <p>
 * * 1、@Aspect：声明本类为切面类
 * * 2、@Component：将本类交由 Spring 容器管理
 * * 3、@Order：指定切入执行顺序，数值越小，切面执行顺序越靠前，默认为 Integer.MAX_VALUE
 *
 * @author wangMaoXiong
 * @version 1.0
 * @date 2022/11/20 16:55
 */
@Aspect
@Order(value = 999)
@Component
public class RedisLockAspect {

    private static final Logger LOG = LoggerFactory.getLogger(RedisLockAspect.class);

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @Pointcut ：切入点声明，即切入到哪些目标方法。
     * execution：可以用于指定具体类中的具体方法
     * annotation：可以直接用于拦截指定注解的方法
     * 用于被下面的通知注解引用，这样通知注解只需要关联此切入点声明即可，无需再重复写切入点表达式
     * @annotation 中的路径表示拦截特定注解
     */
    @Pointcut("@annotation(com.wmx.wmxredis.annotation.RedisLock)")
    public void redisLockPC() {
    }

    /**
     * 环绕通知
     * 1、@Around 的 value 属性：绑定通知的切入点表达式。可以关联切入点声明，也可以直接设置切入点表达式
     * 2、Object ProceedingJoinPoint.proceed(Object[] args) 方法：继续下一个通知或目标方法调用，返回处理结果，如果目标方法发生异常，则 proceed 会抛异常.
     * 3、假如目标方法是控制层接口，则本方法的异常捕获与否都不会影响目标方法的事务回滚
     * 4、假如目标方法是控制层接口，本方法 try-catch 了异常后没有继续往外抛，则全局异常处理 @RestControllerAdvice 中不会再触发
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "redisLockPC()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解所在的目标方法
        Method method = resolveMethod(joinPoint);
        // 获取方法上注解
        RedisLock annotation = method.getAnnotation(RedisLock.class);
        Object[] args = joinPoint.getArgs();
        Object proceed;
        boolean ifAbsent = false;

        //指定信息摘要算法提取摘要的哈希值. 哈希值字节数组，如果直接 new String(md5Byte) 是会乱码的
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        byte[] md5Byte = messageDigest.digest(Arrays.asList(args).toString().getBytes());
        //使用 BASE64 进行定长编码
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String key = base64Encoder.encode(md5Byte);
        String value = key;
        try {
            LOG.info("自定义 Redis 分布式锁注解：方法={} 参数={} 特定参数标识={} 超时重试次数={} 释放时间={} 描述信息={}",
                    joinPoint.getSignature(), Arrays.asList(args), annotation.lockFiled(), annotation.retryCount(),
                    annotation.lockTime(), annotation.desc());

            ifAbsent = redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(annotation.lockTime()));
            Assert.isTrue(ifAbsent, "程序正在处理中，请稍后再试！");

            // 继续下一个通知或目标方法调用，返回处理结果，如果目标方法发生异常，则 proceed 会抛异常.
            // 如果在调用目标方法或者下一个切面通知前抛出异常，则不会再继续往后走.
            proceed = joinPoint.proceed(joinPoint.getArgs());
        } finally {
            if (ifAbsent) {
                // 接口执行完毕后删除 key，key 不存在时 execute 方法返回 0
                // 此种脚本删除的方式在 redis 集群部署时会报错，实际上直接使用  redisTemplate.delete(cacheKey) 也是可以的
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
                // 返回删除key的个数，未删除成功时，返回 0
                redisTemplate.execute(redisScript, Arrays.asList(key), value);
            }
        }
        return proceed;
    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        // 返回连接点处的方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 返回目标对象
        Class<?> targetClass = joinPoint.getTarget().getClass();

        // 获取指定类上的指定方法
        Method method = getDeclaredMethodFor(targetClass, signature.getName(), signature.getMethod().getParameterTypes());
        Assert.isTrue(method != null, "Cannot resolve target method: " + signature.getMethod().getName());
        return method;
    }

    /**
     * 获取指定类上的指定方法
     *
     * @param clazz          指定类
     * @param methodName     指定方法
     * @param parameterTypes 参数类型列表
     * @return 找到就返回method，否则返回null
     */
    public static Method getDeclaredMethodFor(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, methodName, parameterTypes);
            }
        }
        return null;
    }
}
