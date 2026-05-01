// 库存查询和预警功能
package com.automobile.service;

import com.automobile.dao.CarDAO;
import com.automobile.model.Employee;
import java.util.List;
import java.util.Scanner;

public class InventoryQueryService {

    /**
     * 查询车辆库存的交互界面
     */
    public static void queryCarInventory(Scanner scanner, Employee employee) {
        System.out.println("\n========== 查询车辆库存 ==========");
        System.out.println("操作员: " + employee.getName() + " (" + employee.getRole() + " - " + employee.getDepartment() + ")");
        
        // 逐步引导用户输入筛选条件
        System.out.println("\n请输入筛选条件（直接回车跳过该条件）：");
        
        // 品牌筛选
        System.out.print("品牌名称: ");
        String brandName = scanner.nextLine().trim();
        if (brandName.isEmpty()) brandName = null;
        
        // 车系筛选
        System.out.print("车系名称: ");
        String seriesName = scanner.nextLine().trim();
        if (seriesName.isEmpty()) seriesName = null;
        
        // 颜色筛选
        System.out.print("颜色: ");
        String color = scanner.nextLine().trim();
        if (color.isEmpty()) color = null;
        
        // 状态筛选
        System.out.print("车辆状态 (在库/已锁定/已售出/在途): ");
        String status = scanner.nextLine().trim();
        if (status.isEmpty()) status = null;
        
        // 价格范围筛选
        Double minPrice = null;
        Double maxPrice = null;
        System.out.print("最低售价 (直接回车跳过): ");
        String minPriceStr = scanner.nextLine().trim();
        if (!minPriceStr.isEmpty()) {
            try {
                minPrice = Double.parseDouble(minPriceStr);
            } catch (NumberFormatException e) {
                System.out.println("最低售价格式错误，将忽略该条件");
            }
        }
        
        System.out.print("最高售价 (直接回车跳过): ");
        String maxPriceStr = scanner.nextLine().trim();
        if (!maxPriceStr.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (NumberFormatException e) {
                System.out.println("最高售价格式错误，将忽略该条件");
            }
        }
        
        // VIN码搜索
        System.out.print("VIN码 (支持模糊搜索): ");
        String vin = scanner.nextLine().trim();
        if (vin.isEmpty()) vin = null;
        
        // 确认查询
        System.out.println("\n========== 筛选条件确认 ==========");
        if (brandName != null) System.out.println("品牌: " + brandName);
        if (seriesName != null) System.out.println("车系: " + seriesName);
        if (color != null) System.out.println("颜色: " + color);
        if (status != null) System.out.println("状态: " + status);
        if (minPrice != null) System.out.println("最低售价: " + minPrice);
        if (maxPrice != null) System.out.println("最高售价: " + maxPrice);
        if (vin != null) System.out.println("VIN码: " + vin);
        if (brandName == null && seriesName == null && color == null && status == null && 
            minPrice == null && maxPrice == null && vin == null) {
            System.out.println("无筛选条件，将显示所有车辆");
        }
        System.out.println("===================================");
        
        System.out.print("\n确认查询？(y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("查询已取消");
            return;
        }
        
        // 执行查询
        System.out.println("\n正在查询车辆库存...");
        List<CarDAO.CarSearchResult> cars = CarDAO.searchCars(brandName, seriesName, color, status, minPrice, maxPrice, vin);
        
        // 显示结果
        if (cars.isEmpty()) {
            System.out.println("未找到符合条件的车辆");
        } else {
            displayCarSearchResults(cars);
        }
    }

    /**
     * 查看库存预警报表
     */
    public static void showInventoryAlert(Scanner scanner, Employee employee) {
        System.out.println("\n========== 库存预警报表 ==========");
        System.out.println("操作员: " + employee.getName() + " (" + employee.getRole() + " - " + employee.getDepartment() + ")");
        
        // 查询预警信息
        System.out.println("正在查询库存预警信息...");
        List<CarDAO.InventoryAlert> alerts = CarDAO.getInventoryAlert();
        
        // 显示结果
        if (alerts.isEmpty()) {
            System.out.println("\n✓ 所有车型库存充足，无预警信息");
        } else {
            displayInventoryAlerts(alerts);
        }
        
        System.out.println("\n报表生成完成");
    }

