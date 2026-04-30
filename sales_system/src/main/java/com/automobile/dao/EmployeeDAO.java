package com.automobile.dao;

import com.automobile.db.DBUtil;
import com.automobile.model.Employee;
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

    /**
     * 查询所有员工
     */
    public static void listAllEmployees() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT employee_id, name, job_number, role, department FROM employee ORDER BY employee_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("\n--- 员工列表 ---\n");
            System.out.println(String.format("%-10s\t%-20s\t%-15s\t%-10s\t%-15s", "员工ID", "姓名", "工号", "职位", "部门"));
            System.out.println("-------------------------------------------------------------------");
            
            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String name = rs.getString("name");
                String jobNumber = rs.getString("job_number");
                String role = rs.getString("role");
                String department = rs.getString("department");
                System.out.println(String.format("%-10d\t%-20s\t%-15s\t%-10s\t%-15s", employeeId, name, jobNumber, role, department));
            }
            
        } catch (Exception e) {
            System.out.println("查询员工列表失败！");
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
     * 根据工号查询员工信息
     */
    public static Employee getEmployeeByJobNumber(String jobNumber) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Employee employee = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT employee_id, name, job_number, role, department FROM employee WHERE job_number = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, jobNumber);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                employee = new Employee();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setName(rs.getString("name"));
                employee.setJobNumber(rs.getString("job_number"));
                employee.setRole(rs.getString("role"));
                employee.setDepartment(rs.getString("department"));
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
        
        return employee;
    }
}
