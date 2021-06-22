package io.gaad.infrastructure.sentinel.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: framework
 * @author: Tokey
 * @create: 2020-09-07 17:56
 */
public class CommonTest {
    @Test
    public void test() {
        String str = "[{\"apiName\":\"/test1\",\"predicateItems\":[{\"matchStrategy\":1,\"pattern\":\"/test\"}]},{\"apiName\":\"/test2\",\"predicateItems\":[{\"matchStrategy\":0,\"pattern\":\"/test2\"}]}]";
        //String str = "[{\"apiName\":\"\",\"predicateItems\":\"\"}]";
        JSONArray objects = JSON.parseArray(str);
        Set<ApiDefinition> apiDefinitions = new HashSet<>();
        if (objects != null) {
            for (int i = 0; i < objects.size(); i++) {
                JSONObject object =  (JSONObject)objects.get(i);
                String apiName = object.getString("apiName");
                String predicateItems = object.getString("predicateItems");
                ApiDefinition apiDefinition = new ApiDefinition();
                JSONArray preItems = JSON.parseArray(predicateItems);
                Set<ApiPredicateItem> items = new HashSet<>();
                apiDefinition.setApiName(apiName);
                apiDefinition.setPredicateItems(items);
                apiDefinitions.add(apiDefinition);
                if(preItems != null) {
                    for (int j = 0; j < preItems.size(); j++) {
                        JSONObject preItem =  (JSONObject)preItems.get(j);
                        ApiPathPredicateItem apiPathPredicateItem = new ApiPathPredicateItem();
                        apiPathPredicateItem.setMatchStrategy(preItem.getIntValue("matchStrategy"));
                        apiPathPredicateItem.setPattern(preItem.getString("pattern"));
                        items.add(apiPathPredicateItem);
                    }
                }
            }
        }
        System.out.println(apiDefinitions);
    }
}