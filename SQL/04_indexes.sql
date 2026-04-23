USE car_sales;

-- 索引1：销售订单创建时间索引
-- 优化目标：加速按时间范围查询销售订单，支持订单报表的时间筛选和排序
CREATE INDEX idx_sales_order_create_time ON sales_order(create_time);

-- 索引2：库存车辆状态和车型ID复合索引
-- 优化目标：加速按状态和车型查询库存车辆，支持快速查找某车型的可用库存
CREATE INDEX idx_car_status_model ON car(status, model_id);

-- 索引3：客户手机号索引
-- 优化目标：加速通过手机号查找客户信息，支持客户快速登录和身份验证
CREATE INDEX idx_customer_phone ON customer(phone);

-- 索引4：服务工单创建时间索引
-- 优化目标：加速按时间范围查询服务工单，支持服务报表的时间筛选和统计
CREATE INDEX idx_service_order_create_time ON service_order(create_time);

-- 索引5：服务工单车辆VIN索引
-- 优化目标：加速查询某辆车的服务历史，支持车辆维修记录查询
CREATE INDEX idx_service_order_vin ON service_order(vin);

-- 索引6：销售订单客户ID索引
-- 优化目标：加速查询客户的订单历史，支持客户购买记录统计
CREATE INDEX idx_sales_order_customer ON sales_order(customer_id);

-- 索引7：销售订单员工ID索引
-- 优化目标：加速查询销售顾问的订单，支持业绩统计和提成计算
CREATE INDEX idx_sales_order_employee ON sales_order(employee_id);
