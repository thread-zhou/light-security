package com.lightsecurity.core.context;

import javax.servlet.http.HttpSession;

/**
 * 用户信息容器实现
 */
public final class UserInfoSessionContextHolderHolderStrategy extends AbstractContextHolderStrategy<String, HttpSession> {

    public UserInfoSessionContextHolderHolderStrategy(UserInfoSessionContext context) {
        super(context);
    }

}
