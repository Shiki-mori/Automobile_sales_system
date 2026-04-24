USE car_sales;

-- ========================================
-- 触发器1：订单创建时自动锁定车辆
-- ========================================
-- 功能：在销售订单表插入新订单时
-- 自动将对应库存车辆的状态更新为"已锁定"
-- 触发时机：AFTER INSERT on sales_order
DROP TRIGGER IF EXISTS trg_lock_car_on_order;
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
-- 触发器1.5：订单状态变为已创建时锁定车辆
-- ========================================
-- 功能：当销售订单状态更新为"已创建"时，自动将对应车辆状态更新为"已锁定"
-- 触发时机：AFTER UPDATE on sales_order
DROP TRIGGER IF EXISTS trg_lock_car_on_status_change;
DELIMITER //
CREATE TRIGGER trg_lock_car_on_status_change
AFTER UPDATE ON sales_order
FOR EACH ROW
BEGIN
    -- 当状态从其他值变为"已创建"时执行
    IF NEW.status = '已创建' AND OLD.status != '已创建' 
    THEN
        UPDATE car 
        SET status = '已锁定' 
        WHERE vin = NEW.vin;
    END IF;
END//
DELIMITER ;

-- ========================================
-- 触发器2：订单完成时自动设置交车时间
-- ========================================
-- 功能：在销售订单的订单状态更新为"已完成"时，自动设置交车时间为当前时间
-- 触发时机：BEFORE UPDATE on sales_order
DROP TRIGGER IF EXISTS trg_set_delivery_time;
DELIMITER //
CREATE TRIGGER trg_set_delivery_time
BEFORE UPDATE ON sales_order
FOR EACH ROW
BEGIN
    -- 当状态从其他值变为"已完成"时执行
    IF NEW.status = '已完成' AND OLD.status != '已完成' THEN
        -- 如果交车时间为空，则设置为当前时间
        IF NEW.delivery_time IS NULL THEN
            SET NEW.delivery_time = NOW();
        END IF;
    END IF;
END//
DELIMITER ;

-- ========================================
-- 触发器3：订单完成时更新车辆状态
-- ========================================
-- 功能：在销售订单的订单状态更新为"已完成"时，自动将对应车辆状态更新为"已售出"
-- 触发时机：AFTER UPDATE on sales_order
DROP TRIGGER IF EXISTS trg_update_inventory_on_delivery;
DELIMITER //
CREATE TRIGGER trg_update_inventory_on_delivery
AFTER UPDATE ON sales_order
FOR EACH ROW
BEGIN
    -- 当状态从其他值变为"已完成"时执行
    IF NEW.status = '已完成' AND OLD.status != '已完成' THEN
        -- 更新车辆状态为"已售出"
        UPDATE car 
        SET status = '已售出' 
        WHERE vin = NEW.vin;
    END IF;
END//
DELIMITER ;

-- ========================================
-- 触发器4：订单取消时释放车辆
-- ========================================
-- 功能：当销售订单状态更新为"已取消"时，自动将对应车辆状态更新为"在库"
-- 注意：由于vin字段有UNIQUE约束，取消的订单仍占用vin，但车辆状态可恢复
-- 触发时机：AFTER UPDATE on sales_order
DROP TRIGGER IF EXISTS trg_release_car_on_cancel;
DELIMITER //
CREATE TRIGGER trg_release_car_on_cancel
AFTER UPDATE ON sales_order
FOR EACH ROW
BEGIN
    -- 当状态从其他值变为"已取消"时执行
    IF NEW.status = '已取消' AND OLD.status != '已取消' THEN
        -- 更新车辆状态为"在库"
        UPDATE car 
        SET status = '在库' 
        WHERE vin = NEW.vin;
    END IF;
END//
DELIMITER ;
