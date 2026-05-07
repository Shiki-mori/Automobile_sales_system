package com.automobile.dao;

import com.automobile.db.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class IntentionDAO {

    /**
     * 创建意向记录（使用外部连接，支持事务）
     */
    public static boolean createIntention(Connection conn, int customerId, int modelId, String level, 
                                          String note, int followEmployeeId, String nextContactTime) {
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            String sql = "INSERT INTO intention (customer_id, model_id, level, note, follow_employee_id, next_contact_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, modelId);
            pstmt.setString(3, level);
            pstmt.setString(4, note);
            pstmt.setInt(5, followEmployeeId);
            pstmt.setString(6, nextContactTime);
            
            int rows = pstmt.executeUpdate();
            success = rows > 0;
            
        } catch (Exception e) {
            System.out.println("创建意向失败！");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }

    /**
     * 创建意向记录（使用内部连接，不支持事务）
     */
    public static boolean createIntention(int customerId, int modelId, String level, 
                                          String note, int followEmployeeId, String nextContactTime) {
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = DBUtil.getConnection();
            success = createIntention(conn, customerId, modelId, level, note, followEmployeeId, nextContactTime);
        } catch (Exception e) {
            System.out.println("创建意向失败！");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }
}
