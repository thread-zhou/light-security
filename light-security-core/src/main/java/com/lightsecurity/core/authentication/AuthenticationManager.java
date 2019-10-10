package com.lightsecurity.core.authentication;

import com.lightsecurity.core.Authentication;
import com.lightsecurity.core.exception.AuthenticationException;

/**
 * InterfaceName AuthenticationManager
 * Description : 核心认证接口
 * Author Administrator
 * Date 2019/10/9 15:37
 **/
public interface AuthenticationManager {

    /**
    * @Description: 对传递的{@link Authentication}对象尝试认证, 如果认证成功, 则返回填充好的<code>Authentication</code>对象
    * @Param: [authentication]
    * @Return: com.lightsecurity.core.Authentication
    * @Author: Administrator
    * @Date: 2019/10/9 15:39
    */
    Authentication authenticate(Authentication authentication) throws AuthenticationException;

}
