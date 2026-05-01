// 车辆入库功能
package com.automobile.service;

import com.automobile.dao.CarDAO;
import com.automobile.model.Employee;
import java.util.Scanner;

public class InventoryService {

    /**
     * 车辆入库的交互界面
     */
    public static void addCarToInventory(Scanner scanner, Employee employee) {
        System.out.println("\n========== 车辆入库 ==========");
        
        // 显示当前操作员
        System.out.println("操作员: " + employee.getName() + " (" + employee.getRole() + " - " + employee.getDepartment() + ")");
        
        // 1. 显示车型列表并选择车型
        CarDAO.listModels();
        System.out.print("\n请输入车型ID: ");
        int modelId;
        try {
            modelId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("错误：请输入有效的车型ID！");
            return;
        }
        
        String modelInfo = CarDAO.getModelInfo(modelId);
        if (modelInfo == null) {
            System.out.println("错误：车型ID不存在！");
            return;
        }
        System.out.println("已选择车型: " + modelInfo);
        
        // 2. 输入车辆基本信息
        System.out.println("\n请输入车辆信息：");
        
        // VIN码
        System.out.print("VIN码 (17位): ");
        String vin = scanner.nextLine().trim();
        if (vin.isEmpty()) {
            System.out.println("错误：VIN码不能为空！");
            return;
        }
        if (vin.length() != 17) {
            System.out.println("错误：VIN码必须为17位！");
            return;
        }
        if (CarDAO.checkVinExists(vin)) {
            System.out.println("错误：VIN码已存在！");
            return;
        }
        
        // 颜色
        System.out.print("颜色: ");
        String color = scanner.nextLine().trim();
        if (color.isEmpty()) {
            System.out.println("错误：颜色不能为空！");
            return;
        }
        
        // 发动机号
        System.out.print("发动机号: ");
        String engineNo = scanner.nextLine().trim();
        if (engineNo.isEmpty()) {
            System.out.println("错误：发动机号不能为空！");
            return;
        }
        if (CarDAO.checkEngineNoExists(engineNo)) {
            System.out.println("错误：发动机号已存在！");
            return;
        }
        
        // 生产日期
        System.out.print("生产日期 (格式: yyyy-MM-dd): ");
        String productionDate = scanner.nextLine().trim();
        if (productionDate.isEmpty()) {
            System.out.println("错误：生产日期不能为空！");
            return;
        }
        // 简单的日期格式验证
        if (!productionDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("错误：生产日期格式不正确，请使用 yyyy-MM-dd 格式！");
            return;
        }
        
        // 3. 输入价格信息
        System.out.println("\n请输入价格信息：");
        
        System.out.print("采购成本: ");
        double purchasePrice;
        try {
            purchasePrice = Double.parseDouble(scanner.nextLine().trim());
            if (purchasePrice < 0 || purchasePrice > 99999999.99) {
                System.out.println("错误：采购成本必须在 0 到 99,999,999.99 之间！");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("错误：请输入有效的采购成本！");
            return;
        }
        
        System.out.print("建议零售价: ");
        double salePrice;
        try {
            salePrice = Double.parseDouble(scanner.nextLine().trim());
            if (salePrice < 0 || salePrice > 99999999.99) {
                System.out.println("错误：建议零售价必须在 0 到 99,999,999.99 之间！");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("错误：请输入有效的建议零售价！");
            return;
        }
        
        // 4. 确认信息
        System.out.println("\n========== 入库信息确认 ==========");
        System.out.println("车型: " + modelInfo);
        System.out.println("VIN码: " + vin);
        System.out.println("颜色: " + color);
        System.out.println("发动机号: " + engineNo);
        System.out.println("生产日期: " + productionDate);
        System.out.println("采购成本: " + String.format("%.2f", purchasePrice));
        System.out.println("建议零售价: " + String.format("%.2f", salePrice));
        System.out.println("入库日期: 今天 (自动设置)");
        System.out.println("车辆状态: 在库 (自动设置)");
        System.out.println("===================================");
        
        System.out.print("\n确认入库？(y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("操作已取消");
            return;
        }
        
        // 5. 执行入库操作
        System.out.println("\n正在执行车辆入库...");
        boolean success = CarDAO.createCar(vin, modelId, color, engineNo, productionDate, purchasePrice, salePrice);
        
        // 6. 显示结果
        if (success) {
            System.out.println("\n========== 车辆入库成功 ==========");
            System.out.println("VIN码: " + vin);
            System.out.println("车型: " + modelInfo);
            System.out.println("颜色: " + color);
            System.out.println("状态: 在库");
            System.out.println("入库时间: 今天");
            System.out.println("===================================");
        } else {
            System.out.println("\n错误：车辆入库失败，请检查输入信息！");
        }
    }
}
