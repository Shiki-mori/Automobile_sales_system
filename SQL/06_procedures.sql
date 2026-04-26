USE car_sales;

-- ========================================
-- 存储过程1：创建销售订单
-- ========================================
-- 功能：封装创建销售订单的复杂业务逻辑
-- 参数：
--   p_customer_id: 客户ID
--   p_employee_id: 销售顾问ID
--   p_vin: 车辆VIN码
--   p_car_price: 车辆价格
--   p_insurance_fee: 保险费用
--   p_tax_fee: 购置税
--   p_service_fee: 服务费
--   p_other_fee: 其他费用
--   p_deposit: 定金
-- 返回：订单ID (OUT参数)
-- p_result_code: 结果代码 (0=成功, -1=异常, -2=车辆状态异常)
-- p_result_msg: 结果消息
-- 使用事务确保数据一致性
DROP PROCEDURE IF EXISTS sp_create_sales_order;
DELIMITER //
CREATE PROCEDURE sp_create_sales_order(
    IN p_customer_id INT,
    IN p_employee_id INT,
    IN p_vin CHAR(17),
    IN p_car_price DECIMAL(10,2),
    IN p_insurance_fee DECIMAL(10,2),
    IN p_tax_fee DECIMAL(10,2),
    IN p_service_fee DECIMAL(10,2),
    IN p_other_fee DECIMAL(10,2),
    IN p_deposit DECIMAL(10,2),
    OUT p_order_id INT,
    OUT p_result_code INT,
    OUT p_result_msg VARCHAR(200)
)
BEGIN
    DECLARE v_car_status VARCHAR(20);
    DECLARE v_total_amount DECIMAL(10,2);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1;
        SET p_result_msg = '创建订单失败，发生异常';
    END;
    
    -- 开始事务
    START TRANSACTION;
    
    -- 检查车辆状态是否为"在库"
    SELECT status INTO v_car_status FROM car WHERE vin = p_vin;
    
    IF v_car_status != '在库' THEN
        ROLLBACK;
        SET p_result_code = -2;
        SET p_result_msg = CONCAT('车辆状态异常，当前状态：', v_car_status);
    ELSE
        -- 计算总金额
        SET v_total_amount = p_car_price + p_insurance_fee + p_tax_fee + p_service_fee + p_other_fee;
        
        -- 插入销售订单
        INSERT INTO sales_order (
            customer_id, 
            employee_id, 
            vin, 
            total_amount, 
            deposit, 
            status, 
            create_time
        ) VALUES (
            p_customer_id, 
            p_employee_id, 
            p_vin, 
            v_total_amount, 
            p_deposit, 
            '已创建', 
            NOW()
        );
        
        -- 获取新生成的订单ID
        SET p_order_id = LAST_INSERT_ID();
        
        -- 插入订单明细 - 车辆费用
        IF p_car_price > 0 THEN
            INSERT INTO order_item (order_id, type, description, amount)
            VALUES (p_order_id, '车辆费用', '车辆销售价格', p_car_price);
        END IF;
        
        -- 插入订单明细 - 保险费用
        IF p_insurance_fee > 0 THEN
            INSERT INTO order_item (order_id, type, description, amount)
            VALUES (p_order_id, '保险费用', '车辆保险', p_insurance_fee);
        END IF;
        
        -- 插入订单明细 - 购置税
        IF p_tax_fee > 0 THEN
            INSERT INTO order_item (order_id, type, description, amount)
            VALUES (p_order_id, '购置税', '车辆购置税', p_tax_fee);
        END IF;
        
        -- 插入订单明细 - 服务费
        IF p_service_fee > 0 THEN
            INSERT INTO order_item (order_id, type, description, amount)
            VALUES (p_order_id, '服务费', '销售服务费', p_service_fee);
        END IF;
        
        -- 插入订单明细 - 其他费用
        IF p_other_fee > 0 THEN
            INSERT INTO order_item (order_id, type, description, amount)
            VALUES (p_order_id, '其他费用', '其他费用', p_other_fee);
        END IF;
        
        -- 锁定车辆库存，确保事务一致性
        UPDATE car 
        SET status = '已锁定' 
        WHERE vin = p_vin;
        
        -- 提交事务
        COMMIT;
        SET p_result_code = 0;
        SET p_result_msg = '订单创建成功';
    END IF;
END//
DELIMITER ;

-- ========================================
-- 存储过程2：获取月度销售报表
-- ========================================
-- 功能：根据年份和月份返回该月的销售统计报表
-- 参数：
--   p_year: 年份
--   p_month: 月份
-- 返回：该月的销售统计数据
DROP PROCEDURE IF EXISTS sp_get_monthly_report;
DELIMITER //
CREATE PROCEDURE sp_get_monthly_report(
    IN p_year INT,
    IN p_month INT
)
BEGIN
    -- 统计该月订单总数
    SELECT 
        COUNT(*) AS total_orders,
        SUM(CASE WHEN status = '已完成' THEN 1 ELSE 0 END) AS completed_orders,
        SUM(CASE WHEN status = '已锁定' THEN 1 ELSE 0 END) AS locked_orders,
        SUM(CASE WHEN status = '已取消' THEN 1 ELSE 0 END) AS cancelled_orders,
        SUM(total_amount) AS total_sales_amount,
        SUM(deposit) AS total_deposit,
        AVG(total_amount) AS avg_order_amount
    FROM sales_order
    WHERE YEAR(create_time) = p_year 
      AND MONTH(create_time) = p_month;
    
    -- 按销售顾问统计该月业绩
    SELECT 
        e.employee_id,
        e.name AS employee_name,
        COUNT(so.order_id) AS order_count,
        SUM(so.total_amount) AS total_sales,
        SUM(CASE WHEN so.status = '已完成' THEN so.total_amount ELSE 0 END) AS completed_sales
    FROM employee e
    LEFT JOIN sales_order so ON e.employee_id = so.employee_id 
        AND YEAR(so.create_time) = p_year 
        AND MONTH(so.create_time) = p_month
    WHERE e.role = '销售'
    GROUP BY e.employee_id, e.name
    ORDER BY total_sales DESC;
    
    -- 按车型统计该月销售情况
    SELECT 
        m.model_id,
        b.name AS brand_name,
        m.series_name,
        m.config_name,
        COUNT(so.order_id) AS sales_count,
        SUM(so.total_amount) AS total_sales_amount
    FROM model m
    LEFT JOIN car c ON m.model_id = c.model_id
    LEFT JOIN sales_order so ON c.vin = so.vin 
        AND YEAR(so.create_time) = p_year 
        AND MONTH(so.create_time) = p_month
    LEFT JOIN brand b ON m.brand_id = b.brand_id
    GROUP BY m.model_id, b.name, m.series_name, m.config_name
    ORDER BY sales_count DESC;
    
    -- 该月每日销售趋势
    SELECT 
        DAY(create_time) AS day,
        COUNT(*) AS daily_orders,
        SUM(total_amount) AS daily_sales
    FROM sales_order
    WHERE YEAR(create_time) = p_year 
      AND MONTH(create_time) = p_month
    GROUP BY DAY(create_time)
    ORDER BY day;
END//
DELIMITER ;
