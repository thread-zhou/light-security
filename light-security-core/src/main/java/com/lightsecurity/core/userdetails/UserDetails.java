package com.lightsecurity.core.userdetails;

import com.lightsecurity.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

public interface UserDetails extends Serializable {

    /**
     * 返回授予用户的权限
     * @return
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * 返回用于验证用户身份的密码
     * @return
     */
    String getPassword();

    /**
     * 返回用于验证用户身份的用户名
     * @return
     */
    String getUsername();

    /**
     * 指示用户的证书数据是否已经过期
     * @return true表示未过期， false表示已过期
     */
    boolean isCredentialsNonExpired();

    /**
     * 指示用户是否可用，是否被禁用
     * @return true表示用户已启用， false表示用户被禁用
     */
    boolean isEnabled();

}
