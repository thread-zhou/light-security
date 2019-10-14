package com.lightsecurity.core.config;

import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.config.annotation.SecurityConfigurer;
import com.lightsecurity.core.config.annotation.web.AutowiredWebSecurityConfigurersIgnoreParents;
import com.lightsecurity.core.config.annotation.web.WebSecurityConfigurerAdapter;
import com.lightsecurity.core.web.builders.WebSecurity;
import com.lightsecurity.core.web.context.AbstractSecurityWebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

@Configuration
public class WebSecurityConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public WebSecurityConfiguration(){
        logger.debug("开始执行WebSecurityConfiguration...");
    }

    private WebSecurity webSecurity;

    private List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers;

    /**
     * 注入{@link com.lightsecurity.core.config.annotation.web.configuration.ObjectPostProcessorConfiguration}中注册的<code>ObjectPostProcessor</code>
     */
    @Autowired(required = false)
    private ObjectPostProcessor<Object> objectPostProcessor;

    @Bean(name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
    public Filter lightSecurityFilterChain() throws Exception{
        boolean hasConfigurers = webSecurityConfigurers != null && !webSecurityConfigurers.isEmpty();
        //如果webSecurityConfigurers为空, 则会创建默认的配置器
        if (!hasConfigurers){
            //WebSecurityConfigurerAdapter匿名内部类实现
            WebSecurityConfigurerAdapter adapter = objectPostProcessor
                    .postProcess(new WebSecurityConfigurerAdapter() {

                    });
            webSecurity.apply(adapter);
        }
        return webSecurity.build();
    }

    /**
     * 在这里初始化了AutowiredWebSecurityConfigurersIgnoreParents并交由Spring进行管理
     * @param beanFactory
     * @return
     */
    @Bean
    public AutowiredWebSecurityConfigurersIgnoreParents autowiredWebSecurityConfigurersIgnoreParents(ConfigurableListableBeanFactory beanFactory){
        return new AutowiredWebSecurityConfigurersIgnoreParents(beanFactory);
    }

    /**
     * 创建WebSecurity
     * 收集自定义的WebSecurityConfigurer的子类实例并配置到创建的WebSecurity实例中
     * @param objectPostProcessor
     * @param webSecurityConfigurers
     * @throws Exception
     */
    @Autowired(required = false)
    public void setFilterChainProxySecurityConfigurer(
            ObjectPostProcessor<Object> objectPostProcessor,
            @Value("#{@autowiredWebSecurityConfigurersIgnoreParents.getWebSecurityConfigurers()}")  List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers) throws Exception{

        //创建一个webSecurity对象
        webSecurity = objectPostProcessor.postProcess(new WebSecurity(objectPostProcessor));

        //对webSecurityConfigurers进行排序
        Collections.sort(webSecurityConfigurers, AnnotationAwareOrderComparator.INSTANCE);

        //对Order进行比较是否存在相同的,如此简单的比较是在经过排序的前提下进行的
        Integer previousOrder = null;
        Object previousConfig = null;
        for (SecurityConfigurer<Filter, WebSecurity> config : webSecurityConfigurers){
            Integer order = AnnotationAwareOrderComparator.lookupOrder(config);
            if (previousOrder != null && previousOrder.equals(order)){
                throw new IllegalStateException(
                        "@Order on WebSecurityConfigurers must be unique. Order of "
                                + order + " was already used on " + previousConfig + ", so it cannot be used on "
                                + config + " too.");
            }
            previousOrder = order;
            previousConfig = config;
        }

        for (SecurityConfigurer<Filter, WebSecurity> webSecurityConfigurer : webSecurityConfigurers){
            //将配置器信息配置到WebSecurity中
            webSecurity.apply(webSecurityConfigurer);
        }
        this.webSecurityConfigurers =  webSecurityConfigurers;//将方法内的局部变量赋值给类成员变量
    }

    private static class AnnotationAwareOrderComparator extends OrderComparator {
        private static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();

        @Override
        protected int getOrder(Object obj) {
            return lookupOrder(obj);
        }

        private static int lookupOrder(Object obj) {
            if (obj instanceof Ordered) {
                return ((Ordered) obj).getOrder();
            }
            if (obj != null) {
                Class<?> clazz = (obj instanceof Class ? (Class<?>) obj : obj.getClass());
                Order order = AnnotationUtils.findAnnotation(clazz, Order.class);
                if (order != null) {
                    return order.value();
                }
            }
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

}
