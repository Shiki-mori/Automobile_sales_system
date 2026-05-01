package com.automobile.dao;

import com.automobile.db.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 检查VIN码是否已存在
     */
    public static boolean checkVinExists(String vin) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT vin FROM car WHERE vin = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vin);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                exists = true;
            }
            
        } catch (Exception e) {
            System.out.println("检查VIN码失败！");
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
        
        return exists;
    }

    /**
     * 检查发动机号是否已存在
     */
    public static boolean checkEngineNoExists(String engineNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT engine_no FROM car WHERE engine_no = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, engineNo);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                exists = true;
            }
            
        } catch (Exception e) {
            System.out.println("检查发动机号失败！");
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
        
        return exists;
    }

    /**
     * 创建车辆记录
     */
    public static boolean createCar(String vin, int modelId, String color, String engineNo, 
                                   String productionDate, double purchasePrice, double salePrice) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO car (vin, model_id, color, engine_no, production_date, stock_in_date, purchase_price, sale_price, status) " +
                        "VALUES (?, ?, ?, ?, ?, CURDATE(), ?, ?, '在库')";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, vin);
            pstmt.setInt(2, modelId);
            pstmt.setString(3, color);
            pstmt.setString(4, engineNo);
            pstmt.setString(5, productionDate);
            pstmt.setDouble(6, purchasePrice);
            pstmt.setDouble(7, salePrice);
            
            int rows = pstmt.executeUpdate();
            success = rows > 0;
            
        } catch (Exception e) {
            System.out.println("创建车辆记录失败！");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }

    /**
     * 多条件动态查询车辆库存
     */
    public static List<CarSearchResult> searchCars(String brandName, String seriesName, String color, 
                                                  String status, Double minPrice, Double maxPrice, String vin) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CarSearchResult> cars = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            
            // 构建动态SQL
            StringBuilder sql = new StringBuilder(
                "SELECT c.vin, b.name as brand_name, m.series_name, m.config_name, " +
                "c.color, c.purchase_price, c.sale_price, c.status, c.stock_in_date " +
                "FROM car c " +
                "JOIN model m ON c.model_id = m.model_id " +
                "JOIN brand b ON m.brand_id = b.brand_id " +
                "WHERE 1=1 "
            );
            
            List<Object> params = new ArrayList<>();
            
            if (brandName != null && !brandName.isEmpty()) {
                sql.append("AND b.name LIKE ? ");
                params.add("%" + brandName + "%");
            }
            
            if (seriesName != null && !seriesName.isEmpty()) {
                sql.append("AND m.series_name LIKE ? ");
                params.add("%" + seriesName + "%");
            }
            
            if (color != null && !color.isEmpty()) {
                sql.append("AND c.color LIKE ? ");
                params.add("%" + color + "%");
            }
            
            if (status != null && !status.isEmpty()) {
                sql.append("AND c.status = ? ");
                params.add(status);
            }
            
            if (minPrice != null) {
                sql.append("AND c.sale_price >= ? ");
                params.add(minPrice);
            }
            
            if (maxPrice != null) {
                sql.append("AND c.sale_price <= ? ");
                params.add(maxPrice);
            }
            
            if (vin != null && !vin.isEmpty()) {
                sql.append("AND c.vin LIKE ? ");
                params.add("%" + vin + "%");
            }
            
            sql.append("ORDER BY c.stock_in_date DESC");
            
            pstmt = conn.prepareStatement(sql.toString());
            
            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            rs = pstmt.executeQuery();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            while (rs.next()) {
                CarSearchResult car = new CarSearchResult();
                car.vin = rs.getString("vin");
                car.brandName = rs.getString("brand_name");
                car.seriesName = rs.getString("series_name");
                car.configName = rs.getString("config_name");
                car.color = rs.getString("color");
                car.purchasePrice = rs.getDouble("purchase_price");
                car.salePrice = rs.getDouble("sale_price");
                car.status = rs.getString("status");
                car.stockInDate = dateFormat.format(rs.getDate("stock_in_date"));
                cars.add(car);
            }
            
        } catch (Exception e) {
            System.out.println("查询车辆库存失败！");
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
        
        return cars;
    }

    /**
     * 查询库存预警报表（基于Q7查询，暂时使用硬编码安全库存）
     */
    public static List<InventoryAlert> getInventoryAlert() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<InventoryAlert> alerts = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            // 基于Q7查询，使用硬编码安全库存阈值3（与Q7保持一致）
            String sql = "SELECT " +
                        "m.model_id, " +
                        "b.name AS brand_name, " +
                        "m.series_name, " +
                        "m.year, " +
                        "m.config_name, " +
                        "m.guide_price, " +
                        "3 AS safe_stock, " +
                        "COUNT(CASE WHEN c.status = '在库' THEN 1 END) AS current_stock, " +
                        "COUNT(CASE WHEN c.status = '已锁定' THEN 1 END) AS locked_count, " +
                        "COUNT(CASE WHEN c.status = '在途' THEN 1 END) AS in_transit_count, " +
                        "COUNT(*) AS total_count, " +
                        "3 - COUNT(CASE WHEN c.status = '在库' THEN 1 END) AS shortage_quantity " +
                        "FROM model m " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "LEFT JOIN car c ON m.model_id = c.model_id " +
                        "GROUP BY m.model_id, b.name, m.series_name, m.year, m.config_name, m.guide_price " +
                        "HAVING COUNT(CASE WHEN c.status = '在库' THEN 1 END) < 3 " +
                        "ORDER BY shortage_quantity DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                InventoryAlert alert = new InventoryAlert();
                alert.modelId = rs.getInt("model_id");
                alert.brandName = rs.getString("brand_name");
                alert.seriesName = rs.getString("series_name");
                alert.year = rs.getInt("year");
                alert.configName = rs.getString("config_name");
                alert.guidePrice = rs.getDouble("guide_price");
                alert.safeStock = rs.getInt("safe_stock");
                alert.availableCount = rs.getInt("current_stock");
                alert.lockedCount = rs.getInt("locked_count");
                alert.inTransitCount = rs.getInt("in_transit_count");
                alert.totalCount = rs.getInt("total_count");
                alert.shortage = rs.getInt("shortage_quantity");
                alerts.add(alert);
            }
            
        } catch (Exception e) {
            System.out.println("查询库存预警失败！");
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
        
        return alerts;
    }

    /**
     * 车辆搜索结果数据类
     */
    public static class CarSearchResult {
        public String vin;
        public String brandName;
        public String seriesName;
        public String configName;
        public String color;
        public double purchasePrice;
        public double salePrice;
        public String status;
        public String stockInDate;
    }

    /**
     * 库存预警数据类（基于Q7查询优化）
     */
    public static class InventoryAlert {
        public int modelId;
        public String brandName;
        public String seriesName;
        public int year;
        public String configName;
        public double guidePrice;
        public int safeStock;
        public int availableCount;
        public int lockedCount;
        public int inTransitCount;
        public int totalCount;
        public int shortage;
    }
}
