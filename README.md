# 工程简介

学生材料收集管理系统, 用于统计评价学生成绩, 老师课程目标达成度等等

# 数据库构成
<img src="https://github.com/aerlany/Images-of-mine/blob/main/Manage_system/%E6%95%B0%E6%8D%AE%E5%BA%93%E6%9E%84%E6%88%90.png" alt="数据库构成" title="数据库构成">


# 延伸阅读
当你运行此代码时
API 文档 http://localhost:8080/swagger-ui/index.html



## 运行指引

当你在本地运行此代码时请检查src/main/resources/lib/application.yml文件

将

```yml
#使用本地数据库
url: jdbc:mysql://localhost:3306/Manage_system?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8
```

里的

```
3306/Manage_system
```

换成与自己本地数据库所对应的端口号及数据库名称

将

```xml
server:
  port: 2024
```

换成

```xml
server:
  port: 8080
```



## 部署

详情请查看 工程教育认证技术开发文档.md

