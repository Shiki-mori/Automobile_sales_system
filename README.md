# 汽车销售管理系统

## 项目简介
基于 MySQL + Java 的汽车销售管理系统。

## 技术栈
- MySQL 8.0
- Java
- Maven
- JDBC
- DBeaver

## 功能模块
- 销售前台
- 库存管理
- 报表中心

## 数据库初始化步骤

1. 执行 01_create_schema.sql
2. 执行 02_init_data.sql
3. 执行其他SQL脚本

## 程序运行方式

执行`run.sh`，或执行：

```bash
mvn clean compile exec:java -Dexec.mainClass="com.automobile.App"
```