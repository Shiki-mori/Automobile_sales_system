USE car_sales;

-- ========================================
-- 测试存储过程 sp_create_sales_order
-- ========================================

-- 测试1：正常创建订单（车辆状态为"在库"）
SELECT '=== 测试1:正常创建订单 ===' AS test_case;
-- 初始化输出参数
SET @order_id = 0;
SET @result_code = 0;
SET @result_msg = '';

CALL sp_create_sales_order(
    1,                    -- customer_id
    1,                    -- employee_id
    'VIN00000000000001',  -- vin (在库状态)
    120000.00,            -- car_price
    5000.00,              -- insurance_fee
    10000.00,             -- tax_fee
    2000.00,              -- service_fee
    1000.00,              -- other_fee
    20000.00,             -- deposit
    @order_id,            -- OUT: order_id
    @result_code,         -- OUT: result_code
    @result_msg           -- OUT: result_msg
);

SELECT @order_id AS order_id, @result_code AS result_code, @result_msg AS result_msg;

-- 查看创建的订单
SELECT * FROM sales_order WHERE order_id = @order_id;

-- 查看订单明细
SELECT * FROM order_item WHERE order_id = @order_id;

-- 查看车辆状态变化
SELECT vin, status FROM car WHERE vin = 'VIN00000000000001';

-- 测试2：车辆状态异常（非"在库"状态）
SELECT '=== 测试2:车辆状态异常 ===' AS test_case;
SET @order_id = 0;
SET @result_code = 0;
SET @result_msg = '';

CALL sp_create_sales_order(
    2,                    -- customer_id
    2,                    -- employee_id
    'VIN00000000000003',  -- vin (已锁定状态)
    180000.00,            -- car_price
    5000.00,              -- insurance_fee
    10000.00,             -- tax_fee
    2000.00,              -- service_fee
    1000.00,              -- other_fee
    30000.00,             -- deposit
    @order_id,            -- OUT: order_id
    @result_code,         -- OUT: result_code
    @result_msg           -- OUT: result_msg
);

SELECT @order_id AS order_id, @result_code AS result_code, @result_msg AS result_msg;

-- 测试3：创建订单（各项费用为0）
SELECT '=== 测试3:仅车辆价格, 其他费用为0 ===' AS test_case;
SET @order_id = 0;
SET @result_code = 0;
SET @result_msg = '';

CALL sp_create_sales_order(
    3,                    -- customer_id
    3,                    -- employee_id
    'VIN00000000000006',  -- vin (在库状态)
    150000.00,            -- car_price
    0.00,                 -- insurance_fee
    0.00,                 -- tax_fee
    0.00,                 -- service_fee
    0.00,                 -- other_fee
    20000.00,             -- deposit
    @order_id,            -- OUT: order_id
    @result_code,         -- OUT: result_code
    @result_msg           -- OUT: result_msg
);

SELECT @order_id AS order_id, @result_code AS result_code, @result_msg AS result_msg;

-- 查看订单明细（应该只有车辆费用）
SELECT * FROM order_item WHERE order_id = @order_id;

-- 测试4：完整费用明细
SELECT '=== 测试4:完整费用明细 ===' AS test_case;
SET @order_id = 0;
SET @result_code = 0;
SET @result_msg = '';

CALL sp_create_sales_order(
    4,                    -- customer_id
    4,                    -- employee_id
    'VIN00000000000009',  -- vin (在库状态)
    110000.00,            -- car_price
    8000.00,              -- insurance_fee
    12000.00,             -- tax_fee
    3000.00,              -- service_fee
    2000.00,              -- other_fee
    25000.00,             -- deposit
    @order_id,            -- OUT: order_id
    @result_code,         -- OUT: result_code
    @result_msg           -- OUT: result_msg
);

SELECT @order_id AS order_id, @result_code AS result_code, @result_msg AS result_msg;

-- 查看订单明细（应该有5条记录）
SELECT * FROM order_item WHERE order_id = @order_id;

-- 测试5：车辆已售出
SELECT '=== 测试5:车辆已售出 ===' AS test_case;
SET @order_id = 0;
SET @result_code = 0;
SET @result_msg = '';

CALL sp_create_sales_order(
    5,                    -- customer_id
    5,                    -- employee_id
    'VIN00000000000004',  -- vin (已售出状态)
    180000.00,            -- car_price
    5000.00,              -- insurance_fee
    10000.00,             -- tax_fee
    2000.00,              -- service_fee
    1000.00,              -- other_fee
    30000.00,             -- deposit
    @order_id,            -- OUT: order_id
    @result_code,         -- OUT: result_code
    @result_msg           -- OUT: result_msg
);

SELECT @order_id AS order_id, @result_code AS result_code, @result_msg AS result_msg;

-- ========================================
-- 测试存储过程 sp_get_monthly_report
-- ========================================

-- 测试6：获取2026年1月的销售报表
SELECT '=== 测试6:2026年1月销售报表 ===' AS test_case;
CALL sp_get_monthly_report(2026, 1);

-- 测试7：获取2026年2月的销售报表
SELECT '=== 测试7:2026年2月销售报表 ===' AS test_case;
CALL sp_get_monthly_report(2026, 2);

-- 测试8：获取没有数据的月份（2026年12月）
SELECT '=== 测试8:2026年12月销售报表(无数据) ===' AS test_case;
CALL sp_get_monthly_report(2026, 12);

-- ========================================
-- 清理测试数据
-- ========================================
SELECT '=== 清理测试数据 ===' AS cleanup;

-- 删除测试创建的订单明细
DELETE FROM order_item WHERE order_id IN (
    SELECT order_id FROM sales_order 
    WHERE customer_id IN (1, 3, 4) 
    AND create_time >= NOW() - INTERVAL 1 DAY
);

-- 删除测试创建的订单
DELETE FROM sales_order WHERE customer_id IN (1, 3, 4) 
AND create_time >= NOW() - INTERVAL 1 DAY;

-- 恢复车辆状态
UPDATE car SET status = '在库' WHERE vin IN ('VIN00000000000001', 'VIN00000000000006', 'VIN00000000000009');

SELECT '测试数据清理完成' AS message;
