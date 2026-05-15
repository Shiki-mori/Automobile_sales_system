USE car_sales;

-- ========================================
-- 测试索引功能
-- ========================================

-- 1. 查看所有索引是否创建成功
SHOW INDEX FROM sales_order;
SHOW INDEX FROM car;
SHOW INDEX FROM customer;
SHOW INDEX FROM intention;
SHOW INDEX FROM service_order;

-- 2. 测试索引1：销售订单创建时间索引
-- 验证索引是否被使用
EXPLAIN SELECT * FROM sales_order WHERE create_time BETWEEN '2025-01-01' AND '2026-12-31' ORDER BY create_time;
-- 数据量过小，优化器使用全表扫描，强制使用索引
EXPLAIN SELECT * FROM sales_order FORCE INDEX (idx_sales_order_create_time) WHERE create_time BETWEEN '2025-01-01' AND '2026-12-31' ORDER BY create_time;

-- 3. 测试索引2：库存车辆状态和车型ID复合索引
EXPLAIN SELECT * FROM car WHERE status = '在库' AND model_id = 1;

-- 4. 测试索引3：客户手机号索引
EXPLAIN SELECT * FROM customer WHERE phone = '13800000009';

-- 5. 测试索引4：服务工单创建时间索引
EXPLAIN SELECT * FROM service_order WHERE create_time BETWEEN '2025-01-01' AND '2026-12-31' ORDER BY create_time;

-- 6. 测试索引5：服务工单车辆VIN索引
EXPLAIN SELECT * FROM service_order WHERE vin = 'VIN00000000000001';

-- 7. 测试索引6：销售订单客户ID索引
EXPLAIN SELECT * FROM sales_order WHERE customer_id = 1;

-- 8. 测试索引7：销售订单员工ID索引
EXPLAIN SELECT * FROM sales_order WHERE employee_id = 1;