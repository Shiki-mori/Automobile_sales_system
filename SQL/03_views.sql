USE car_sales;

-- 视图1：销售顾问业绩
CREATE OR REPLACE VIEW v_sales_performance AS
SELECT
    e.employee_id,
    e.name AS employee_name,
    e.job_number,
    COUNT(so.order_id) AS total_orders,
    IFNULL(SUM(so.total_amount), 0) AS total_sales_amount,
    IFNULL(SUM(so.total_amount - c.purchase_price), 0) AS total_gross_profit,
    IFNULL(AVG(so.total_amount), 0) AS avg_order_amount,
    MIN(so.create_time) AS first_order_date,
    MAX(so.create_time) AS last_order_date
FROM employee e
LEFT JOIN sales_order so
    ON e.employee_id = so.employee_id
   AND so.status IN ('已锁定', '已完成')
LEFT JOIN car c
    ON so.vin = c.vin
WHERE e.role = '销售'
GROUP BY e.employee_id, e.name, e.job_number;

-- 视图2：各车型库存汇总
CREATE OR REPLACE VIEW v_inventory_summary AS
SELECT
    b.brand_id,
    b.name AS brand_name,
    m.model_id,
    m.series_name,
    m.year,
    m.config_name,
    m.guide_price,
    m.type AS model_type,
    SUM(CASE WHEN c.status = '在库' THEN 1 ELSE 0 END) AS available_count,
    SUM(CASE WHEN c.status = '已锁定' THEN 1 ELSE 0 END) AS locked_count,
    SUM(CASE WHEN c.status = '在途' THEN 1 ELSE 0 END) AS in_transit_count,
    SUM(CASE WHEN c.status = '已售出' THEN 1 ELSE 0 END) AS sold_count,
    COUNT(c.vin) AS total_count
FROM model m
JOIN brand b ON m.brand_id = b.brand_id
LEFT JOIN car c ON m.model_id = c.model_id
GROUP BY
    b.brand_id, b.name,
    m.model_id, m.series_name, m.year, m.config_name, m.guide_price, m.type;

-- 视图3：客户价值分析
-- 销售和售后先分别聚合，再和客户表关联，避免重复统计
CREATE OR REPLACE VIEW v_customer_value AS
SELECT
    cu.customer_id,
    cu.name AS customer_name,
    cu.gender,
    cu.phone,
    cu.first_visit_date,
    IFNULL(s.purchase_count, 0) AS purchase_count,
    IFNULL(s.total_consumption, 0) AS total_consumption,
    s.last_purchase_date,
    CASE
        WHEN IFNULL(s.total_consumption, 0) > 300000 THEN '金卡客户'
        WHEN IFNULL(s.total_consumption, 0) >= 100000 THEN '银卡客户'
        ELSE '普通客户'
    END AS customer_level,
    IFNULL(v.service_count, 0) AS service_count,
    IFNULL(v.total_service_cost, 0) AS total_service_cost
FROM customer cu
LEFT JOIN (
    SELECT
        so.customer_id,
        COUNT(so.order_id) AS purchase_count,
        SUM(so.total_amount) AS total_consumption,
        MAX(so.create_time) AS last_purchase_date
    FROM sales_order so
    WHERE so.status = '已完成'
    GROUP BY so.customer_id
) s ON cu.customer_id = s.customer_id
LEFT JOIN (
    SELECT
        svo.customer_id,
        COUNT(svo.service_id) AS service_count,
        SUM(svo.total_cost) AS total_service_cost
    FROM service_order svo
    WHERE svo.status = '已完成'
    GROUP BY svo.customer_id
) v ON cu.customer_id = v.customer_id;
