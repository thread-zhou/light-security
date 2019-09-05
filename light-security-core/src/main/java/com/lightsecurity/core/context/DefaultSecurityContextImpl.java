package com.lightsecurity.core.context;

import com.lightsecurity.core.Authentication;

/**
 * SecurityContext 的默认实现
 */
public class DefaultSecurityContextImpl implements SecurityContext {

    private Authentication authentication;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DefaultSecurityContextImpl){
            DefaultSecurityContextImpl temp = (DefaultSecurityContextImpl)obj;
            if ((this.getAuthentication() == null) && (temp.getAuthentication() == null)){
                return true;
            }

            if ((this.getAuthentication() != null) && (temp.getAuthentication() != null)
                    && this.getAuthentication().equals(temp.getAuthentication())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.authentication == null){
            return -1;
        }else {
            return this.authentication.hashCode();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(":");
        if (this.authentication == null){
            sb.append(": Null authentication");
        }else {
            sb.append(": Authentication: ").append(this.authentication);
        }
        return sb.toString();
    }
}
