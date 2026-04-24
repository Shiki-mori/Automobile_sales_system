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

### 05_trigger

问题根源：sales_order 表的 vin 字段有 UNIQUE 约束，即使订单已取消，vin 也不能重复使用。

解决方案：

添加了触发器3 trg_release_car_on_cancel：当订单状态更新为"已取消"时，自动将车辆状态恢复为"在库"  
修改测试脚本使用未被占用的 vin（VIN00000000000015 和 VIN00000000000016）避免 UNIQUE 约束冲突  
添加了测试触发器3的用例，验证订单取消时车辆状态释放功能  
注意：由于 vin 的 UNIQUE 约束，已取消的订单仍会占用该 vin，无法被新订单使用。如果需要支持 vin 重复使用，需要移除 sales_order.vin 的 UNIQUE 约束。

初始设计中对销售订单表的车辆VIN设置了唯一约束，但在实际业务中，订单取消后车辆可重新销售，因此同一VIN可能对应多个订单记录。为符合业务实际，本系统移除了VIN唯一约束，并通过业务逻辑控制同一时间仅允许一个有效订单占用该车辆，从而保证数据一致性。

移除约束后，新的问题：

同一辆车可能被同时卖给两个人

✔ 正确约束应该是：

❗ 同一时间只能有一个“有效订单”占用该 VIN

标准解决方式: 用 业务逻辑 / 触发器 控制

规则  
对于同一 VIN，在任意时刻最多只能存在一个状态为：
“已创建 / 已锁定 / 已完成”的订单
允许重复的情况  
如果订单状态 = 已取消 → 可以再次使用 VIN

推荐写法

在创建订单时检查：

SELECT COUNT(*)
FROM Sales_Order
WHERE vin = ?
AND status IN ('已创建','已锁定');

如果 > 0 → 不允许创建订单

#### 触发器和测试语句冲突

已修复触发器冲突问题。将原触发器拆分为两个：

修改说明：

触发器2 trg_set_delivery_time (BEFORE UPDATE)：在订单状态变为"已完成"前，设置 delivery_time 为当前时间
触发器3 trg_update_inventory_on_delivery (AFTER UPDATE)：在订单状态变为"已完成"后，更新车辆状态为"已售出"
触发器4 trg_release_car_on_cancel (AFTER UPDATE)：订单取消时释放车辆状态
这样避免了在 AFTER UPDATE 触发器中更新正在被触发的表。

原因是触发器1 trg_lock_car_on_order 只在 INSERT 时触发，而测试场景是 UPDATE 操作。需要添加一个触发器处理 UPDATE 时状态变为"已创建"的情况：