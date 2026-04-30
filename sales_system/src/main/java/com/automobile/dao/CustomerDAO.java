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
}
