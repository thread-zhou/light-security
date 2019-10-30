package com.lightsecurity.core.web.builders;

import com.lightsecurity.core.filter.AnonymousAuthenticationFilter;
import com.lightsecurity.core.filter.ExceptionTranslationFilter;
import com.lightsecurity.core.filter.SecurityContextPersistenceFilter;
import com.lightsecurity.core.filter.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 仅供内部使用的{@link Comparator}, 对{@link Filter}实例进行排序, 以确保它们的顺序正确
 */
final public class FilterComparator implements Comparator<Filter>, Serializable {

    private static int STEP = 100;

    private Map<String, Integer> filterToOrder =  new HashMap<>();

    FilterComparator(){
        int order = 100;
        put(SecurityContextPersistenceFilter.class, order);
        order += STEP;//每个既定Filter之间步长为100
        put(CorsFilter.class, order);
        order += STEP;
        put(UsernamePasswordAuthenticationFilter.class, order);
        order += STEP;
        put(AnonymousAuthenticationFilter.class, order);
        order += STEP;
        put(ExceptionTranslationFilter.class, order);
    }

    @Override
    public int compare(Filter o1, Filter o2) {
        Integer left = getOrder(o1.getClass());
        Integer right = getOrder(o2.getClass());
        return left - right;
    }

    /**
     * 确定是否已注册特定的{@link Filter}, 并进行了排序
     * @param filter
     * @return
     */
    public boolean isRegistered(Class<? extends Filter> filter) {
        return getOrder(filter) != null;
    }

    /**
     * 注册一个{@link Filter}, 使其存在于已注册的特定的{@link Filter}之后
     * @param filter
     * @param afterFilter
     */
    public void registerAfter(Class<? extends Filter> filter, Class<? extends Filter> afterFilter) {
        Integer position = getOrder(afterFilter);
        if (position == null) {
            throw new IllegalArgumentException("Cannot register after unregistered Filter " + afterFilter);
        }
        put(filter, position + 1);
    }

    /**
     * 注册一个{@link Filter}以存在于特定的{@link Filter}位置
     * @param filter
     * @param atFilter
     */
    public void registerAt(Class<? extends Filter> filter, Class<? extends Filter> atFilter) {
        Integer position = getOrder(atFilter);
        if (position == null) {
            throw new IllegalArgumentException("Cannot register after unregistered Filter " + atFilter);
        }
        put(filter, position);
    }

    /**
     * 注册{@link Filter}, 使其在已注册的特定{@link Filter}之前存在
     * @param filter
     * @param beforeFilter
     */
    public void registerBefore(Class<? extends Filter> filter, Class<? extends Filter> beforeFilter){
        Integer position = getOrder(beforeFilter);
        if (position == null){
            throw new IllegalArgumentException("Cannot register after unregistered Filter " + beforeFilter);
        }
        put(filter, position-1);
    }


    private void put(Class<? extends Filter> filter, int position){
        String className = filter.getName();
        filterToOrder.put(className, position);
    }

    /**
     * 获取考虑了超类的特定{@link Filter}类的顺序
     * @param clazz
     * @return
     */
    private Integer getOrder(Class<?> clazz){
        while (clazz != null){
            Integer result = filterToOrder.get(clazz.getName());
            if (result != null){
                return result;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
}
