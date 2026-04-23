USE car_sales;

-- ========================================
-- 触发器1：订单创建时自动锁定车辆
-- ========================================
-- 功能：在销售订单表插入新订单时，自动将对应库存车辆的状态更新为"已锁定"
-- 触发时机：AFTER INSERT on sales_order
DELIMITER //
CREATE TRIGGER trg_lock_car_on_order
AFTER INSERT ON sales_order
FOR EACH ROW
BEGIN
    -- 只有当订单状态为"已创建"时才锁定车辆
    IF NEW.status = '已创建' THEN
        UPDATE car 
        SET status = '已锁定' 
        WHERE vin = NEW.vin;
    END IF;
END//
DELIMITER ;

-- ========================================
-- 触发器2：订单完成时更新车辆状态和交车时间
-- ========================================
-- 功能：在销售订单的订单状态更新为"已完成"时，自动将对应车辆状态更新为"已售出"，并记录交车时间
-- 触发时机：AFTER UPDATE on sales_order
DELIMITER //
CREATE TRIGGER trg_update_inventory_on_delivery
AFTER UPDATE ON sales_order
FOR EACH ROW
BEGIN
    -- 当状态从其他值变为"已完成"时执行
    -- 防止了其他字段更新（如修改备注）时误触发业务逻辑
    IF NEW.status = '已完成' AND OLD.status != '已完成' THEN
        -- 更新车辆状态为"已售出"
        UPDATE car 
        SET status = '已售出' 
        WHERE vin = NEW.vin;
        
        -- 如果交车时间为空，则设置为当前时间
        IF NEW.delivery_time IS NULL THEN
            UPDATE sales_order 
            SET delivery_time = NOW() 
            WHERE order_id = NEW.order_id;
        END IF;
    END IF;
END//
DELIMITER ;