    /**
     * 显示车辆搜索结果
     */
    private static void displayCarSearchResults(List<CarDAO.CarSearchResult> cars) {
        System.out.println("\n--- 车辆库存查询结果 ---");
        System.out.println(String.format("%-18s\t%-12s\t%-20s\t%-20s\t%-10s\t%-12s\t%-12s\t%-10s\t%-12s", 
                "VIN码", "品牌", "车系", "配置", "颜色", "采购成本", "零售价", "状态", "入库日期"));
        System.out.println("--------------------------------------------------------------------------------------------------------");
        
        for (CarDAO.CarSearchResult car : cars) {
            System.out.println(String.format("%-18s\t%-12s\t%-20s\t%-20s\t%-10s\t%-12.2f\t%-12.2f\t%-10s\t%-12s", 
                    car.vin, car.brandName, car.seriesName, car.configName, 
                    car.color, car.purchasePrice, car.salePrice, car.status, car.stockInDate));
        }
        
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.println("共找到 " + cars.size() + " 辆车");
    }

    /**
     * 显示库存预警报表（基于Q7查询优化）
     */
    private static void displayInventoryAlerts(List<CarDAO.InventoryAlert> alerts) {
        System.out.println("\n--- 库存预警详情（基于Q7查询） ---");
        System.out.println(String.format("%-8s\t%-24s\t%-20s\t%-8s\t%-20s\t%-8s\t%-8s\t%-8s\t%-8s\t%-8s\t%-8s\t%-8s", 
                "车型ID", "品牌", "车系", "年款", "配置", "指导价", "安全库存", "在库", "已锁定", "在途", "总数", "缺货"));
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        
        for (CarDAO.InventoryAlert alert : alerts) {
            String status = alert.availableCount == 0 ? "⚠️ 严重" : "⚠️ 预警";
            System.out.println(String.format("%-8d\t%-24s\t%-20s\t%-8d\t%-20s\t%-8.2f\t%-8d\t%-8d\t%-8d\t%-8d\t%-8d\t%-8d %s", 
                    alert.modelId, alert.brandName, alert.seriesName, alert.year, alert.configName, 
                    alert.guidePrice, alert.safeStock, alert.availableCount, alert.lockedCount, alert.inTransitCount, 
                    alert.totalCount, alert.shortage, status));
        }
        
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        System.out.println("⚠️ 共有 " + alerts.size() + " 个车型需要补货（基于Q7查询结果）");
        
        // 显示统计信息
        System.out.println("\n--- 预警统计 ---");
        int severeAlerts = 0;
        int warningAlerts = 0;
        int totalShortage = 0;
        double totalGuidePrice = 0;
        
        for (CarDAO.InventoryAlert alert : alerts) {
            if (alert.availableCount == 0) {
                severeAlerts++;
            } else {
                warningAlerts++;
            }
            totalShortage += alert.shortage;
            totalGuidePrice += alert.guidePrice;
        }
        
        System.out.println("严重缺货（0辆）: " + severeAlerts + " 个车型");
        System.out.println("库存预警: " + warningAlerts + " 个车型");
        System.out.println("总缺货数量: " + totalShortage + " 辆");
        System.out.println("平均指导价: " + String.format("%.2f", totalGuidePrice / alerts.size()));
        
        // 业务建议
        System.out.println("\n--- 业务建议 ---");
        if (severeAlerts > 0) {
            System.out.println("🚨 紧急建议：立即采购 " + severeAlerts + " 个严重缺货车型的车辆");
        }
        if (warningAlerts > 0) {
            System.out.println("⚠️ 建议关注： " + warningAlerts + " 个车型库存偏低，建议适时补货");
        }
    }
}
