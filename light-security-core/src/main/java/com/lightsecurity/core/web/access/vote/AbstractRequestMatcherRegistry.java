package com.lightsecurity.core.web.access.vote;

import com.lightsecurity.core.config.annotation.ObjectPostProcessor;
import com.lightsecurity.core.util.matcher.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractRequestMatcherRegistry<C> {
    private static final RequestMatcher ANY_REQUEST = AnyRequestMatcher.INSTANCE;

    private ApplicationContext context;

    protected final void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Gets the {@link ApplicationContext}
     *
     * @return the {@link ApplicationContext}
     */
    protected final ApplicationContext getApplicationContext() {
        return this.context;
    }

    /**
     * Maps any request.
     *
     * @return the object that is chained after creating the {@link RequestMatcher}
     */
    public C anyRequest() {
        return requestMatchers(ANY_REQUEST);
    }

    public C antMatchers(HttpMethod method) {
        return antMatchers(method, new String[] { "/**" });
    }


    public C antMatchers(HttpMethod method, String... antPatterns) {
        return chainRequestMatchers(RequestMatchers.antMatchers(method, antPatterns));
    }

    public C antMatchers(String... antPatterns) {
        return chainRequestMatchers(RequestMatchers.antMatchers(antPatterns));
    }

    /**
     * <p>
     * Maps an {@link MvcRequestMatcher} that does not care which {@link HttpMethod} is
     * used. This matcher will use the same rules that Spring MVC uses for matching. For
     * example, often times a mapping of the path "/path" will match on "/path", "/path/",
     * "/path.html", etc.
     * </p>
     * <p>
     * If the current request will not be processed by Spring MVC, a reasonable default
     * using the pattern as a ant pattern will be used.
     * </p>
     *
     * @param mvcPatterns the patterns to match on. The rules for matching are defined by
     * Spring MVC
     * @return the object that is chained after creating the {@link RequestMatcher}.
     */
    public abstract C mvcMatchers(String... mvcPatterns);

    /**
     * <p>
     * Maps an {@link MvcRequestMatcher} that also specifies a specific {@link HttpMethod}
     * to match on. This matcher will use the same rules that Spring MVC uses for
     * matching. For example, often times a mapping of the path "/path" will match on
     * "/path", "/path/", "/path.html", etc.
     * </p>
     * <p>
     * If the current request will not be processed by Spring MVC, a reasonable default
     * using the pattern as a ant pattern will be used.
     * </p>
     *
     * @param method the HTTP method to match on
     * @param mvcPatterns the patterns to match on. The rules for matching are defined by
     * Spring MVC
     * @return the object that is chained after creating the {@link RequestMatcher}.
     */
    public abstract C mvcMatchers(HttpMethod method, String... mvcPatterns);

    /**
     * Creates {@link MvcRequestMatcher} instances for the method and patterns passed in
     *
     * @param method the HTTP method to use or null if any should be used
     * @param mvcPatterns the Spring MVC patterns to match on
     * @return a List of {@link MvcRequestMatcher} instances
     */
    protected final List<MvcRequestMatcher> createMvcMatchers(HttpMethod method,
                                                              String... mvcPatterns) {
        boolean isServlet30 = ClassUtils.isPresent("javax.servlet.ServletRegistration", getClass().getClassLoader());
        ObjectPostProcessor<Object> opp = this.context.getBean(ObjectPostProcessor.class);
        HandlerMappingIntrospector introspector = new HandlerMappingIntrospector(
                this.context);
        List<MvcRequestMatcher> matchers = new ArrayList<MvcRequestMatcher>(
                mvcPatterns.length);
        for (String mvcPattern : mvcPatterns) {
            MvcRequestMatcher matcher = new MvcRequestMatcher(introspector, mvcPattern);
            if (isServlet30) {
                opp.postProcess(matcher);
            }
            if (method != null) {
                matcher.setMethod(method);
            }
            matchers.add(matcher);
        }
        return matchers;
    }


    public C regexMatchers(HttpMethod method, String... regexPatterns) {
        return chainRequestMatchers(RequestMatchers.regexMatchers(method, regexPatterns));
    }


    public C regexMatchers(String... regexPatterns) {
        return chainRequestMatchers(RequestMatchers.regexMatchers(regexPatterns));
    }

    /**
     * Associates a list of {@link RequestMatcher} instances with the
     * {@link AbstractConfigAttributeRequestMatcherRegistry}
     *
     * @param requestMatchers the {@link RequestMatcher} instances
     *
     * @return the object that is chained after creating the {@link RequestMatcher}
     */
    public C requestMatchers(RequestMatcher... requestMatchers) {
        return chainRequestMatchers(Arrays.asList(requestMatchers));
    }

    /**
     * Subclasses should implement this method for returning the object that is chained to
     * the creation of the {@link RequestMatcher} instances.
     *
     * @param requestMatchers the {@link RequestMatcher} instances that were created
     * @return the chained Object for the subclass which allows association of something
     * else to the {@link RequestMatcher}
     */
    protected abstract C chainRequestMatchers(List<RequestMatcher> requestMatchers);

    /**
     * Utilities for creating {@link RequestMatcher} instances.
     *
     * @author Rob Winch
     * @since 3.2
     */
    private static final class RequestMatchers {

        /**
         * Create a {@link List} of {@link AntPathRequestMatcher} instances.
         *
         * @param httpMethod the {@link HttpMethod} to use or {@code null} for any
         * {@link HttpMethod}.
         * @param antPatterns the ant patterns to create {@link AntPathRequestMatcher}
         * from
         *
         * @return a {@link List} of {@link AntPathRequestMatcher} instances
         */
        public static List<RequestMatcher> antMatchers(HttpMethod httpMethod,
                                                       String... antPatterns) {
            String method = httpMethod == null ? null : httpMethod.toString();
            List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
            for (String pattern : antPatterns) {
                matchers.add(new AntPathRequestMatcher(pattern, method));
            }
            return matchers;
        }

        public static List<RequestMatcher> antMatchers(String... antPatterns) {
            return antMatchers(null, antPatterns);
        }

        /**
         * Create a {@link List} of {@link RegexRequestMatcher} instances.
         *
         * @param httpMethod the {@link HttpMethod} to use or {@code null} for any
         * {@link HttpMethod}.
         * @param regexPatterns the regular expressions to create
         * {@link RegexRequestMatcher} from
         *
         * @return a {@link List} of {@link RegexRequestMatcher} instances
         */
        public static List<RequestMatcher> regexMatchers(HttpMethod httpMethod,
                                                         String... regexPatterns) {
            String method = httpMethod == null ? null : httpMethod.toString();
            List<RequestMatcher> matchers = new ArrayList<RequestMatcher>();
            for (String pattern : regexPatterns) {
                matchers.add(new RegexRequestMatcher(pattern, method));
            }
            return matchers;
        }

        /**
         * Create a {@link List} of {@link RegexRequestMatcher} instances that do not
         * specify an {@link HttpMethod}.
         *
         * @param regexPatterns the regular expressions to create
         * {@link RegexRequestMatcher} from
         *
         * @return a {@link List} of {@link RegexRequestMatcher} instances
         */
        public static List<RequestMatcher> regexMatchers(String... regexPatterns) {
            return regexMatchers(null, regexPatterns);
        }

        private RequestMatchers() {
        }
    }

}
