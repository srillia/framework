package net.unsun.infrastructure.springtest;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author srillia(srillia@coldweaponera.com)
 * @version 1.0.0
 * @since 2017-06-22 10:00
 */
@WebAppConfiguration
@AutoConfigureMockMvc
public abstract class BaseWebAppTest extends BaseTest {
}
