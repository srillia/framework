<center><h1>框架说明文档</h1></center>

## 目录结构:

```html
L--framework                    		   		//主目录
    L--framework-base                    	   	//框架基础      
	    L--base-boot-web                    	//spring-boot-start-web 相关jar
	    L--base-cloud-config                  	//spring cloud config 相关jar
	    L--base-cloud-feign                 	//spring cloud feign  相关jar
	    L--base-cloud-zipkin                 	//zipkin 相关jar,目前已废弃未使用
	    L--base-common-kit                 	  	//通用工具类，并引入了hutool相关jar
	    L--base-config-starter                 	//自定义的启动配置
	    L--base-data-aggr                 	   	//数据聚合
	    L--base-data-redis                      //redis相关jar
	    L--base-logging-log4j2                  //日志相关jar
	    L--base-mybatis-plus                    //mybatis-plus相关jar
	    L--base-mybatis-plus-generator          //mybatis-plus-generator相关jar
	    L--base-rabbitmq-rpc                 	//fgb框架
	    L--base-security-starter                //项目资源认证相关配置
	    L--base-sentinel-gateway                //alibab的sentinel，基于开源版本改造
	    L--base-spring-test                  	//测试相关jar包
	    L--base-swagger2                 		//swagger2相关jar，已废弃
	    L--base-tx-seata                 		//alibaba seata相关
	    L--base-zzcx-security-starter           //众智传学资源认证相关配置
    
```

## 项目说明:

### 	base-boot-web:

​			此项目主要用于存放**spring-boot-start-web**相关jar，如果项目需要引入web相关jar，直接添加此项目即可。

```groovy
compile "net.unsun.infrastructure:base-boot-web:${frameworkVersion}"
```

### 	base-cloud-config:

​			此项目主要用于引入**spring-cloud-config**相关jar，需要接入配置中心的项目，直接添加此项目即可。

```groovy
compile "net.unsun.infrastructure:base-cloud-config:${frameworkVersion}"
```

### 	base-cloud-feign

​			此项目主要用于引入**spring-cloud-starter-openfeign**相关jar，需要接入feign的项目，直接添加此项目即可。

```groovy
compile "net.unsun.infrastructure:base-cloud-feign:${frameworkVersion}"
```

### 	base-cloud-zipkin

​			已废弃项目

```groovy
compile "net.unsun.infrastructure:base-cloud-zipkin:${frameworkVersion}"
```

### 	base-common-kit

​			项目中的工具类，整合了hutool工具包；**所有的子项目必须引入此项目**。

```groovy
compile "net.unsun.infrastructure:base-common-kit:${frameworkVersion}"
```

### 	base-config-starter

​			此项目主要用于解决项目中，需要额外处理的相关操作；

![image-20210313170634075](C:\Users\loken\AppData\Roaming\Typora\typora-user-images\image-20210313170634075.png)

```groovy
compile "net.unsun.infrastructure:base-config-starter:${frameworkVersion}"
```

### 	base-data-aggr

​			项目数据聚合框架，具体的使用请参考**《框架-Aggr(聚合)使用.pdf》**文件

```groovy
compile "net.unsun.infrastructure:base-data-aggr:${frameworkVersion}"
```

### 	base-data-redis

​			此项目主要用于引入**spring-boot-starter-data-redis**相关jar，需要使用redis的项目，直接添加此项目即可。同时也对redistemplate的序列化进行了自定义处理（**net/unsun/infrastructure/reids/config/RedisConfig.java**）；

```groovy
compile "net.unsun.infrastructure:base-data-redis:${frameworkVersion}"
```

### 	base-logging-log4j2

​			此项目主要用于引入**spring-boot-starter-log4j2**相关jar，**所有的子项目必须引入此项目**。同时也自定义了日志注解，具体的实现和注解请查看该项目代码

```groovy
compile "net.unsun.infrastructure:base-logging-log4j2:${frameworkVersion}"
```

### 	base-mybatis-plus

​			此项目主要用于引入**mybatis-plus**相关jar，需要使用mybatis-plus的项目，直接添加此项目即可。同时也自定义了部分类用于继承mybatis-plus的抽象类和实现接口，子项目只需继承或者实现该jar中自定的类即可

```groovy
compile "net.unsun.infrastructure:base-mybatis-plus:${frameworkVersion}"
```

### 	base-mybatis-plus-generator

​			此项目主要用于引入**mybatis-plus-generator**相关jar。需要注意的是，此项目中的mybaits-plus-generator的jar不是mybaits-plus官方提供的，而是基于官方提供的jar进行了模板渲染改造。

```groovy
compile "net.unsun.infrastructure:base-mybatis-plus-generator:${frameworkVersion}"
```

### 	base-rabbitmq-rpc

​			此项目主要用于引入**spring-boot-starter-amqp**相关jar，通过自定义注解，基于rabbitmq的rpc模式实现了fgb框架。

```groovy
compile "net.unsun.infrastructure:base-rabbitmq-rpc:${frameworkVersion}"
```

### 	base-security-starter

​			此项目主要用于教师云和N次方商城的资源认证服务，不能用于众智传学的资源认证。

```java
CustomSecurityAutoConfiguration.class    //此类用于引入所有的自定义配置和声明bean
CustomResourceServerConfigurerAdapter.class //此类用于自定义ResourceServer相关的配置
CustomSecurityProperties.class     //此类用于加载spring security的相关配置
CustomUserAuthenticationConverter.class  //此类用于对token进行解析转换成UserDetails用户信息，重要类
CustomWebSecurityConfigurerAdapter.class //spring security的自定义配置，主要是多加了一个StrictHttpFirewall相关配置
GlobalCorsConfig.class   //解决跨域问题
TokenFeignClientInterceptor.class  //解决feign的调用时无权限或者无登录信息问题
GlobalExceptionHandler.class  //全局异常处理    
CustomMetaObjectHandler.class  //mybatis-plus的自动填充字段插件   只能是mybatis-plus的自己封装的方法生效
SqlInterceptor.class    //自定义mybatis插件，功能与 CustomMetaObjectHandler相似，但次拦截器只拦截update/insert相关操作，且方法为非mybatis-plus自带方法，必须为自己在xml中自定义的方法 
SecurityKit.class      //获取用户信息的相关操作类
```

```groovy
compile "net.unsun.infrastructure:base-security-starter:${frameworkVersion}"
```

### 	base-sentinel-gateway

​			此项目引入了阿里巴巴的**Sentinel**组件，关于Sentinel请查看官网信息；此项目基于1.7.1版本进行了部分改造，使其支持拦截持久化。

```groovy
compile "net.unsun.infrastructure:base-sentinel-gateway:${frameworkVersion}"
```

### 	base-spring-test

​			此项目主要用于引入**spring-boot-starter-test**和**junit**相关单元测试需要的jar。

```groovy
compile "net.unsun.infrastructure:base-spring-test:${frameworkVersion}"
```

### 	base-swagger2

​			已废弃项目，未有地方使用

```groovy
compile "net.unsun.infrastructure:base-swagger2:${frameworkVersion}"
```

### 	base-tx-seata

​			此项目引入阿里巴巴的分布式事务框架seata，关于seata的相关描述，请参考其官网。具体的使用方法，请查看**《框架-Seata的使用.pdf》**文件

```groovy
compile "net.unsun.infrastructure:base-tx-seata:${frameworkVersion}"
```
