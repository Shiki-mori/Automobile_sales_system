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

## 控制台应用开发

### java环境准备

使用sdkman切换至java 17：

```bash
sdk use java 17.0.10-tem
```

验证环境：

```bash
java -version
javac -version
```

### JDBC 驱动

Java连接MySql必须使用 JDBC 驱动。  
方法：使用 Maven 添加依赖

在pom.xml 文件中添加依赖：

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
```

### IDE

IntelliJ IDEA  
或windsurf

### 数据库连接配置

写 db.properties 文件，该文件必须加入 .gitignore:

```properties
db.url=jdbc:mysql://localhost:3306/car_sales?useSSL=false&serverTimezone=UTC
db.user=root
db.password=你的密码
```

Java读取：

```java
Properties props = new Properties();
FileInputStream fis = new FileInputStream("db.properties");
props.load(fis);

String url = props.getProperty("db.url");
String user = props.getProperty("db.user");
String password = props.getProperty("db.password");

Connection conn = DriverManager.getConnection(url, user, password);
```

>优点：系统采用配置文件方式管理数据库连接信息，避免敏感信息硬编码，提高安全性与可维护性。

#### 作业提交

提供一个示例配置文件`db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/car_sales
db.user=root
db.password=your_password
```

README说明:

```text
请复制 db.properties.example 为 db.properties 并填写本地数据库密码
```

### 任务说明

1. 执行普通sql查询
2. 调用存储过程
3. 使用事务

### 目标项目结构

automobile_sales_system/  
├── SQL/  
│   ├── 01_create_schema.sql  
│   ├── 02_init_data.sql  
│   ├── ...  
│  
├── TestCommand/  
│   ├── 05_test_triggers.sql  
│  
├── sales_system/  
│   ├── src/  
│   │   └── main/  
│   │       └── java/com/automobile/  
│   │           ├── db/  
│   │           │   └── DBUtil.java  
│   │           ├── App.java  
│   │       └── resources/  
│   │           └── db.properties  
│   │           └── db.properties.example  
│   │  
│   ├── target/  
│   │  
│   ├── pom.xml             ← Maven配置  

安装Maven：

```bash
sudo zypper install maven
```

在automobile_sales_system目录下执行：

```bash
mvn archetype:generate
```

Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 2346: maven-archetype-quickstart

接下来两个数字默认。（3和9）

groupID：com.automobile  
artifactID：sales_system  
version：1.0-SNAPSHOT
package: com.automobile

加入 JDBC 依赖：

编辑 pom.xml:

```xml
<dependencies>
    ...
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>
    ...
</dependencies>
```

修改`App.java`文件，替换为连接数据库测试（确保已启动mysql）：

```java
package com.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/car_sales?useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "password";  

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("数据库连接成功！");
            conn.close();
        } catch (Exception e) {
            System.out.println("连接失败！");
            e.printStackTrace();
        }
    }
}
```

在`sales_system`目录下执行：

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.App"
```

显示“数据库连接成功”。

#### 将明文密码改为配置文件方式

添加密码配置文件。  
在src/main/目录下新建文件夹resources，在其下新建文件`db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/car_sales?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8
db.user=root
db.password=password
```

将该文件加入.gitignore。

写读取配置的代码。单独做一个工具类来实现。创建`sales_system/src/main/java/com/example/db/DBUtil.java`:

```java
package com.example.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class DBUtil {

    public static Connection getConnection() throws Exception {

        // 1. 加载配置文件（从 resources 读取）
        Properties props = new Properties();
        InputStream is = DBUtil.class
                .getClassLoader()
                .getResourceAsStream("db.properties");

        if (is == null) {
            throw new RuntimeException("找不到 db.properties 文件");
        }

        props.load(is);

        // 2. 读取配置
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // 3. 建立连接
        return DriverManager.getConnection(url, user, password);
    }
}
```

修改App.java为从配置文件导入密码的形式。

系统通过外部配置文件（db.properties）管理数据库连接信息，避免将敏感信息硬编码在程序中，提高了系统的安全性与可维护性。

修改为com.automobile后，执行命令也需修改：

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.automobile.App"
```

### 任务核心

控制台驱动的数据库客户端系统。

- 调用数据库（SQL查询/存储过程）
- 展示结果
- 保证安全（参数化）
- 保证一致性（事务）

架构：

- App（菜单控制）  
负责交互。
- Service（业务逻辑）  
调用存储过程、事务。
- DAO（data access object，数据访问对象）  
用于封装与数据库、文件或外部API等数据源交互的细节。  
在本项目中用于存放SQL语句。
- DBUtil（数据库连接管理）

目前只有App和DBUtil。  
先补齐项目结构。  
在com.automobile下新增：

com.automobile  
├── db  
│   └── DBUtil.java  
├── dao  
│   ├── CustomerDAO.java  
│   ├── OrderDAO.java  
│   ├── InventoryDAO.java  
├── service  
│   ├── SalesService.java  
│   ├── InventoryService.java  
│   ├── ReportService.java  
├── App.java

### 简化登录系统

程序启动时，要求输入员工ID，将其存到全局变量中：

```java
int currentEmpId = sc.nextInt();
```

角色分流示例：

```java
String role = employeeDAO.getRole(empId);
if ("销售顾问".equals(role)) {
    showSalesMenu();
} else if ("库存管理员".equals(role)) {
    showInventoryMenu();
}
```

### 创建订单功能

访问数据对象：

- CustomerDAO
- OrderDAO
- InventoryDAO

调用服务：

- SalesService

输出客户列表，销售人员列表，在库车辆列表。

### 菜单交互控制

