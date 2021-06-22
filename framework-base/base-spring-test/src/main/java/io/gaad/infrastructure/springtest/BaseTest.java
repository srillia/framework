package io.gaad.infrastructure.springtest;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author srillia(srillia@coldweaponera.com)
 * @version 1.0.0
 * @since 2017-06-22 10:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseTest {

    protected Logger logger = LoggerFactory.getLogger(getClass());

}
