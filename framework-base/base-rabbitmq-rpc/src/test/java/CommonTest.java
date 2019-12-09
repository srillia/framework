import net.unsun.infrastructure.common.constant.BaseCode;
import net.unsun.infrastructure.common.kit.PageResultBean;
import net.unsun.infrastructure.common.kit.ResultBean;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-12-06 10:54
 */
public class CommonTest {

    @Test
    public void test() {
        String name = ResultBean.class.getName();
        String name2 =  ResultBean.class.getSimpleName();
        String name3 =  ResultBean.class.getCanonicalName();
        System.out.println(name);
        System.out.println(name2);
        System.out.println(name3);

//        System.out.println((PageResultBean)ResultBean.fail().setMessage("rpc,没有返回值"));

        try {
            Method method = ResultBean.class.getMethod("setCode", BaseCode.class);
            Class<?> returnType = method.getReturnType();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}