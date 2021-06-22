package io.gaad.infrastructure.sentinel.gateway.init;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: sentinel-dashboard
 * @author: Tokey
 * @create: 2020-08-31 17:31
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.cloud.sentinel", name = "enabled", havingValue = "true")
public class NacosDataSourceInitFunc implements InitializingBean {
    @Value("${spring.cloud.sentinel.datasource.nacos.server-addr}")
    private String serverAddr;

    @Value("${spring.cloud.sentinel.datasource.nacos.groupId}")
    private String groupId;

    @Value("${spring.application.name}")
    private String appName;

    public static final String FLOW_DATA_ID_POSTFIX = "-flow-rules";
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "-param-flow-rules";
    public static final String AUTHORITY_DATA_ID_POSTFIX = "-authority-rules";

    public static final String DEGRADE_DATA_ID_POSTFIX = "-degrade-rules";
    public static final String SYSTEM_DATA_ID_POSTFIX = "-system-rules";
    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "-gateway-flow-rules";
    public static final String GATEWAY_API_DATA_ID_POSTFIX = "-gateway-api-rules";

    public static final String DASHBOARD_POSTFIX = "-dashboard";

    @Override
    public void afterPropertiesSet() throws Exception {
//        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(serverAddr, groupId, getDataId(appName, FLOW_DATA_ID_POSTFIX),
//                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
//                }));
//        ReadableDataSource<String, List<AuthorityRule>> authorityRuleDataSource = new NacosDataSource<>(serverAddr, groupId, getDataId(appName, AUTHORITY_DATA_ID_POSTFIX),
//                source -> JSON.parseObject(source, new TypeReference<List<AuthorityRule>>() {
//                }));
//        ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleDataSource = new NacosDataSource<>(serverAddr, groupId, getDataId(appName, PARAM_FLOW_DATA_ID_POSTFIX),
//                source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {
//                }));
//
//        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
//        AuthorityRuleManager.register2Property(authorityRuleDataSource.getProperty());
//        ParamFlowRuleManager.register2Property(paramFlowRuleDataSource.getProperty());

        ReadableDataSource<String, Set<GatewayFlowRule>> gatewayFlowRuleDataSource = new NacosDataSource<>(serverAddr, groupId, getDataId(appName, GATEWAY_FLOW_DATA_ID_POSTFIX),
                source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {
                }));
        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new NacosDataSource<>(serverAddr, groupId, getDataId(appName, SYSTEM_DATA_ID_POSTFIX),
                source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {
                }));
        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<>(serverAddr, groupId, getDataId(appName, DEGRADE_DATA_ID_POSTFIX),
                source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
                }));
        ReadableDataSource<String, Set<ApiDefinition>> gatewayApiRuleDataSource = new NacosDataSource<>(serverAddr, groupId, getDataId(appName, GATEWAY_API_DATA_ID_POSTFIX),
                source -> {
                    JSONArray objects = JSON.parseArray(source);
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
                    return apiDefinitions;
                });
        GatewayApiDefinitionManager.register2Property(gatewayApiRuleDataSource.getProperty());
        GatewayRuleManager.register2Property(gatewayFlowRuleDataSource.getProperty());
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());

    }

    private static String getDataId(String appName, String postfix) {
        return appName + postfix;
    }
}