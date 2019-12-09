package net.unsun.infrastructure.security.config;

import net.unsun.infrastructure.security.base.UserDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;

import java.util.Map;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-28 11:12
 */
public class CustomUserAuthenticationConverter implements UserAuthenticationConverter {
    private static final String N_A = "N/A";

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
        return null;
    }

    // map 是check-token 返回的全部信息
    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {

            String username = (String) map.get(USERNAME);
            UserDetail user = new UserDetail(username,"", AuthorityUtils.commaSeparatedStringToAuthorityList("xxxxxxxx"));
            user.setUserId(Long.parseLong(map.get("id").toString()));
            user.setUserAvatar(map.get("userAvatar") == null ? null:map.get("userAvatar").toString());
            user.setUserAccount(map.get("userAccount") == null ? null:map.get("userAccount").toString());
            user.setUserNickname(map.get("userNickname") == null ? null:map.get("userNickname").toString());
            return new UsernamePasswordAuthenticationToken(user, N_A, AuthorityUtils.commaSeparatedStringToAuthorityList("xxxxxxxx"));
        }
        return null;
    }
}