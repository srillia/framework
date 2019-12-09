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

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 用户别名
     */
    private String userNickname;
    /**
     * 用户头像
     */
    private String userAvatar;

    public UserDetail(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }


    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }
}