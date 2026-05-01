package com.automobile.service;

import com.automobile.dao.ReportDAO;
import com.automobile.model.ReportDTO.SalesPerformance;
import com.automobile.model.ReportDTO.ModelSales;
import com.automobile.model.ReportDTO.MonthlyReport;
import com.automobile.model.ReportDTO.DailySales;
import com.automobile.model.Employee;
import java.util.List;
import java.util.Scanner;

public class ReportService {

    /**
     * 查询销售业绩榜（对应需求Q2）
     */
    public static void querySalesPerformance(Scanner scanner, Employee employee) {
        System.out.println("\n========== 查询销售业绩榜 ==========");
        System.out.println("操作员: " + employee.getName() + " (" + employee.getRole() + " - " + employee.getDepartment() + ")");
        
        // 时间范围选择
        System.out.println("\n请选择时间范围（直接回车使用默认值）：");
        System.out.print("开始日期 (YYYY-MM-DD, 默认不限制): ");
        String startDate = scanner.nextLine().trim();
        if (startDate.isEmpty()) startDate = null;
        
        System.out.print("结束日期 (YYYY-MM-DD, 默认不限制): ");
        String endDate = scanner.nextLine().trim();
        if (endDate.isEmpty()) endDate = null;
        
        // 确认查询
        System.out.println("\n========== 查询条件确认 ==========");
        if (startDate != null) System.out.println("开始日期: " + startDate);
        if (endDate != null) System.out.println("结束日期: " + endDate);
        if (startDate == null && endDate == null) {
            System.out.println("时间范围: 不限制");
        }
        System.out.println("===================================");
        
        System.out.print("\n确认查询？(y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("查询已取消");
            return;
        }
        
        // 执行查询
        System.out.println("\n正在查询销售业绩...");
        List<SalesPerformance> performanceList = ReportDAO.getSalesPerformance(startDate, endDate);
        
        // 显示结果
        if (performanceList.isEmpty()) {
            System.out.println("未找到符合条件的销售业绩数据");
        } else {
            displaySalesPerformance(performanceList);
        }
    }

    /**
     * 查询畅销车型排行（对应需求Q3）
     */
    public static void queryBestSellingModels(Scanner scanner, Employee employee) {
        System.out.println("\n========== 查询畅销车型排行 ==========");
        System.out.println("操作员: " + employee.getName() + " (" + employee.getRole() + " - " + employee.getDepartment() + ")");
        
        // 时间范围选择
        System.out.println("\n请选择时间范围（直接回车使用默认值）：");
        System.out.print("开始日期 (YYYY-MM-DD, 默认不限制): ");
        String startDate = scanner.nextLine().trim();
        if (startDate.isEmpty()) startDate = null;
        
        System.out.print("结束日期 (YYYY-MM-DD, 默认不限制): ");
        String endDate = scanner.nextLine().trim();
        if (endDate.isEmpty()) endDate = null;
        
        // 确认查询
        System.out.println("\n========== 查询条件确认 ==========");
        if (startDate != null) System.out.println("开始日期: " + startDate);
        if (endDate != null) System.out.println("结束日期: " + endDate);
        if (startDate == null && endDate == null) {
            System.out.println("时间范围: 不限制");
        }
        System.out.println("===================================");
        
        System.out.print("\n确认查询？(y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("查询已取消");
            return;
        }
        
        // 执行查询
        System.out.println("\n正在查询畅销车型...");
        List<ModelSales> modelSalesList = ReportDAO.getBestSellingModels(startDate, endDate);
        
        // 显示结果
        if (modelSalesList.isEmpty()) {
            System.out.println("未找到符合条件的车型销售数据");
        } else {
            displayBestSellingModels(modelSalesList);
        }
    }

