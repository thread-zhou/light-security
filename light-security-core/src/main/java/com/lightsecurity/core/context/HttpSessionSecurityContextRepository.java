package com.lightsecurity.core.context;

import com.lightsecurity.core.Authentication;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * SecurityContextRepository的一种实现，用于在请求之间存储SecurityContext
 * 参照spring security 中的 HttpSessionSecurityContextRepository 进行实现
 */
public class HttpSessionSecurityContextRepository implements SecurityContextRepository {

    private static final String LIGHT_SECURITY_CONTEXT_KEY = "LIGHT_SECURITY_CONTEXT_KEY";
    private static final String TOKEN_FIELD_NAME = "auth";
    private static final UserInfoSessionContextHolder userInfoSessionContextHolder = new UserInfoSessionContextHolder();
    private final SecurityContextHolder securityContextHolder = new SecurityContextHolder();

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String lightSecurityContextKey = LIGHT_SECURITY_CONTEXT_KEY;
    private String tokenHeaderKey = TOKEN_FIELD_NAME;

    public String getLightSecurityContextKey() {
        return lightSecurityContextKey;
    }

    public void setLightSecurityContextKey(String lightSecurityContextKey) {
        this.lightSecurityContextKey = lightSecurityContextKey;
    }

    public String getTokenHeaderKey() {
        return tokenHeaderKey;
    }

    public void setTokenHeaderKey(String tokenHeaderKey) {
        this.tokenHeaderKey = tokenHeaderKey;
    }

    /**
     * 先从session中获取context, 如果获取不到，则尝试从request中获取header为auth的属性
     * , 以该属性值为key从UserInfoSessionContext中获取值, 如果获取失败则返回一个SecurityContext的空实现
     * @param request
     * @return
     */
    private SecurityContext readSecurityContext(HttpServletRequest request){
        final boolean debug = logger.isDebugEnabled();
        boolean session_existed = true;//session中的SecurityContext是否存在
        HttpSession session = request.getSession(false);
        if (null == session){
            if (debug){
                logger.debug("当前HttpSession不存在");
            }
            session_existed = false;
        }
        Object context = session.getAttribute(lightSecurityContextKey);

        /**
         * 如果当前请求获取的session不存在或者当前请求的session中获取到的内容为null
         * , 则尝试从UserInfoSessionContext进行获取
         */
        if ((!session_existed) || (session_existed && null == context) ){
            String auth_header = request.getHeader(tokenHeaderKey);
            if (StringUtils.isBlank(auth_header)){
                if (debug){
                    logger.debug("当前请求的header中未设置 {} 属性", tokenHeaderKey);
                }
                return null;
            }
            session = userInfoSessionContextHolder.getContextWrapper().get(auth_header);//session from UserInfoSessionContext
            if (null == session){
                if (debug){
                    logger.debug("UserInfoSessionContext不存在适配当前请求的值");
                }
                return null;
            }
            context = session.getAttribute(lightSecurityContextKey);
        }

        if (null == context){
            if (debug){
                logger.debug("HttpSession中key为LIGHT_SECURITY_CONTEXT_KEY属性为null");
            }
        }

        if (!(context instanceof SecurityContext)){
            if (logger.isWarnEnabled()){
                logger.warn("通过lightSecurityContextKey获取到的对象不是SecurityContext的实例，而是: {}", context);
            }
        }
        if (debug){
            logger.debug("从 {} 中获取到有效的数据: {}", lightSecurityContextKey, context);
        }

        return (SecurityContext)context;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        SecurityContext context = readSecurityContext(request);
        if (null == context){
            if (logger.isDebugEnabled()){
                logger.debug("没有从HTTPSession或UserInfoSessionContext中获取到有效的SecurityContext. 将会创建一个空的SecurityContext实现.");
            }
            context = generateNewContext();
        }
        return context;
    }

    private SecurityContext generateNewContext() {
        return securityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        //简单进行实现, 对, 简单粗暴
        final Authentication authentication = context.getAuthentication();
        HttpSession session = request.getSession(false);
        if (authentication == null){
            if (logger.isDebugEnabled()){
                logger.debug("SecurityContext 是一个空的实现 - 不会使用HttpSession和UserInfoSessionContext进行存储");
            }
            if (null != session){
                session.removeAttribute(lightSecurityContextKey);
                userInfoSessionContextHolder.getContextWrapper().remove(session.getId());
            }
        }
        if (null != session){
            if (session.getAttribute(lightSecurityContextKey) == null && userInfoSessionContextHolder.getContextWrapper().get(session.getId()) == null){
                session.setAttribute(lightSecurityContextKey, context);
                userInfoSessionContextHolder.getContextWrapper().put(session.getId(), session);
                if (logger.isDebugEnabled()){
                    logger.debug("SecurityContext: {}, 被UserInfoSessionContext: {}和HttpSession: {}进行存储", context, userInfoSessionContextHolder.getContextWrapper(), session);
                }
            }
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return false;
    }
}
