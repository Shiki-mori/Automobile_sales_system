USE car_sales;

-- ========================================
-- 测试触发器功能
-- ========================================

-- 1. 查看触发器是否创建成功
SHOW TRIGGERS;

-- 2. 测试触发器1：订单创建时自动锁定车辆
-- 先查询车辆当前状态（使用未被占用的vin）
SELECT vin, status FROM car WHERE vin = 'VIN00000000000015';

-- 插入一个"已创建"状态的订单，应该自动锁定车辆
INSERT INTO sales_order (customer_id, employee_id, vin, total_amount, deposit, status, create_time)
VALUES (1, 1, 'VIN00000000000015', 200000, 20000, '已创建', NOW());

-- 再次查询车辆状态，应该变为"已锁定"
SELECT vin, status FROM car WHERE vin = 'VIN00000000000015';

-- 触发器1测试通过

-- 3. 测试触发器2：订单完成时更新车辆状态和交车时间
-- 先查询车辆和订单当前状态
SELECT vin, status FROM car WHERE vin = 'VIN00000000000015';
SELECT order_id, status, delivery_time FROM sales_order WHERE vin = 'VIN00000000000015';

-- 更新订单状态为"已完成"，应该自动将车辆状态更新为"已售出"并记录交车时间
UPDATE sales_order SET status = '已完成' WHERE vin = 'VIN00000000000015';

-- 再次查询车辆和订单状态
SELECT vin, status FROM car WHERE vin = 'VIN00000000000015';
SELECT order_id, status, delivery_time FROM sales_order WHERE vin = 'VIN00000000000015';

-- 触发器2测试通过

-- 4. 测试边界情况：插入非"已创建"状态的订单不应锁定车辆
-- 先查询车辆当前状态（使用未被占用的vin）
SELECT vin, status FROM car WHERE vin = 'VIN00000000000016';

-- 插入一个"已取消"状态的订单，不应锁定车辆
INSERT INTO sales_order (customer_id, employee_id, vin, total_amount, deposit, status, create_time)
VALUES (2, 2, 'VIN00000000000016', 150000, 20000, '已取消', NOW());

-- 再次查询车辆状态，应该保持不变
SELECT vin, status FROM car WHERE vin = 'VIN00000000000016';

-- 边界测试通过

-- 5. 测试触发器4：订单取消时释放车辆
-- 先查询车辆当前状态
SELECT vin, status FROM car WHERE vin = 'VIN00000000000016';

-- 测试触发器1.5
-- 将订单状态从"已取消"更新为"已创建"，应该锁定车辆
UPDATE sales_order SET status = '已创建' WHERE vin = 'VIN00000000000016';

-- 再次查询车辆状态，应该变为"已锁定"
SELECT vin, status FROM car WHERE vin = 'VIN00000000000016';

-- 再将订单状态从"已创建"更新为"已取消"，应该释放车辆
UPDATE sales_order SET status = '已取消' WHERE vin = 'VIN00000000000016';

-- 再次查询车辆状态，应该变为"在库"
SELECT vin, status FROM car WHERE vin = 'VIN00000000000016';

-- 触发器1.5测试通过
-- 触发器4测试通过