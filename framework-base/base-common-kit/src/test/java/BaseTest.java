import io.gaad.infrastructure.common.constant.BaseCode;
import io.gaad.infrastructure.common.kit.LogInfoBean;
import io.gaad.infrastructure.common.kit.PageResultBean;
import io.gaad.infrastructure.common.kit.ResultBean;
import org.junit.jupiter.api.Test;

/**
 * @program: unsun-framework
 * @author: Tokey
 * @create: 2019-11-20 13:57
 */
public class BaseTest {
    @Test
    public void test() {
        System.out.println(ResultBean.success());
        System.out.println(ResultBean.fail());
        System.out.println(ResultBean.create());
        System.out.println(BaseCode.systemBusy.getCodeExplainByCode(1));

        ResultBean<LogInfoBean> result = new ResultBean<>();
        PageResultBean<LogInfoBean> pageResultBean = new PageResultBean<>();
    }

}