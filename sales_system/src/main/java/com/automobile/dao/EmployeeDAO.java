package com.automobile.dao;

import com.automobile.db.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmployeeDAO {

    /**
     * 查询所有销售顾问
     */
    public static void listSalesEmployees() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT employee_id, name, job_number FROM employee WHERE role = '销售' ORDER BY employee_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("\n--- 销售顾问列表 ---");
            System.out.println(String.format("%-10s\t%-20s\t%-15s", "员工ID", "姓名", "工号"));
            System.out.println("--------------------------------------------");
            
            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String name = rs.getString("name");
                String jobNumber = rs.getString("job_number");
                System.out.println(String.format("%-10d\t%-20s\t%-15s", employeeId, name, jobNumber));
            }
            
        } catch (Exception e) {
            System.out.println("查询销售顾问列表失败！");
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
     * 根据ID查询员工姓名
     */
    public static String getEmployeeName(int employeeId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String name = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT name FROM employee WHERE employee_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employeeId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                name = rs.getString("name");
            }
            
        } catch (Exception e) {
            System.out.println("查询员工失败！");
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
