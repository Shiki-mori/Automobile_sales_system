// 查询我的订单功能
package com.automobile.service;

import com.automobile.dao.SalesOrderDAO;
import com.automobile.model.Employee;
import java.util.List;
import java.util.Scanner;

public class OrderQueryService {

    /**
     * 查询我的订单的交互界面
     */
    public static void queryMyOrders(Scanner scanner, Employee employee) {
        System.out.println("\n========== 查询我的订单 ==========");
        
        // 使用当前登录员工作为销售顾问
        int employeeId = employee.getEmployeeId();
        String employeeName = employee.getName();
        System.out.println("销售顾问: " + employeeName + " (ID: " + employeeId + ")");
        
        // 1. 查询订单列表
        List<SalesOrderDAO.OrderInfo> orders = SalesOrderDAO.getOrdersByEmployee(employeeId);
        
        if (orders.isEmpty()) {
            System.out.println("\n暂无订单记录");
            return;
        }
        
        // 2. 显示订单概览
        displayOrderList(orders);
        
        // 3. 提供查看详情选项
        while (true) {
            System.out.println("\n请选择操作：");
            System.out.println("1. 查看订单详情");
            System.out.println("2. 返回上级菜单");
            System.out.print("请输入选择: ");
            
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("1")) {
                System.out.print("请输入订单ID: ");
                try {
                    int orderId = Integer.parseInt(scanner.nextLine().trim());
                    displayOrderDetails(orderId);
                } catch (NumberFormatException e) {
                    System.out.println("错误：请输入有效的订单ID！");
                }
            } else if (choice.equals("2")) {
                break;
            } else {
                System.out.println("无效选择，请重新输入");
            }
        }
    }

    /**
     * 显示订单列表
     */
    private static void displayOrderList(List<SalesOrderDAO.OrderInfo> orders) {
        System.out.println("\n--- 订单列表 ---");
        System.out.println(String.format("%-8s\t%-15s\t%-18s\t%-30s\t%-10s\t%-12s\t%-12s", 
                "订单ID", "客户姓名", "VIN码", "车型", "状态", "总金额", "创建时间"));
        System.out.println("----------------------------------------------------------------------------------------");
        
        for (SalesOrderDAO.OrderInfo order : orders) {
            String carInfo = order.seriesName + " " + order.configName + " (" + order.color + ")";
            System.out.println(String.format("%-8d\t%-15s\t%-18s\t%-30s\t%-10s\t%-12.2f\t%-12s", 
                    order.orderId, order.customerName, order.vin, carInfo, 
                    order.status, order.totalAmount, order.createTime));
        }
        
        System.out.println("\n共找到 " + orders.size() + " 条订单记录");
    }

    /**
     * 显示订单详情
     */
    private static void displayOrderDetails(int orderId) {
        // 1. 查询订单基本信息
        SalesOrderDAO.OrderInfo order = SalesOrderDAO.getOrderById(orderId);
        
        if (order == null) {
            System.out.println("错误：订单ID不存在！");
            return;
        }
        
        // 2. 显示订单基本信息
        System.out.println("\n========== 订单详情 ==========");
        System.out.println("订单ID: " + order.orderId);
        System.out.println("客户姓名: " + order.customerName);
        System.out.println("车辆信息: " + order.seriesName + " " + order.configName + " (" + order.color + ")");
        System.out.println("VIN码: " + order.vin);
        System.out.println("订单状态: " + order.status);
        System.out.println("订单总金额: " + String.format("%.2f", order.totalAmount));
        System.out.println("定金: " + String.format("%.2f", order.deposit));
        System.out.println("创建时间: " + order.createTime);
        if (!order.deliveryTime.isEmpty()) {
            System.out.println("交车时间: " + order.deliveryTime);
        }
        
        // 3. 查询并显示订单明细
        List<SalesOrderDAO.OrderItem> items = SalesOrderDAO.getOrderItems(orderId);
        
        if (!items.isEmpty()) {
            System.out.println("\n--- 订单明细 ---");
            System.out.println(String.format("%-8s\t%-15s\t%-20s\t%-12s", "明细ID", "类型", "描述", "金额"));
            System.out.println("--------------------------------------------------------");
            
            double itemsTotal = 0;
            for (SalesOrderDAO.OrderItem item : items) {
                System.out.println(String.format("%-8d\t%-15s\t%-20s\t%-12.2f", 
                        item.itemId, item.type, item.description, item.amount));
                itemsTotal += item.amount;
            }
            
            System.out.println("--------------------------------------------------------");
            System.out.println(String.format("明细合计: %.2f", itemsTotal));
        }
        
        System.out.println("===================================");
    }
}