    /**
     * 生成月度销售统计（调用存储过程sp_get_monthly_report）
     */
    public static void generateMonthlyReport(Scanner scanner, Employee employee) {
        System.out.println("\n========== 生成月度销售统计 ==========");
        System.out.println("操作员: " + employee.getName() + " (" + employee.getRole() + " - " + employee.getDepartment() + ")");
        
        // 输入年份和月份
        System.out.print("请输入年份 (YYYY): ");
        String yearStr = scanner.nextLine().trim();
        int year;
        try {
            year = Integer.parseInt(yearStr);
            if (year < 2020 || year > 2030) {
                System.out.println("年份范围错误，请输入2020-2030之间的年份");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("年份格式错误");
            return;
        }
        
        System.out.print("请输入月份 (1-12): ");
        String monthStr = scanner.nextLine().trim();
        int month;
        try {
            month = Integer.parseInt(monthStr);
            if (month < 1 || month > 12) {
                System.out.println("月份范围错误，请输入1-12之间的月份");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("月份格式错误");
            return;
        }
        
        // 确认查询
        System.out.println("\n========== 查询条件确认 ==========");
        System.out.println("报表时间: " + year + "年" + month + "月");
        System.out.println("===================================");
        
        System.out.print("\n确认生成报表？(y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("报表生成已取消");
            return;
        }
        
        // 调用存储过程
        System.out.println("\n正在生成月度销售报表...");
        MonthlyReport report = ReportDAO.callMonthlyReport(year, month);
        
        // 显示结果
        displayMonthlyReport(report);
    }

    /**
     * 显示销售业绩榜
     */
    private static void displaySalesPerformance(List<SalesPerformance> performanceList) {
        System.out.println("\n--- 销售业绩排行榜 ---");
        System.out.println(String.format("%-12s\t%-12s\t%-12s\t%-8s\t%-15s\t%-15s\t%-15s\t%-12s", 
                "排名", "姓名", "工号", "订单数", "销售总额", "毛利润", "平均订单金额", "最后订单日期"));
        System.out.println("----------------------------------------------------------------------------------------------------------");
        
        for (SalesPerformance sp : performanceList) {
            System.out.println(String.format("%-12d\t%-12s\t%-12s\t%-8d\t%-15.2f\t%-15.2f\t%-15.2f\t%-12s", 
                    sp.getRank(), sp.getEmployeeName(), sp.getJobNumber(), sp.getTotalOrders(),
                    sp.getTotalSalesAmount(), sp.getTotalGrossProfit(), sp.getAvgOrderAmount(),
                    sp.getLastOrderDate() != null ? sp.getLastOrderDate() : "无"));
        }
        
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println("共显示 " + performanceList.size() + " 位销售顾问的业绩");
        
        // 显示统计信息
        System.out.println("\n--- 业绩统计 ---");
        double totalSales = performanceList.stream().mapToDouble(SalesPerformance::getTotalSalesAmount).sum();
        int totalOrders = performanceList.stream().mapToInt(SalesPerformance::getTotalOrders).sum();
        System.out.println("总订单数: " + totalOrders);
        System.out.println("总销售额: " + String.format("%.2f", totalSales));
        System.out.println("平均销售额: " + String.format("%.2f", totalSales / performanceList.size()));
    }

    /**
     * 显示畅销车型排行
     */
    private static void displayBestSellingModels(List<ModelSales> modelSalesList) {
        System.out.println("\n--- 畅销车型排行榜 (Top 5) ---");
        System.out.println(String.format("%-12s\t%-24s\t%-20s\t%-20s\t%-8s\t%-15s", 
                "排名", "品牌", "车系", "配置", "销量", "销售总额"));
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        for (ModelSales ms : modelSalesList) {
            System.out.println(String.format("%-12d\t%-24s\t%-20s\t%-20s\t%-8d\t%-15.2f", 
                    ms.getRank(), ms.getBrandName(), ms.getSeriesName(), ms.getConfigName(),
                    ms.getSalesCount(), ms.getTotalSalesAmount()));
        }
        
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.println("共显示 " + modelSalesList.size() + " 款车型的销售数据");
        
        // 显示统计信息
        System.out.println("\n--- 销售统计 ---");
        int totalSales = modelSalesList.stream().mapToInt(ModelSales::getSalesCount).sum();
        double totalAmount = modelSalesList.stream().mapToDouble(ModelSales::getTotalSalesAmount).sum();
        System.out.println("总销量: " + totalSales + " 辆");
        System.out.println("总销售额: " + String.format("%.2f", totalAmount));
        System.out.println("平均售价: " + String.format("%.2f", totalAmount / totalSales));
    }

    /**
     * 显示月度销售统计报表
     */
    private static void displayMonthlyReport(MonthlyReport report) {
        System.out.println("\n========== " + report.getYear() + "年" + report.getMonth() + "月 销售统计报表 ==========");
        
        // 总体统计
        System.out.println("\n--- 总体统计 ---");
        System.out.println("总订单数: " + report.getTotalOrders());
        System.out.println("已完成订单: " + report.getCompletedOrders());
        System.out.println("已锁定订单: " + report.getLockedOrders());
        System.out.println("已取消订单: " + report.getCancelledOrders());
        System.out.println("总销售额: " + String.format("%.2f", report.getTotalSalesAmount()));
        System.out.println("总定金: " + String.format("%.2f", report.getTotalDeposit()));
        System.out.println("平均订单金额: " + String.format("%.2f", report.getAvgOrderAmount()));
        
        // 销售顾问业绩
        if (report.getEmployeePerformance() != null && !report.getEmployeePerformance().isEmpty()) {
            System.out.println("\n--- 销售顾问业绩 ---");
            System.out.println(String.format("%-12s\t%-12s\t%-8s\t%-15s", "排名", "姓名", "订单数", "销售额"));
            System.out.println("--------------------------------------------------");
            
            for (SalesPerformance sp : report.getEmployeePerformance()) {
                System.out.println(String.format("%-12d\t%-12s\t%-8d\t%-15.2f", 
                        sp.getRank(), sp.getEmployeeName(), sp.getTotalOrders(), sp.getTotalSalesAmount()));
            }
        }
        
        // 车型销售情况
        if (report.getModelSales() != null && !report.getModelSales().isEmpty()) {
            System.out.println("\n--- 车型销售情况 ---");
            System.out.println(String.format("%-12s\t%-24s\t%-20s\t%-8s\t%-15s", "排名", "品牌", "车系", "销量", "销售额"));
            System.out.println("--------------------------------------------------");
            
            for (ModelSales ms : report.getModelSales()) {
                System.out.println(String.format("%-12d\t%-24s\t%-20s\t%-8d\t%-15.2f", 
                        ms.getRank(), ms.getBrandName(), ms.getSeriesName(), ms.getSalesCount(), ms.getTotalSalesAmount()));
            }
        }
        
        // 每日销售趋势
        if (report.getDailySales() != null && !report.getDailySales().isEmpty()) {
            System.out.println("\n--- 每日销售趋势 ---");
            System.out.println(String.format("%-6s\t%-8s\t%-12s", "日期", "订单数", "销售额"));
            System.out.println("----------------------------------");
            
            for (DailySales ds : report.getDailySales()) {
                System.out.println(String.format("%-6d\t%-8d\t%-12.2f", 
                        ds.getDay(), ds.getDailyOrders(), ds.getDailySales()));
            }
        }
        
        System.out.println("\n========== 报表生成完成 ==========");
    }
}
