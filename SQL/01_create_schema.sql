CREATE DATABASE IF NOT EXISTS car_sales;
USE car_sales;

CREATE TABLE `brand` (
  `brand_id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `model` (
  `model_id` int PRIMARY KEY AUTO_INCREMENT,
  `brand_id` int NOT NULL,
  `series_name` varchar(255),
  `year` int,
  `config_name` varchar(255),
  `guide_price` decimal(10, 2),
  `engine` varchar(50),
  `type` varchar(255)
);

CREATE TABLE `car` (
  `vin` char(17) PRIMARY KEY,
  `model_id` int NOT NULL,
  `color` varchar(255),
  `engine_no` varchar(255) UNIQUE,
  `production_date` date,
  `stock_in_date` date,
  `purchase_price` decimal(10, 2),
  `sale_price` decimal(10, 2),
  `status` enum('在库','已锁定','已售出','在途') NOT NULL
);

CREATE TABLE `customer` (
  `customer_id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `gender` varchar(255),
  `phone` varchar(20) UNIQUE NOT NULL,
  `id_card` varchar(255),
  `address` varchar(255),
  `first_visit_date` date
);

CREATE TABLE `employee` (
  `employee_id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `job_number` varchar(255) UNIQUE NOT NULL,
  `role` varchar(255),
  `department` varchar(255),
  `hire_date` date,
  `manager_id` int
);

CREATE TABLE `intention` (
  `intention_id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `model_id` int NOT NULL,
  `level` varchar(255),
  `note` varchar(255),
  `follow_employee_id` int,
  `next_contact_time` datetime
);

CREATE TABLE `sales_order` (
  `order_id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `employee_id` int NOT NULL,
  `vin` char(17) NOT NULL,
  `total_amount` decimal(10, 2),
  `deposit` decimal(10, 2),
  `status` enum('已创建','已锁定','已完成','已取消') NOT NULL,
  `create_time` datetime,
  `delivery_time` datetime
);

CREATE TABLE `order_item` (
  `item_id` int PRIMARY KEY AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `type` varchar(255),
  `description` varchar(255),
  `amount` decimal(10, 2)
);

CREATE TABLE `service_order` (
  `service_id` int PRIMARY KEY AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `vin` char(17) NOT NULL,
  `service_type` varchar(255),
  `employee_id` int,
  `create_time` datetime,
  `expected_finish_time` datetime,
  `total_cost` decimal(10, 2),
  `status` enum('待服务','进行中','已完成','已取消') NOT NULL
);

CREATE TABLE `service_item` (
  `item_id` int PRIMARY KEY AUTO_INCREMENT,
  `service_id` int NOT NULL,
  `name` varchar(255),
  `quantity` int CHECK (`quantity` > 0),
  `price` decimal(10, 2),
  `amount` decimal(10, 2)
);

ALTER TABLE `model` ADD FOREIGN KEY (`brand_id`) REFERENCES `brand` (`brand_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `car` ADD FOREIGN KEY (`model_id`) REFERENCES `model` (`model_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `intention` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `intention` ADD FOREIGN KEY (`model_id`) REFERENCES `model` (`model_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `intention` ADD FOREIGN KEY (`follow_employee_id`) REFERENCES `employee` (`employee_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `sales_order` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `sales_order` ADD FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `sales_order` ADD FOREIGN KEY (`vin`) REFERENCES `car` (`vin`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `order_item` ADD FOREIGN KEY (`order_id`) REFERENCES `sales_order` (`order_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `service_order` ADD FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `service_order` ADD FOREIGN KEY (`vin`) REFERENCES `car` (`vin`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `service_order` ADD FOREIGN KEY (`employee_id`) REFERENCES `employee` (`employee_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `service_item` ADD FOREIGN KEY (`service_id`) REFERENCES `service_order` (`service_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;

ALTER TABLE `employee` ADD FOREIGN KEY (`manager_id`) REFERENCES `employee` (`employee_id`) 
ON DELETE RESTRICT
ON UPDATE CASCADE;