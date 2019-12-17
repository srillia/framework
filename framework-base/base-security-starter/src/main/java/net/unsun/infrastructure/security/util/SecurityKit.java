package net.unsun.infrastructure.security.util;

import net.unsun.infrastructure.security.base.UserDetail;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-28 16:40
 */
public class SecurityKit extends SecurityContextHolder {

    /**
     * 判断当前系统是否已登录
     *
     * @return 是否
     */
    public static boolean isAuthenticated() {
        if (null == getContext() || null == getContext().getAuthentication()) {
            return false;
        }
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return getContext().getAuthentication().isAuthenticated();
    }

    /**
     * 获取当前系统登陆用户账号
     *
     * @return 用户账号
     */
    public static boolean isLogin() {
        if (!isAuthenticated()) {
            return false;
        }
        UserDetail userDetail = (UserDetail) getContext().getAuthentication().getPrincipal();
        if(userDetail == null) {
            return false;
        }
        Collection<GrantedAuthority> authorities = userDetail.getAuthorities();
        if(authorities == null || authorities.size() == 0) {
            return false;
        }
        for (GrantedAuthority authority : authorities) {
            //不需要登录的用户
            if("ANONYMOUS".equals(authority.getAuthority())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前系统登陆用户账号
     *
     * @return 用户账号
     */
    public static String userAccount() {
        if (!isAuthenticated()) {
            return null;
        }
        return getContext().getAuthentication().getName();
    }

    /**
     * 获取当前系统登陆用户信息
     *
     * @return 用户账号
     */
    public static UserDetail currentUser() {
        if (!isAuthenticated()) {
            return null;
        }
        return (UserDetail) getContext().getAuthentication().getPrincipal();
    }

}
