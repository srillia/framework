## 开始构建

``` bash
# 清理项目 - 安装依赖 - 上传到仓库
gradle clean  install upload

# 构建项目并打包
gradle build -x test

# run all tests
gradle test
```