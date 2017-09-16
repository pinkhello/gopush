package com.gopush.datacenter.restfuls.aop;

import com.gopush.datacenter.restfuls.loader.LoaderService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 下午12:35
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class RestfulLoaderAspect {

    @Autowired
    private LoaderService loaderService;

    @Pointcut("execution(public * com.gopush.datacenter.restfuls.controller..*.*(..))")
    public void loaderPoint() {

    }

    @Around("loaderPoint()")
    public Object loaderAround(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        loaderService.count(method);
        return pjp.proceed();
    }
}
