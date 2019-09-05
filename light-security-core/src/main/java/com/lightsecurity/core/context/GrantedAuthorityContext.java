package com.lightsecurity.core.context;

import com.lightsecurity.core.GrantedAuthority;

import java.util.Collection;

/**
 * <String, Collection<GrantedAuthority>>对应为UserId和该Id对应的权限集合对象
 * 该类用于存储用户权限信息
 */
public class GrantedAuthorityContext extends AbstractContext<String, Collection<GrantedAuthority>> {

}
