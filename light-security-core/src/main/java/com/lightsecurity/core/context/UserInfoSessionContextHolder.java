package com.lightsecurity.core.context;

import javax.servlet.http.HttpSession;

public final class UserInfoSessionContextHolder extends AbstractContextHolder<String, HttpSession> {

    public UserInfoSessionContextHolder() {
        super(USER_INFO_SESSION_CONTEXT_HOLDER_STRATEGY);
    }
}
