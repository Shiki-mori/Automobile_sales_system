package com.automobile.dao;

import com.automobile.db.DBUtil;
import com.automobile.model.ReportDTO.SalesPerformance;
import com.automobile.model.ReportDTO.ModelSales;
import com.automobile.model.ReportDTO.MonthlyReport;
import com.automobile.model.ReportDTO.DailySales;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    /**
     * 查询销售业绩榜（基于v_sales_performance视图）
     * @param startDate 开始日期，null表示不限制
     * @param endDate 结束日期，null表示不限制
     * @return 销售业绩列表
     */
    public static List<SalesPerformance> getSalesPerformance(String startDate, String endDate) {
        List<SalesPerformance> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            
            String sql = "SELECT employee_id, employee_name, job_number, total_orders, " +
                        "total_sales_amount, total_gross_profit, avg_order_amount, " +
                        "first_order_date, last_order_date " +
                        "FROM v_sales_performance " +
                        "WHERE 1=1 ";
            
            if (startDate != null && !startDate.isEmpty()) {
                sql += "AND first_order_date >= ? ";
            }
            if (endDate != null && !endDate.isEmpty()) {
                sql += "AND last_order_date <= ? ";
            }
            
            sql += "ORDER BY total_sales_amount DESC, total_orders DESC";
            
            pstmt = conn.prepareStatement(sql);
            
            int paramIndex = 1;
            if (startDate != null && !startDate.isEmpty()) {
                pstmt.setString(paramIndex++, startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                pstmt.setString(paramIndex++, endDate);
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                SalesPerformance sp = new SalesPerformance();
                sp.setEmployeeId(rs.getInt("employee_id"));
                sp.setEmployeeName(rs.getString("employee_name"));
                sp.setJobNumber(rs.getString("job_number"));
                sp.setTotalOrders(rs.getInt("total_orders"));
                sp.setTotalSalesAmount(rs.getDouble("total_sales_amount"));
                sp.setTotalGrossProfit(rs.getDouble("total_gross_profit"));
                sp.setAvgOrderAmount(rs.getDouble("avg_order_amount"));
                sp.setFirstOrderDate(rs.getString("first_order_date"));
                sp.setLastOrderDate(rs.getString("last_order_date"));
                list.add(sp);
            }
            
            // 设置排名
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setRank(i + 1);
            }
            
        } catch (Exception e) {
            System.out.println("查询销售业绩失败！");
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
        
        return list;
    }

    /**
     * 查询畅销车型排行（Top 5）
     * @param startDate 开始日期，null表示不限制
     * @param endDate 结束日期，null表示不限制
     * @return 畅销车型列表
     */
    public static List<ModelSales> getBestSellingModels(String startDate, String endDate) {
        List<ModelSales> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            
            String sql = "SELECT " +
                        "m.model_id, " +
                        "b.name AS brand_name, " +
                        "m.series_name, " +
                        "m.config_name, " +
                        "COUNT(so.order_id) AS sales_count, " +
                        "SUM(so.total_amount) AS total_sales_amount " +
                        "FROM sales_order so " +
                        "JOIN car c ON so.vin = c.vin " +
                        "JOIN model m ON c.model_id = m.model_id " +
                        "JOIN brand b ON m.brand_id = b.brand_id " +
                        "WHERE so.status IN ('已完成', '已锁定') ";
            
            if (startDate != null && !startDate.isEmpty()) {
                sql += "AND so.create_time >= ? ";
            }
            if (endDate != null && !endDate.isEmpty()) {
                sql += "AND so.create_time <= ? ";
            }
            
            sql += "GROUP BY m.model_id, b.name, m.series_name, m.config_name " +
                   "ORDER BY sales_count DESC, total_sales_amount DESC " +
                   "LIMIT 5";
            
            pstmt = conn.prepareStatement(sql);
            
            int paramIndex = 1;
            if (startDate != null && !startDate.isEmpty()) {
                pstmt.setString(paramIndex++, startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                pstmt.setString(paramIndex++, endDate);
            }
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ModelSales ms = new ModelSales();
                ms.setModelId(rs.getInt("model_id"));
                ms.setBrandName(rs.getString("brand_name"));
                ms.setSeriesName(rs.getString("series_name"));
                ms.setConfigName(rs.getString("config_name"));
                ms.setSalesCount(rs.getInt("sales_count"));
                ms.setTotalSalesAmount(rs.getDouble("total_sales_amount"));
                list.add(ms);
            }
            
            // 设置排名
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setRank(i + 1);
            }
            
        } catch (Exception e) {
            System.out.println("查询畅销车型失败！");
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
        
        return list;
    }

    /**
     * 调用存储过程生成月度销售统计
     * @param year 年份
     * @param month 月份
     * @return 月度报表数据
     */
    public static MonthlyReport callMonthlyReport(int year, int month) {
        MonthlyReport report = new MonthlyReport();
        report.setYear(year);
        report.setMonth(month);
        
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            cstmt = conn.prepareCall("{CALL sp_get_monthly_report(?, ?)}");
            cstmt.setInt(1, year);
            cstmt.setInt(2, month);
            
            // 第一个结果集：总体统计
            boolean hasResult = cstmt.execute();
            if (hasResult) {
                rs = cstmt.getResultSet();
                if (rs.next()) {
                    report.setTotalOrders(rs.getInt("total_orders"));
                    report.setCompletedOrders(rs.getInt("completed_orders"));
                    report.setLockedOrders(rs.getInt("locked_orders"));
                    report.setCancelledOrders(rs.getInt("cancelled_orders"));
                    report.setTotalSalesAmount(rs.getDouble("total_sales_amount"));
                    report.setTotalDeposit(rs.getDouble("total_deposit"));
                    report.setAvgOrderAmount(rs.getDouble("avg_order_amount"));
                }
                rs.close();
            }
            
            // 第二个结果集：销售顾问业绩
            if (cstmt.getMoreResults()) {
                rs = cstmt.getResultSet();
                List<SalesPerformance> employeePerformance = new ArrayList<>();
                while (rs.next()) {
                    SalesPerformance sp = new SalesPerformance();
                    sp.setEmployeeId(rs.getInt("employee_id"));
                    sp.setEmployeeName(rs.getString("employee_name"));
                    sp.setTotalOrders(rs.getInt("order_count"));
                    sp.setTotalSalesAmount(rs.getDouble("total_sales"));
                    employeePerformance.add(sp);
                }
                
                // 设置排名
                for (int i = 0; i < employeePerformance.size(); i++) {
                    employeePerformance.get(i).setRank(i + 1);
                }
                
                report.setEmployeePerformance(employeePerformance);
                rs.close();
            }
            
            // 第三个结果集：车型销售情况
            if (cstmt.getMoreResults()) {
                rs = cstmt.getResultSet();
                List<ModelSales> modelSales = new ArrayList<>();
                while (rs.next()) {
                    ModelSales ms = new ModelSales();
                    ms.setModelId(rs.getInt("model_id"));
                    ms.setBrandName(rs.getString("brand_name"));
                    ms.setSeriesName(rs.getString("series_name"));
                    ms.setConfigName(rs.getString("config_name"));
                    ms.setSalesCount(rs.getInt("sales_count"));
                    ms.setTotalSalesAmount(rs.getDouble("total_sales_amount"));
                    modelSales.add(ms);
                }
                
                // 设置排名
                for (int i = 0; i < modelSales.size(); i++) {
                    modelSales.get(i).setRank(i + 1);
                }
                
                report.setModelSales(modelSales);
                rs.close();
            }
            
            // 第四个结果集：每日销售趋势
            if (cstmt.getMoreResults()) {
                rs = cstmt.getResultSet();
                List<DailySales> dailySales = new ArrayList<>();
                while (rs.next()) {
                    DailySales ds = new DailySales();
                    ds.setDay(rs.getInt("day"));
                    ds.setDailyOrders(rs.getInt("daily_orders"));
                    ds.setDailySales(rs.getDouble("daily_sales"));
                    dailySales.add(ds);
                }
                report.setDailySales(dailySales);
                rs.close();
            }
            
        } catch (Exception e) {
            System.out.println("生成月度报表失败！");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return report;
    }
}
