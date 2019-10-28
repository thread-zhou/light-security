package com.lightsecurity.core.filter;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.authentication.AuthenticationManager;
import com.lightsecurity.core.authentication.UsernamePasswordAuthenticationToken;
import com.lightsecurity.core.exception.AuthenticationException;
import com.lightsecurity.core.exception.AuthenticationServiceException;
import com.lightsecurity.core.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String LIGHT_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String LIGHT_SECURITY_FORM_PASSWORD_KEY = "password";

    private String usernameParameter = LIGHT_SECURITY_FORM_USERNAME_KEY;
    private String passwordParameter = LIGHT_SECURITY_FORM_PASSWORD_KEY;
    private boolean postOnly = true;


    public UsernamePasswordAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication processAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals("POST")){
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        String username = obtainUsername(request);
        String password = obtainPassword(request);
        if (username == null){
            username = "";
        }
        if (password == null){
            password = "";
        }

        username = username.trim();

        //发起请求组装的token
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // Allow subclasses to set the "details" property
        setDetails(request, authenticationToken);
        return getAuthenticationManager().authenticate(authenticationToken);//这里开始真实认证过程, 使用ProviderManager进行代理
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    protected String obtainUsername(HttpServletRequest request){
        return request.getParameter(usernameParameter);
    }

    protected String obtainPassword(HttpServletRequest request){
        return request.getParameter(passwordParameter);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken token){
        //todo
        token.setDetails(authenticationDetailsSource.bindDetails(request));
    }

}
