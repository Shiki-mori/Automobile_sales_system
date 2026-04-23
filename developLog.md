# 开发日志

## 任务分配

- 设计  
E-R图  
表结构设计
- SQL实现  
建表  
插数据  
写查询  
- 高级对象  
触发器  
存储过程
- 程序开发  
- debug
- 编写文档与报告  

先做出最小原型：建表，插数据，简单查询  
后续再逐步增加功能

## 技术栈选择

- 编程语言：Python 3.13.9
- 操作系统：opensuse Tumbleweed  
- 数据库：MySQL Ver 8.4.8 for Linux on x86_64 (MySQL Community Server - GPL)
- MySQL Python驱动：pymysql
- 数据库可视化工具（调试工具）：DBeaver

### 安装 pymysql

```bash
pip install pymysql
```

### 安装 DBeaver

在官方下载url<https://dbeaver.io/download/?utm_source=chatgpt.com>下载rpm版本。

安装rpm遇到了签名校验问题，改用tar.gz:

```bash
tar -xvf dbeaver-ce-26.0.3-linux-x86_64.tar.gz 
cd dbeaver
# 启动
./dbeaver
```

### MySQL 配置

登录mysql：

```bash
mysql -u root -p
```

## mysql脚本

使用sql脚本而不是控制台命令开发。“像工程师一样管理数据库，而不是像用户一样操作数据库。”

开发流程：

```bash
mysql -u root -p
```

```sql
source 01_create_schema.sql
```

source命令执行路径是相对于启动mysql时的工作路径。

如果要建的表已经存在，将报错，但建表后续语句仍然继续执行。  
建议在脚本中加入判定：

```sql
DROP TABLE IF EXISTS brand;

CREATE TABLE brand(
    ...
);
```

或其他判定方式：

```sql
CREATE TABLE IF NOT EXISTS brand (
    brand_id INT PRIMARY KEY
);
```

## 02

必须按顺序插入：

```text
Brand → Model → Car → Employee → Customer → Sales_Order → Order_Item → Service_Order → Service_Item
```

## 执行

直接在控制台执行：

```bash
mysql -u root -p < ./01_create_schema.sql
mysql -u root -p < ./02_init_data.sql
```

或：

登录mysql，执行：

```sql
source ./01_create_schema.sql;
SOURCE ./02_init_data.sql;
```

完整路径：

```sql
/home/phrolova/Learning/数据库系统/course_design/automobile_sales_system/SQL/01_create_schema.sql
```

查看插入是否成功：

```sql
USE car_sales;
SHOW TABLES;
SELECT COUNT(*) FROM car;
SELECT * FROM sales_order LIMIT 5;
```

此阶段，发生错误直接删除数据库重新运行脚本：

```sql
DROP DATABASE car_sales;
```

改初始化测试数据必须重置数据库，否则会遇到各种错误。

查看数据库是否已删除：

```sql
SHOW DATABASES;
```
