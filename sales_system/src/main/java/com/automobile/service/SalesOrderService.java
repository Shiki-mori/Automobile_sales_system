package com.automobile.service;

import com.automobile.dao.CarDAO;
import com.automobile.dao.CustomerDAO;
import com.automobile.dao.EmployeeDAO;
import com.automobile.dao.SalesOrderDAO;
import java.util.Scanner;

public class SalesOrderService {

    private static Scanner scanner = new Scanner(System.in);

    /**
     * 创建销售订单的交互界面
     */
    public static void createSalesOrder() {
        System.out.println("\n========== 创建销售订单 ==========");
        
        // 1. 显示客户列表并选择客户
        CustomerDAO.listAllCustomers();
        System.out.print("\n请输入客户ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符
        
        String customerName = CustomerDAO.getCustomerName(customerId);
        if (customerName == null) {
            System.out.println("错误：客户ID不存在！");
            return;
        }
        System.out.println("已选择客户: " + customerName);
        
        // 2. 显示销售顾问列表并选择销售顾问
        EmployeeDAO.listSalesEmployees();
        System.out.print("\n请输入销售顾问ID: ");
        int employeeId = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符
        
        String employeeName = EmployeeDAO.getEmployeeName(employeeId);
        if (employeeName == null) {
            System.out.println("错误：员工ID不存在！");
            return;
        }
        System.out.println("已选择销售顾问: " + employeeName);
        
        // 3. 显示在库车辆列表并选择车辆
        CarDAO.listAvailableCars();
        System.out.print("\n请输入车辆VIN码: ");
        String vin = scanner.nextLine().trim();
        
        String carInfo = CarDAO.getCarInfo(vin);
        if (carInfo == null) {
            System.out.println("错误：车辆VIN码不存在或车辆不在库！");
            return;
        }
        System.out.println("已选择车辆: " + carInfo);
        
        // 4. 输入费用信息
        System.out.println("\n请输入费用信息：");
        System.out.print("车辆价格: ");
        double carPrice = scanner.nextDouble();
        if (carPrice < 0 || carPrice > 99999999.99) {
            System.out.println("错误：车辆价格必须在 0 到 99,999,999.99 之间！");
            return;
        }
        
        System.out.print("保险费用: ");
        double insuranceFee = scanner.nextDouble();
        if (insuranceFee < 0 || insuranceFee > 99999999.99) {
            System.out.println("错误：保险费用必须在 0 到 99,999,999.99 之间！");
            return;
        }
        
        System.out.print("购置税: ");
        double taxFee = scanner.nextDouble();
        if (taxFee < 0 || taxFee > 99999999.99) {
            System.out.println("错误：购置税必须在 0 到 99,999,999.99 之间！");
            return;
        }
        
        System.out.print("服务费: ");
        double serviceFee = scanner.nextDouble();
        if (serviceFee < 0 || serviceFee > 99999999.99) {
            System.out.println("错误：服务费必须在 0 到 99,999,999.99 之间！");
            return;
        }
        
        System.out.print("其他费用: ");
        double otherFee = scanner.nextDouble();
        if (otherFee < 0 || otherFee > 99999999.99) {
            System.out.println("错误：其他费用必须在 0 到 99,999,999.99 之间！");
            return;
        }
        
        System.out.print("定金: ");
        double deposit = scanner.nextDouble();
        if (deposit < 0 || deposit > 99999999.99) {
            System.out.println("错误：定金必须在 0 到 99,999,999.99 之间！");
            return;
        }
        scanner.nextLine(); // 消耗换行符
        
        // 5. 调用DAO创建订单
        System.out.println("\n正在创建订单...");
        int[] result = SalesOrderDAO.createSalesOrder(
            customerId, employeeId, vin,
            carPrice, insuranceFee, taxFee,
            serviceFee, otherFee, deposit
        );
        
        // 6. 显示结果
        int orderId = result[0];
        int resultCode = result[1];
        
        if (resultCode == 0) {
            System.out.println("\n========== 订单创建成功 ==========");
            System.out.println("订单ID: " + orderId);
            System.out.println("客户: " + customerName);
            System.out.println("销售顾问: " + employeeName);
            System.out.println("车辆: " + carInfo);
            double totalAmount = carPrice + insuranceFee + taxFee + serviceFee + otherFee;
            System.out.println("订单总金额: " + totalAmount);
            System.out.println("定金: " + deposit);
            System.out.println("===================================");
        } else if (resultCode == -2) {
            System.out.println("\n错误：车辆状态异常，无法创建订单！");
        } else {
            System.out.println("\n错误：订单创建失败，请检查输入信息！");
        }
    }
}
