-- 重置数据库脚本：删除现有数据库并重新创建
-- 执行此脚本后，再执行 01_create_schema.sql 和 02_init_data.sql

DROP DATABASE IF EXISTS car_sales;
