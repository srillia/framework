package net.unsun.infrastructure.rpc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.unsun.infrastructure.common.kit.PageResultBean;
import net.unsun.infrastructure.common.kit.ResultBean;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: framework
 * @author: Tokey
 * @create: 2019-12-11 15:02
 */
public class ParamsConverter {

    public static Object convertReturnType(Method method, JSONObject result) {
        if(result != null) {
            if(method.getReturnType() == ResultBean.class) {
                ResultBean resultBean = JSON.parseObject(result.toJSONString(), ResultBean.class);
                if(method.getGenericReturnType() instanceof  ParameterizedType){
                    Type[] resultGenericType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();
                    Object resultData = resultBean.getData();
                    if (resultData != null) {
                        resultBean.setData(JSON.parseObject(resultData.toString(), resultGenericType[0]));
                    }
                }
                return resultBean;
            }
            if(method.getReturnType() == PageResultBean.class) {
                PageResultBean pageResultBean = JSON.parseObject(result.toJSONString(), PageResultBean.class);
                if(method.getGenericReturnType() instanceof  ParameterizedType) {
                    Type[] resultGenericType = ((ParameterizedType)method.getGenericReturnType() ).getActualTypeArguments();
                    Object resultData = pageResultBean.getData();
                    if (resultData != null) {
                        pageResultBean.setData(JSON.parseObject(resultData.toString(), resultGenericType[0]));
                    }
                    List list = pageResultBean.getList();

                    if (list != null && list.size() > 0) {
                        List ret = new ArrayList();
                        for (Object item : list) {
                            if(item != null) {
                                ret.add(JSON.parseObject(item.toString(), resultGenericType[0]));
                            }
                        }
                        pageResultBean.setList(ret);
                    }
                }
                return pageResultBean;
            }
        }
        return null;
    }


}