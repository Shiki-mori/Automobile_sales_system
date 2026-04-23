# 具体

1. 品牌 `Brand`  
brand_id (PK)  
name
2. 车型 `Model`  
model_id (PK)  
brand_id (FK)  
series_name  
year  
config_name  
guide_price  
engine  
type  
👉 关系：  
Brand 1 —— N Model
3. 库存车辆 `Car`  
vin (PK)  
model_id (FK)  
color  
engine_no  
production_date  
stock_in_date  
purchase_price  
sale_price  
status（在库/锁定/售出/在途）  
👉 关系：  
Model 1 —— N Car  
4. 客户 `Customer`  
customer_id (PK)  
name  
gender  
phone  
id_card  
address  
first_visit_date  
5. 员工 `Employee`  
employee_id (PK)  
name  
job_number  
role  
department  
hire_date  
manager_id（自关联）  
👉 关系：  
Employee（1）——（N）Employee（主管关系）  
6. 客户意向 `Intention`  
intention_id (PK)  
customer_id (FK)  
model_id (FK)  
level  
note  
follow_employee_id (FK)  
next_contact_time  
👉 关系：  
Customer 1 —— N Intention  
Model 1 —— N Intention  
Employee 1 —— N Intention  
7. 销售订单 `Sales_Order`  
order_id (PK)  
customer_id (FK)  
employee_id (FK)  
vin (FK)  
total_amount  
deposit  
status  
create_time  
delivery_time  
👉 关系：  
Customer 1 —— N Sales_Order  
Employee 1 —— N Sales_Order  
Car 1 —— 1 Sales_Order（关键：一车一单）  
8. 订单明细 `Order_Item`  
item_id (PK)  
order_id (FK)  
type  
description  
amount  
👉 关系：  
Sales_Order 1 —— N Order_Item  
9. 服务工单 `Service_Order`  
service_id (PK)  
customer_id (FK)  
vin (FK)  
service_type  
employee_id (FK)  
create_time  
expected_finish_time  
total_cost  
status  
👉 关系：  
Customer 1 —— N Service_Order  
Car 1 —— N Service_Order  
Employee 1 —— N Service_Order  
10. 服务明细 `Service_Item`  
item_id (PK)  
service_id (FK)  
name  
quantity  
price  
amount  
👉 关系：  
Service_Order 1 —— N Service_Item  

# 关键关系

| 实体A | 关系  | 实体B | 基数 |
| ------------- | ----- | ---- | ----- |
| Brand         | 拥有  | Model         | 1:N      |
| Model         | 对应  | Car           | 1:N      |
| Customer      | 下单  | Sales_Order   | 1:N      |
| Employee      | 负责  | Sales_Order   | 1:N      |
| Car           | 被销售 | Sales_Order   | 1:1      |
| Sales_Order   | 包含  | Order_Item    | 1:N      |
| Customer      | 有   | Intention     | 1:N      |
| Model         | 被意向 | Intention     | 1:N      |
| Employee      | 跟进  | Intention     | 1:N      |
| Customer      | 有   | Service_Order | 1:N      |
| Car           | 维修  | Service_Order | 1:N      |
| Service_Order | 包含  | Service_Item  | 1:N      |
| Employee      | 管理  | Employee      | 1:N（自关联） |

# 布局

按照业务流，从左往右画：

品牌 → 车型 → 车辆 → 销售订单 → 订单明细
             ↓
          服务工单 → 服务明细

客户 → 意向
客户 → 销售订单
客户 → 服务工单

员工 →（管理）员工
员工 → 销售订单 / 意向 / 服务工单