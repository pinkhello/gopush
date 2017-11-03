package com.gopush.datacenter.restfuls.loader;

import com.gopush.infos.datacenter.bo.RestfulLoaderInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 下午12:56
 */
@Slf4j
@Component
public class LoaderService {

    private static final int INT_ZERO = 0;
    private static final int INT_MAX_VAL = Integer.MAX_VALUE - 1;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private Map<List<String>, AtomicInteger> restfulUrlCounters = new ConcurrentHashMap<>();
    private Map<Method, List<String>> methodRestfulUrls = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        handlerMethods.forEach((k, v) -> {
            List<String> urls = k.getPatternsCondition().getPatterns().stream().sorted().collect(Collectors.toList());
            methodRestfulUrls.put(v.getMethod(), urls);
            restfulUrlCounters.put(urls, new AtomicInteger(INT_ZERO));
        });

    }

    @PreDestroy
    public void destory() {
        methodRestfulUrls.clear();
        methodRestfulUrls = null;
        restfulUrlCounters.clear();
        restfulUrlCounters = null;
    }

    public List<RestfulLoaderInfo> restfulLoader() {
        List<RestfulLoaderInfo> list = new ArrayList<>();
        restfulUrlCounters.forEach((k, v) -> list.add(RestfulLoaderInfo.builder().callCounter(v.get()).restfulUrl(new ArrayList(k)).build()));
        return list;
    }


    public void count(Method method) {
        if (methodRestfulUrls.containsKey(method)) {
            AtomicInteger count = restfulUrlCounters.get(methodRestfulUrls.get(method));
            int c = count.incrementAndGet();
            if (c >= INT_MAX_VAL) {
                count.set(INT_ZERO);
            }
        }
    }


}
