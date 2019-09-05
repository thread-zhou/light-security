package com.lightsecurity.core;

import java.io.Serializable;

/**
 * Represents an authority granted to an {@link Authentication} object.
 * 表示授予{@link Authentication}的权限
 */
public interface GrantedAuthority extends Serializable {

    Permission getAuthority();

}
