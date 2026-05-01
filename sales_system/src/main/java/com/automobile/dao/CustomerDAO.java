package com.automobile.dao;

import com.automobile.db.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerDAO {

    /**
     * 查询所有客户
     */
    public static void listAllCustomers() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT customer_id, name, phone FROM customer ORDER BY customer_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("\n--- 客户列表 ---");
            System.out.println(String.format("%-10s\t%-20s\t%-15s", "客户ID", "姓名", "电话"));
            System.out.println("---------------------------------------------------");
            
            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                System.out.println(String.format("%-10d\t%-20s\t%-15s", customerId, name, phone));
            }
            
        } catch (Exception e) {
            System.out.println("查询客户列表失败！");
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
    }

    /**
     * 根据ID查询客户姓名
     */
    public static String getCustomerName(int customerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT name FROM customer WHERE customer_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                name = rs.getString("name");
            }
            
        } catch (Exception e) {
            System.out.println("查询客户失败！");
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
        
        return name;
    }

    /**
     * 根据手机号查询客户ID
     */
    public static Integer getCustomerIdByPhone(String phone) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Integer customerId = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT customer_id FROM customer WHERE phone = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, phone);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                customerId = rs.getInt("customer_id");
            }
            
        } catch (Exception e) {
            System.out.println("查询客户失败！");
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
        
        return customerId;
    }

    /**
     * 创建新客户
     */
    public static int createCustomer(String name, String gender, String phone, String idCard, String address) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int customerId = -1;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO customer (name, gender, phone, id_card, address, first_visit_date) " +
                        "VALUES (?, ?, ?, ?, ?, CURDATE())";
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setString(2, gender);
            pstmt.setString(3, phone);
            pstmt.setString(4, idCard);
            pstmt.setString(5, address);
            pstmt.executeUpdate();
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                customerId = rs.getInt(1);
            }
            
        } catch (Exception e) {
            System.out.println("创建客户失败！");
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
        
        return customerId;
    }
}
