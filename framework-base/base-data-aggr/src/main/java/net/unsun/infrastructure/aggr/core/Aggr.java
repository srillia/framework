package net.unsun.infrastructure.aggr.core;

import net.unsun.infrastructure.aggr.util.AggrUtil;
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
    protected static final String mainIdFiledName = "id";

    protected static final String slaveIdFileName = "id";

    public static <T> Aggr<T> main(T main) {
        return main(main,null);
    }

    static <T> Aggr<T> main(T main, String mainIdName) {
        try {
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
                return new AggrCollectionData<T>();
            }
            stack.get().put("operator",Operator.born().setMainMap(stack.get()));
            return new AggrStandardData<T>();
        } catch (RuntimeException e) {
            e.printStackTrace();
            stack.remove();
            throw  new RuntimeException(e);
        }
    }


    public void checkAggrParams() {
        Object main = stack.get().get(MAIN);
        if(stack.get() == null ||stack.get().get(MAIN) == null) {
            throw new RuntimeException("Aggr : main item cannot be null");
        }
        if(stack.get() == null || stack.get().get(SLAVE) == null) {
            throw new RuntimeException("Aggr : slave item cannot be null");
        }
    }
    @Override
    public  T build() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            stack.remove();
        }
    }

    public abstract Object aggregate();

    public  Aggr<T> connect(Object item) {
        return connect(item, null);
    }


    private Aggr<T> connect(Object item, String filedName) {
        return connect(item,filedName,null);
    }

    private Aggr<T> connect(Object item, String filedName, String idFiledName) {
        try {
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

            map.put(SLAVE_ITEM_NAME, isBlank(filedName)? AggrUtil.lowerHeadChar(map.get(SLAVE_ITEM).getClass().getSimpleName()):filedName);
            map.put(SLAVE_ID_FIELD_NAME, isBlank(idFiledName) ? slaveIdFileName : idFiledName);
            ((Set) stack.get().get(SLAVE)).add(map);
            stack.get().put("operator",Operator.born().setCertainSlaveMap(map));
        } catch (RuntimeException e) {
            e.printStackTrace();
            stack.remove();
            throw new RuntimeException(e);
        }
        return this;
    }

    public  Aggr<T> id(String id) {
        try {
            Operator operator = (Operator) stack.get().get("operator");
            if(operator != null) {
                if(operator.getType() == 1) {
                    if(operator.getMainMap() != null) {
                        operator.getMainMap().put(MAIN_ID_FIELD_NAME,id);
                    }
                    return this;
                } else if (operator.getType() == 2){
                    if(operator.getCertainSlaveMap() != null) {
                        operator.getCertainSlaveMap().put(SLAVE_ID_FIELD_NAME,id);
                    }
                    return this;
                }
            }
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            stack.remove();
            throw new RuntimeException(e);
        }
    }

    public Aggr<T> name(String name) {
        try {
            Operator operator = (Operator) stack.get().get("operator");
            //main type do not need name
            if(operator != null) {
                if(operator.getType() == 1) {
                    return this;
                } else if (operator.getType() == 2){
                    if(operator.getCertainSlaveMap() != null) {
                        operator.getCertainSlaveMap().put(SLAVE_ITEM_NAME,name);
                    }
                    return this;
                }
            }
            return this;
        } catch (Exception e) {
            e.printStackTrace();
            stack.remove();
            throw new RuntimeException(e);
        }
    }

    public static class Operator {

        //operator type 0 row type ,1 main type, 2 slave type
        private int type = 0;

        private Map<String,Object> mainMap = null;

        private Map<String,Object> certainSlaveMap = null;


        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Map<String, Object> getMainMap() {
            return mainMap;
        }

        public Map<String, Object> getCertainSlaveMap() {
            return certainSlaveMap;
        }

        public static Operator born(){
            return new Operator();
        }

        Operator setMainMap(Map<String, Object> mainMap) {
            //is not main, nothing to do
            if(type == 2) {
                return null;
            }
            this.mainMap = mainMap;
            type = 1;
            return this;
        }

        Operator setCertainSlaveMap(Map<String, Object> certainSlaveMap) {
            //is not slave,nothing to do
            if(type == 1) {
                return null;
            }
            this.certainSlaveMap = certainSlaveMap;
            type = 2;
            return this;
        }

    }

    static boolean isBlank(String str) {
        if(null == str || "".equals(str)) {
            return true;
        }
        return false;
    }


}
