package net.unsun.infrastructure.security.base;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-26 17:51
 */
public class UserDetail extends User {

    /**
     *
     */
    private static final long serialVersionUID = 7589191160462204999L;
    /**
     *
     */
    public Long userId;

    public UserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}