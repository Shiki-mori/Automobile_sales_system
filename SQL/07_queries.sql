USE car_sales;

-- ============================================================================
-- Q1: 查询指定时间段内（如2026年第一季度）的销售统计，包括总订单数、总销售额、总毛利
-- 业务问题：管理层需要了解特定季度的整体销售业绩，用于季度总结和决策分析
-- ============================================================================
SELECT 
    COUNT(DISTINCT so.order_id) AS total_orders,
    SUM(so.total_amount) AS total_sales_amount,
    SUM(so.total_amount - c.purchase_price) AS total_gross_profit
FROM sales_order so
JOIN car c ON so.vin = c.vin
WHERE so.status = '已完成'
  AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01'
  AND COALESCE(so.delivery_time, so.create_time) < '2026-04-01';


-- ============================================================================
-- Q2: 查询每位销售顾问的月度/季度业绩（订单数、销售额、毛利），并进行排名
-- 业务问题：销售经理需要评估销售顾问的业绩表现，用于绩效考核和激励
-- ============================================================================
SELECT 
    e.employee_id,
    e.name AS employee_name,
    COUNT(DISTINCT CASE WHEN so.status = '已完成' 
        AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01'
        AND COALESCE(so.delivery_time, so.create_time) < '2026-04-01' 
        THEN so.order_id END) AS order_count,
    COALESCE(SUM(CASE WHEN so.status = '已完成' 
        AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01'
        AND COALESCE(so.delivery_time, so.create_time) < '2026-04-01' 
        THEN so.total_amount END), 0) AS total_sales,
    COALESCE(SUM(CASE WHEN so.status = '已完成' 
        AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01'
        AND COALESCE(so.delivery_time, so.create_time) < '2026-04-01' 
        THEN so.total_amount - c.purchase_price END), 0) AS total_gross_profit,
    RANK() OVER (ORDER BY COALESCE(SUM(CASE WHEN so.status = '已完成' 
        AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01'
        AND COALESCE(so.delivery_time, so.create_time) < '2026-04-01' 
        THEN so.total_amount END), 0) DESC) AS sales_rank
FROM employee e
LEFT JOIN sales_order so ON e.employee_id = so.employee_id
LEFT JOIN car c ON so.vin = c.vin
WHERE e.role = '销售'
GROUP BY e.employee_id, e.name
ORDER BY total_sales DESC;


-- ============================================================================
-- Q3: 查询最畅销的车型Top 5及其销量
-- 业务问题：采购部门需要了解哪些车型最受欢迎，用于优化库存结构
-- ============================================================================
SELECT 
    m.model_id,
    b.name AS brand_name,
    m.series_name,
    m.year,
    m.config_name,
    COUNT(DISTINCT so.order_id) AS sales_count,
    RANK() OVER (ORDER BY COUNT(DISTINCT so.order_id) DESC) AS popularity_rank
FROM model m
JOIN brand b ON m.brand_id = b.brand_id
JOIN car c ON m.model_id = c.model_id
JOIN sales_order so ON c.vin = so.vin
WHERE so.status = '已完成'
GROUP BY m.model_id, b.name, m.series_name, m.year, m.config_name
ORDER BY sales_count DESC
LIMIT 5;


-- ============================================================================
-- Q4: 查询所有"库存周期"（从入库到售出的天数）超过90天的滞销车辆清单
-- 业务问题：库存管理部门需要识别滞销车辆，采取促销或调拨措施
-- 该查询暂时无法进行测试，需要进行数据填充 `stock_in_date`
-- ============================================================================
SELECT 
    c.vin,
    b.name AS brand_name,
    m.series_name,
    m.year,
    m.config_name,
    c.color,
    c.stock_in_date,
    COALESCE(so.delivery_time, so.create_time) AS sale_date,
    DATEDIFF(COALESCE(so.delivery_time, so.create_time), c.stock_in_date) AS inventory_days,
    c.purchase_price,
    c.sale_price
FROM car c
JOIN model m ON c.model_id = m.model_id
JOIN brand b ON m.brand_id = b.brand_id
JOIN sales_order so ON c.vin = so.vin
WHERE so.status = '已完成'
  AND DATEDIFF(COALESCE(so.delivery_time, so.create_time), c.stock_in_date) > 90
ORDER BY inventory_days DESC;


-- ============================================================================
-- Q5: 根据客户的历史消费总额，对客户进行分类（普通客户<10万，银卡客户10-30万，金卡客户>30万）
-- 业务问题：客户关系管理需要根据消费水平对客户进行分级，提供差异化服务
-- ============================================================================
SELECT 
    c.customer_id,
    c.name,
    c.phone,
    COALESCE(SUM(so.total_amount), 0) AS total_consumption,
    CASE 
        WHEN COALESCE(SUM(so.total_amount), 0) < 100000 THEN '普通客户'
        WHEN COALESCE(SUM(so.total_amount), 0) BETWEEN 100000 AND 300000 THEN '银卡客户'
        WHEN COALESCE(SUM(so.total_amount), 0) > 300000 THEN '金卡客户'
    END AS customer_level
