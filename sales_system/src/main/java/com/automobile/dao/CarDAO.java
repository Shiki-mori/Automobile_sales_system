package com.automobile.dao;

import com.automobile.db.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CarDAO {

    /**
     * 查询所有在库车辆
     */
    public static void listAvailableCars() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT c.vin, m.series_name, m.config_name, c.color, c.sale_price " +
                        "FROM car c " +
                        "JOIN model m ON c.model_id = m.model_id " +
                        "WHERE c.status = '在库' " +
                        "ORDER BY c.vin";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("\n--- 在库车辆列表 ---");
            System.out.println(String.format("%-18s\t%-20s\t%-20s\t%-10s\t%-12s", "VIN码", "车型", "配置", "颜色", "售价"));
            System.out.println("-------------------------------------------------------------------------------------------------");
            
            while (rs.next()) {
                String vin = rs.getString("vin");
                String seriesName = rs.getString("series_name");
                String configName = rs.getString("config_name");
                String color = rs.getString("color");
                double salePrice = rs.getDouble("sale_price");
                System.out.println(String.format("%-18s\t%-20s\t%-20s\t%-10s\t%-12.2f", vin, seriesName, configName, color, salePrice));
            }
            
        } catch (Exception e) {
            System.out.println("查询车辆列表失败！");
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
     * 根据VIN查询车辆信息
     */
    public static String getCarInfo(String vin) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String info = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT m.series_name, m.config_name, c.color, c.sale_price " +
                        "FROM car c " +
                        "JOIN model m ON c.model_id = m.model_id " +
                        "WHERE c.vin = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vin);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String seriesName = rs.getString("series_name");
                String configName = rs.getString("config_name");
                String color = rs.getString("color");
                double salePrice = rs.getDouble("sale_price");
                info = seriesName + " " + configName + " (" + color + ") - 售价: " + salePrice;
            }
            
        } catch (Exception e) {
            System.out.println("查询车辆失败！");
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
        
        return info;
    }

    /**
     * 查询所有车型
     */
    public static void listModels() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT m.model_id, b.name as brand_name, m.series_name, m.year, m.config_name, m.guide_price " +
                        "FROM model m " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "ORDER BY m.model_id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            System.out.println("\n--- 车型列表 ---");
            System.out.println(String.format("%-10s\t%-15s\t%-20s\t%-8s\t%-20s\t%-12s", "车型ID", "品牌", "车系", "年款", "配置", "指导价"));
            System.out.println("----------------------------------------------------------------------------------------");
            
            while (rs.next()) {
                int modelId = rs.getInt("model_id");
                String brandName = rs.getString("brand_name");
                String seriesName = rs.getString("series_name");
                int year = rs.getInt("year");
                String configName = rs.getString("config_name");
                double guidePrice = rs.getDouble("guide_price");
                System.out.println(String.format("%-10d\t%-15s\t%-20s\t%-8d\t%-20s\t%-12.2f", modelId, brandName, seriesName, year, configName, guidePrice));
            }
            
        } catch (Exception e) {
            System.out.println("查询车型列表失败！");
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
     * 根据车型ID查询车型信息
     */
    public static String getModelInfo(int modelId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String info = null;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT b.name as brand_name, m.series_name, m.year, m.config_name, m.guide_price " +
                        "FROM model m " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "WHERE m.model_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, modelId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String brandName = rs.getString("brand_name");
                String seriesName = rs.getString("series_name");
                int year = rs.getInt("year");
                String configName = rs.getString("config_name");
                double guidePrice = rs.getDouble("guide_price");
                info = brandName + " " + seriesName + " " + year + "款 " + configName + " (指导价: " + guidePrice + ")";
            }
            
        } catch (Exception e) {
            System.out.println("查询车型失败！");
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
        
        return info;
    }
}
