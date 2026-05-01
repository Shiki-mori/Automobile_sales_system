package com.automobile.model;

import java.util.List;

public class ReportDTO {
    
    // 销售业绩数据
    public static class SalesPerformance {
        private int employeeId;
        private String employeeName;
        private String jobNumber;
        private int totalOrders;
        private double totalSalesAmount;
        private double totalGrossProfit;
        private double avgOrderAmount;
        private String firstOrderDate;
        private String lastOrderDate;
        private int rank;
        
        public SalesPerformance() {}
        
        public SalesPerformance(int employeeId, String employeeName, String jobNumber, 
                              int totalOrders, double totalSalesAmount, double totalGrossProfit,
                              double avgOrderAmount, String firstOrderDate, String lastOrderDate) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.jobNumber = jobNumber;
            this.totalOrders = totalOrders;
            this.totalSalesAmount = totalSalesAmount;
            this.totalGrossProfit = totalGrossProfit;
            this.avgOrderAmount = avgOrderAmount;
            this.firstOrderDate = firstOrderDate;
            this.lastOrderDate = lastOrderDate;
        }
        
        // Getters and Setters
        public int getEmployeeId() { return employeeId; }
        public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
        
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        
        public String getJobNumber() { return jobNumber; }
        public void setJobNumber(String jobNumber) { this.jobNumber = jobNumber; }
        
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
        
        public double getTotalSalesAmount() { return totalSalesAmount; }
        public void setTotalSalesAmount(double totalSalesAmount) { this.totalSalesAmount = totalSalesAmount; }
        
        public double getTotalGrossProfit() { return totalGrossProfit; }
        public void setTotalGrossProfit(double totalGrossProfit) { this.totalGrossProfit = totalGrossProfit; }
        
        public double getAvgOrderAmount() { return avgOrderAmount; }
        public void setAvgOrderAmount(double avgOrderAmount) { this.avgOrderAmount = avgOrderAmount; }
        
        public String getFirstOrderDate() { return firstOrderDate; }
        public void setFirstOrderDate(String firstOrderDate) { this.firstOrderDate = firstOrderDate; }
        
        public String getLastOrderDate() { return lastOrderDate; }
        public void setLastOrderDate(String lastOrderDate) { this.lastOrderDate = lastOrderDate; }
        
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }
    
    // 车型销售数据
    public static class ModelSales {
        private int modelId;
        private String brandName;
        private String seriesName;
        private String configName;
        private int salesCount;
        private double totalSalesAmount;
        private int rank;
        
        public ModelSales() {}
        
        public ModelSales(int modelId, String brandName, String seriesName, String configName,
                         int salesCount, double totalSalesAmount) {
            this.modelId = modelId;
            this.brandName = brandName;
            this.seriesName = seriesName;
            this.configName = configName;
            this.salesCount = salesCount;
            this.totalSalesAmount = totalSalesAmount;
        }
        
        // Getters and Setters
        public int getModelId() { return modelId; }
        public void setModelId(int modelId) { this.modelId = modelId; }
        
        public String getBrandName() { return brandName; }
        public void setBrandName(String brandName) { this.brandName = brandName; }
        
        public String getSeriesName() { return seriesName; }
        public void setSeriesName(String seriesName) { this.seriesName = seriesName; }
        
        public String getConfigName() { return configName; }
        public void setConfigName(String configName) { this.configName = configName; }
        
        public int getSalesCount() { return salesCount; }
        public void setSalesCount(int salesCount) { this.salesCount = salesCount; }
        
        public double getTotalSalesAmount() { return totalSalesAmount; }
        public void setTotalSalesAmount(double totalSalesAmount) { this.totalSalesAmount = totalSalesAmount; }
        
        public int getRank() { return rank; }
        public void setRank(int rank) { this.rank = rank; }
    }
    
    // 月度报表数据
    public static class MonthlyReport {
        private int year;
        private int month;
        private int totalOrders;
        private int completedOrders;
        private int lockedOrders;
        private int cancelledOrders;
        private double totalSalesAmount;
        private double totalDeposit;
        private double avgOrderAmount;
        private List<SalesPerformance> employeePerformance;
        private List<ModelSales> modelSales;
        private List<DailySales> dailySales;
        
        public MonthlyReport() {}
        
        // Getters and Setters
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        
        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }
        
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
        
        public int getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(int completedOrders) { this.completedOrders = completedOrders; }
        
        public int getLockedOrders() { return lockedOrders; }
        public void setLockedOrders(int lockedOrders) { this.lockedOrders = lockedOrders; }
        
        public int getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(int cancelledOrders) { this.cancelledOrders = cancelledOrders; }
        
        public double getTotalSalesAmount() { return totalSalesAmount; }
        public void setTotalSalesAmount(double totalSalesAmount) { this.totalSalesAmount = totalSalesAmount; }
        
        public double getTotalDeposit() { return totalDeposit; }
        public void setTotalDeposit(double totalDeposit) { this.totalDeposit = totalDeposit; }
        
        public double getAvgOrderAmount() { return avgOrderAmount; }
        public void setAvgOrderAmount(double avgOrderAmount) { this.avgOrderAmount = avgOrderAmount; }
        
        public List<SalesPerformance> getEmployeePerformance() { return employeePerformance; }
        public void setEmployeePerformance(List<SalesPerformance> employeePerformance) { this.employeePerformance = employeePerformance; }
        
        public List<ModelSales> getModelSales() { return modelSales; }
        public void setModelSales(List<ModelSales> modelSales) { this.modelSales = modelSales; }
        
        public List<DailySales> getDailySales() { return dailySales; }
        public void setDailySales(List<DailySales> dailySales) { this.dailySales = dailySales; }
    }
    
    // 每日销售数据
    public static class DailySales {
        private int day;
        private int dailyOrders;
        private double dailySales;
        
        public DailySales() {}
        
        public DailySales(int day, int dailyOrders, double dailySales) {
            this.day = day;
            this.dailyOrders = dailyOrders;
            this.dailySales = dailySales;
        }
        
        // Getters and Setters
        public int getDay() { return day; }
        public void setDay(int day) { this.day = day; }
        
        public int getDailyOrders() { return dailyOrders; }
        public void setDailyOrders(int dailyOrders) { this.dailyOrders = dailyOrders; }
        
        public double getDailySales() { return dailySales; }
        public void setDailySales(double dailySales) { this.dailySales = dailySales; }
    }
}
