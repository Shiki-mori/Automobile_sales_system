package com.automobile.dao;

import com.automobile.db.DBUtil;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SalesOrderDAO {

    /**
     * 调用存储过程创建销售订单
     * 
     * @param customerId 客户ID
     * @param employeeId 销售顾问ID
     * @param vin 车辆VIN码
     * @param carPrice 车辆价格
     * @param insuranceFee 保险费用
     * @param taxFee 购置税
     * @param serviceFee 服务费
     * @param otherFee 其他费用
     * @param deposit 定金
     * @return 包含订单ID、结果代码和结果消息的数组
     */
    public static int[] createSalesOrder(int customerId, int employeeId, String vin,
            double carPrice, double insuranceFee, double taxFee,
            double serviceFee, double otherFee, double deposit) {
        
        int[] result = new int[2]; // result[0] = order_id, result[1] = result_code
        String resultMsg = "";
        
        Connection conn = null;
        CallableStatement cstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            
            // 调用存储过程 sp_create_sales_order
            cstmt = conn.prepareCall("{call sp_create_sales_order(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            
            // 设置输入参数（使用参数化查询防SQL注入）
            cstmt.setInt(1, customerId);
            cstmt.setInt(2, employeeId);
            cstmt.setString(3, vin);
            cstmt.setDouble(4, carPrice);
            cstmt.setDouble(5, insuranceFee);
            cstmt.setDouble(6, taxFee);
            cstmt.setDouble(7, serviceFee);
            cstmt.setDouble(8, otherFee);
            cstmt.setDouble(9, deposit);
            
            // 注册输出参数
            cstmt.registerOutParameter(10, Types.INTEGER); // p_order_id
            cstmt.registerOutParameter(11, Types.INTEGER); // p_result_code
            cstmt.registerOutParameter(12, Types.VARCHAR);  // p_result_msg
            
            // 执行存储过程
            cstmt.execute();
            
            // 获取输出参数
            result[0] = cstmt.getInt(10); // order_id
            result[1] = cstmt.getInt(11); // result_code
            resultMsg = cstmt.getString(12); // result_msg
            
            System.out.println("存储过程执行结果: " + resultMsg);
            
        } catch (Exception e) {
            System.out.println("创建订单失败！");
            e.printStackTrace();
            result[1] = -1; // 异常
        } finally {
            try {
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }

    /**
     * 查询指定员工的订单列表
     */
    public static List<OrderInfo> getOrdersByEmployee(int employeeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderInfo> orders = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT o.order_id, c.name as customer_name, o.vin, " +
                        "m.series_name, m.config_name, car.color, o.total_amount, " +
                        "o.deposit, o.status, o.create_time, o.delivery_time " +
                        "FROM sales_order o " +
                        "JOIN customer c ON o.customer_id = c.customer_id " +
                        "JOIN car car ON o.vin = car.vin " +
                        "JOIN model m ON car.model_id = m.model_id " +
                        "WHERE o.employee_id = ? " +
                        "ORDER BY o.create_time DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                OrderInfo order = new OrderInfo();
                order.orderId = rs.getInt("order_id");
                order.customerName = rs.getString("customer_name");
                order.vin = rs.getString("vin");
                order.seriesName = rs.getString("series_name");
                order.configName = rs.getString("config_name");
                order.color = rs.getString("color");
                order.totalAmount = rs.getDouble("total_amount");
                order.deposit = rs.getDouble("deposit");
                order.status = rs.getString("status");
                order.createTime = dateFormat.format(rs.getTimestamp("create_time"));
                
                java.sql.Timestamp deliveryTime = rs.getTimestamp("delivery_time");
                if (deliveryTime != null) {
                    order.deliveryTime = dateFormat.format(deliveryTime);
                } else {
                    order.deliveryTime = "";
                }
                
                orders.add(order);
            }
            
        } catch (Exception e) {
            System.out.println("查询订单列表失败！");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return orders;
    }

    /**
     * 根据订单ID查询订单详细信息
     */
    public static OrderInfo getOrderById(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        OrderInfo order = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT o.order_id, c.name as customer_name, o.vin, " +
                        "m.series_name, m.config_name, car.color, o.total_amount, " +
                        "o.deposit, o.status, o.create_time, o.delivery_time " +
                        "FROM sales_order o " +
                        "JOIN customer c ON o.customer_id = c.customer_id " +
                        "JOIN car car ON o.vin = car.vin " +
                        "JOIN model m ON car.model_id = m.model_id " +
                        "WHERE o.order_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            if (rs.next()) {
                order = new OrderInfo();
                order.orderId = rs.getInt("order_id");
                order.customerName = rs.getString("customer_name");
                order.vin = rs.getString("vin");
                order.seriesName = rs.getString("series_name");
                order.configName = rs.getString("config_name");
                order.color = rs.getString("color");
                order.totalAmount = rs.getDouble("total_amount");
                order.deposit = rs.getDouble("deposit");
                order.status = rs.getString("status");
                order.createTime = dateFormat.format(rs.getTimestamp("create_time"));
                
                java.sql.Timestamp deliveryTime = rs.getTimestamp("delivery_time");
                if (deliveryTime != null) {
                    order.deliveryTime = dateFormat.format(deliveryTime);
                } else {
                    order.deliveryTime = "";
                }
            }
            
        } catch (Exception e) {
            System.out.println("查询订单详情失败！");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return order;
    }

    /**
     * 查询订单明细信息
     */
    public static List<OrderItem> getOrderItems(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<OrderItem> items = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT item_id, type, description, amount " +
                        "FROM order_item " +
                        "WHERE order_id = ? " +
                        "ORDER BY item_id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.itemId = rs.getInt("item_id");
                item.type = rs.getString("type");
                item.description = rs.getString("description");
                item.amount = rs.getDouble("amount");
                items.add(item);
            }
            
        } catch (Exception e) {
            System.out.println("查询订单明细失败！");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return items;
    }

    /**
     * 订单信息数据类
     */
    public static class OrderInfo {
        public int orderId;
        public String customerName;
        public String vin;
        public String seriesName;
        public String configName;
        public String color;
        public double totalAmount;
        public double deposit;
        public String status;
        public String createTime;
        public String deliveryTime;
    }

    /**
     * 订单明细数据类
     */
    public static class OrderItem {
        public int itemId;
        public String type;
        public String description;
        public double amount;
    }
}
