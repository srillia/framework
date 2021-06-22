import io.gaad.infrastructure.common.constant.BaseCode;
import io.gaad.infrastructure.common.kit.ResultBean;
import io.gaad.infrastructure.rpc.entity.ErrorCode;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
        List<ErrorCode> list = new ArrayList<>();

//        System.out.println((PageResultBean)ResultBean.fail().setMessage("rpc,没有返回值"));

        try {
            Method method = ResultBean.class.getMethod("setCode", BaseCode.class);
            Class<?> returnType = method.getReturnType();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}