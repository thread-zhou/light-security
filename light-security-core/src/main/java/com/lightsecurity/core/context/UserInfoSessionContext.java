package com.lightsecurity.core.context;

import javax.servlet.http.HttpSession;

/**
 * <String, HttpSession>对应为SessionId和Session对象
 * 该类用于存储用户会话信息
 */
public class UserInfoSessionContext extends AbstractContext<String, HttpSession> {

}