FROM customer c
LEFT JOIN sales_order so ON c.customer_id = so.customer_id AND so.status = '已完成'
GROUP BY c.customer_id, c.name, c.phone
ORDER BY total_consumption DESC;


-- ============================================================================
-- Q6: 查询特定客户的完整购车及服务历史
-- 业务问题：客服人员需要查看客户的完整历史记录，提供个性化服务
-- 参数说明：将 customer_id = 1 替换为实际查询的客户ID
-- ============================================================================
-- 购车历史
SELECT 
    '购车' AS record_type,
    so.order_id AS record_id,
    b.name AS brand_name,
    m.series_name,
    m.config_name,
    c.color,
    so.total_amount,
    so.create_time,
    so.delivery_time,
    e.name AS sales_consultant
FROM sales_order so
JOIN car c ON so.vin = c.vin
JOIN model m ON c.model_id = m.model_id
JOIN brand b ON m.brand_id = b.brand_id
LEFT JOIN employee e ON so.employee_id = e.employee_id
WHERE so.customer_id = 1
  AND so.status = '已完成'

UNION ALL

-- 服务历史
SELECT 
    '服务' AS record_type,
    s.service_id AS record_id,
    b.name AS brand_name,
    m.series_name,
    m.config_name,
    c.color,
    s.total_cost,
    s.create_time,
    s.expected_finish_time,
    e.name AS service_technician
FROM service_order s
JOIN car c ON s.vin = c.vin
JOIN model m ON c.model_id = m.model_id
JOIN brand b ON m.brand_id = b.brand_id
LEFT JOIN employee e ON s.employee_id = e.employee_id
WHERE s.customer_id = 1
  AND s.status = '已完成'

ORDER BY create_time DESC;


-- ============================================================================
-- Q7: 生成库存预警报表，列出库存数量低于安全库存阈值的车型
-- 业务问题：采购部门需要及时补充库存，避免热销车型断货
-- 参数说明：安全库存阈值设为3辆，可根据实际业务调整
-- ============================================================================
SELECT 
    m.model_id,
    b.name AS brand_name,
    m.series_name,
    m.year,
    m.config_name,
    m.guide_price,
    COUNT(CASE WHEN c.status = '在库' THEN 1 END) AS current_stock,
    3 AS safety_stock_threshold,
    3 - COUNT(CASE WHEN c.status = '在库' THEN 1 END) AS shortage_quantity
FROM model m
JOIN brand b ON m.brand_id = b.brand_id
LEFT JOIN car c ON m.model_id = c.model_id
GROUP BY m.model_id, b.name, m.series_name, m.year, m.config_name, m.guide_price
HAVING COUNT(CASE WHEN c.status = '在库' THEN 1 END) < 3
ORDER BY shortage_quantity DESC;


-- ============================================================================
-- Q8: 查询各品牌的市场份额及平均成交价分析（自定义复杂查询）
-- 业务问题：管理层需要了解各品牌在销售中的占比和价格定位，用于品牌策略调整
-- ============================================================================
WITH brand_sales AS (
    SELECT 
        b.brand_id,
        b.name AS brand_name,
        COUNT(DISTINCT CASE WHEN so.status = '已完成' 
            AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01' 
            THEN so.order_id END) AS sales_count,
        COALESCE(SUM(CASE WHEN so.status = '已完成' 
            AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01' 
            THEN so.total_amount END), 0) AS total_sales_amount,
        COALESCE(AVG(CASE WHEN so.status = '已完成' 
            AND COALESCE(so.delivery_time, so.create_time) >= '2026-01-01' 
            THEN so.total_amount END), 0) AS avg_deal_price
    FROM brand b
    LEFT JOIN model m ON b.brand_id = m.brand_id
    LEFT JOIN car c ON m.model_id = c.model_id
    LEFT JOIN sales_order so ON c.vin = so.vin
    GROUP BY b.brand_id, b.name
),
total_market AS (
    SELECT 
        SUM(sales_count) AS total_orders,
        SUM(total_sales_amount) AS total_revenue
    FROM brand_sales
)
SELECT 
    bs.brand_name,
    bs.sales_count,
    bs.total_sales_amount,
    bs.avg_deal_price,
    CASE WHEN tm.total_orders > 0 
        THEN ROUND(bs.sales_count * 100.0 / tm.total_orders, 2) 
        ELSE 0 END AS market_share_orders,
    CASE WHEN tm.total_revenue > 0 
        THEN ROUND(bs.total_sales_amount * 100.0 / tm.total_revenue, 2) 
        ELSE 0 END AS market_share_revenue,
    ROUND(bs.avg_deal_price, 2) AS avg_price_formatted,
    CASE 
        WHEN bs.avg_deal_price < 150000 THEN '经济型'
        WHEN bs.avg_deal_price BETWEEN 150000 AND 300000 THEN '中端型'
        WHEN bs.avg_deal_price > 300000 THEN '高端型'
        ELSE '无销售'
    END AS price_positioning
FROM brand_sales bs
CROSS JOIN total_market tm
ORDER BY bs.total_sales_amount DESC;
