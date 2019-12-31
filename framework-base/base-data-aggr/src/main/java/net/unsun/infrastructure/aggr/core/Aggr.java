package net.unsun.infrastructure.aggr.core;

import net.unsun.infrastructure.aggr.util.DataAggrUtil;
import net.unsun.infrastructure.common.kit.PageResultBean;
import net.unsun.infrastructure.common.kit.ResultBean;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * use Aggr.main(foo).connect(foo1).connect(foo2).build() to aggregate data.
 * @author tokey
 * @param <T>
 */
public abstract class Aggr<T> implements  AggrBuilder<T> {

    protected static final ThreadLocal<Map<String, Object>> stack = new ThreadLocal<>();

    protected static final String FRAMEWORK_BEAN = "framework_bean";

    //main needed to be compounded
    protected static final String MAIN = "main";

    protected static final String MAIN_ID_FIELD_NAME = "main_id_filed_name";

    //slave Set<String,Object>
    protected static final String SLAVE = "slave";

    //A Map<String,Object> in SLAVE Set<String,Object>
    protected static final String SLAVE_ITEM = "slave_item";

    protected static final String SLAVE_ITEM_NAME = "slave_item_name";

    protected static final String SLAVE_ID_FIELD_NAME = "slave_id_filed_name";

    //default value
    protected static final String slaveIdFileName = "id";

    protected static final String mainIdFiledName = "id";

    public static <T> Aggr<T> main(T main) {
        return main(main,null);
    }

    public static <T> Aggr<T> main(T main, String mainIdName) {
        if(main == null) {
            throw new RuntimeException("Aggr : main item cannot be null");
        }
        //init
        stack.set(new HashMap<String, Object>(16));
        stack.get().put(SLAVE, new HashSet<Object>());

        //add main information
        //first check whether is framework bean or not
        if(main instanceof PageResultBean) {
            PageResultBean<?> pageResultBean = (PageResultBean)main;
            List<?> mainList = pageResultBean.getList();
            stack.get().put(FRAMEWORK_BEAN, pageResultBean);
            stack.get().put(MAIN, mainList);
        } else if(main instanceof ResultBean) {
            ResultBean<?> resultBean = (ResultBean)main;
            Object mainData = resultBean.getData();
            stack.get().put(FRAMEWORK_BEAN, resultBean);
            stack.get().put(MAIN, mainData);
        } else {
            stack.get().put(MAIN, main);
        }
        stack.get().put(MAIN_ID_FIELD_NAME, isBlank(mainIdName) ? mainIdFiledName : mainIdName);



        if(stack.get().get(MAIN) instanceof Collection) {
            return new AggrCollectionData();
        }
        return new AggrStandardData();
    }

    @Override
    public T build() {
        Object main = aggregate();
        Object frameworkBean = stack.get().get(FRAMEWORK_BEAN);
        if(null != frameworkBean) {
            if(frameworkBean instanceof PageResultBean) {
                PageResultBean pageResultBean = (PageResultBean)frameworkBean;
                pageResultBean.setList((List)main);
                return (T)pageResultBean;
            } else if(frameworkBean instanceof ResultBean) {
                ResultBean resultBean = (ResultBean)frameworkBean;
                resultBean.setData(main);
                return (T) resultBean;
            }
        }
        return (T)main;
    }

    public abstract Object aggregate();

    public Aggr<T> connect(Object item) {
        return connect(item, null);
    }


    public Aggr<T> connect(Object item, String filedName) {
        return connect(item,filedName,null);
    }


    public Aggr<T> connect(Object item, String filedName, String idFiledName) {
        if(item == null) {
            throw new RuntimeException("Aggr : connect item cannot be null");
        }

        Map map = new HashMap();

        if(item instanceof PageResultBean) {
            PageResultBean<?> pageResultBean = (PageResultBean)item;
            List<?> mainList = pageResultBean.getList();
            map.put(SLAVE_ITEM, mainList);
        } else if(item instanceof ResultBean) {
            ResultBean<?> resultBean = (ResultBean)item;
            Object mainData = resultBean.getData();
            map.put(SLAVE_ITEM, mainData);
        } else {
            map.put(SLAVE_ITEM, item);
        }

        map.put(SLAVE_ITEM_NAME, isBlank(filedName)? DataAggrUtil.lowerHeadChar(item.getClass().getSimpleName()):filedName);
        map.put(SLAVE_ID_FIELD_NAME, isBlank(idFiledName) ? this.slaveIdFileName : idFiledName);
        ((Set) stack.get().get(SLAVE)).add(map);
        return this;
    }


    static boolean isBlank(String str) {
        if(null == str || "".equals(str)) {
            return true;
        }
        return false;
    }


}
