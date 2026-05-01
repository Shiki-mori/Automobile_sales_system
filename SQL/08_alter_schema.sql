-- 为库存预警功能添加安全库存字段
USE car_sales;

-- 为model表添加安全库存字段
ALTER TABLE model ADD COLUMN safe_stock INT DEFAULT 3 COMMENT '安全库存阈值';

-- 为现有车型设置默认安全库存
UPDATE model SET safe_stock = 3 WHERE safe_stock IS NULL;
