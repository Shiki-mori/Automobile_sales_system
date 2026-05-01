package com.automobile;

import com.automobile.dao.EmployeeDAO;
import com.automobile.model.Employee;
import com.automobile.service.InventoryQueryService;
import com.automobile.service.InventoryService;
import com.automobile.service.IntentionService;
import com.automobile.service.OrderQueryService;
import com.automobile.service.SalesOrderService;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("\n========== 汽车销售系统 ==========");
        
        Scanner scanner = new Scanner(System.in);
        
        // 打印所有员工列表
        EmployeeDAO.listAllEmployees();
        
        // 要求用户输入工号
        System.out.print("\n请输入您的工号: ");
        String jobNumber = scanner.nextLine().trim();
        
        // 根据工号查询员工信息
        Employee employee = EmployeeDAO.getEmployeeByJobNumber(jobNumber);
        
        if (employee == null) {
            System.out.println("工号不存在，登录失败！");
            System.out.println("\n程序结束");
            scanner.close();
            cleanupMySQL();
            System.exit(0);
        }
        
        System.out.println("\n登录成功!");
        System.out.println("欢迎, " + employee.getName() + " (" + employee.getRole() + " - " + employee.getDepartment() + ")");
        
        // 根据职责进入不同菜单
        String role = employee.getRole();
        if (role.equals("销售")) {
            showSalesMenu(scanner, employee);
        } else if (role.equals("售后")) {
            showAfterSalesMenu(scanner, employee);
        } else if (role.equals("经理")) {
            showManagerMenu(scanner, employee);
        } else {
            System.out.println("未知职位，无法进入系统");
        }
        
        System.out.println("\n程序结束");
        scanner.close();
        cleanupMySQL();
        System.exit(0);
    }
    
    /**
     * 清理MySQL连接清理线程
     */
    private static void cleanupMySQL() {
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            // 忽略清理异常
        }
    }
    
    /**
     * 销售菜单 - 销售前台
     */
    private static void showSalesMenu(Scanner scanner, Employee employee) {
        while (true) {
            System.out.println("\n========== 销售前台 ==========");
            System.out.println("1. 创建意向客户");
            System.out.println("2. 创建销售订单");
            System.out.println("3. 查询我的订单");
            System.out.println("4. 退出登录");
            System.out.print("请选择操作: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    IntentionService.createIntentionCustomer(scanner, employee);
                    break;
                case "2":
                    SalesOrderService.createSalesOrder(scanner, employee);
                    break;
                case "3":
                    OrderQueryService.queryMyOrders(scanner, employee);
                    break;
                case "4":
                    System.out.println("退出登录");
                    return;
                default:
                    System.out.println("无效选择，请重新输入");
            }
        }
    }
    
    /**
     * 售后菜单 - 任务书中未定义售后功能
     */
    private static void showAfterSalesMenu(Scanner scanner, Employee employee) {
        System.out.println("\n========== 售后功能 ==========");
        System.out.println("无操作权限");
        System.out.println("退出登录");
    }
    
    /**
     * 经理菜单 - 库存管理 + 报表中心
     */
    private static void showManagerMenu(Scanner scanner, Employee employee) {
        while (true) {
            System.out.println("\n========== 经理菜单 ==========");
            System.out.println("--- 库存管理 ---");
            System.out.println("1. 车辆入库");
            System.out.println("2. 查询车辆库存");
            System.out.println("3. 查看库存预警报表");
            System.out.println("--- 报表中心 ---");
            System.out.println("4. 查询销售业绩榜");
            System.out.println("5. 查询畅销车型排行");
            System.out.println("6. 生成月度销售统计");
            System.out.println("7. 退出登录");
            System.out.print("请选择操作: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    InventoryService.addCarToInventory(scanner, employee);
                    break;
                case "2":
                    InventoryQueryService.queryCarInventory(scanner, employee);
                    break;
                case "3":
                    InventoryQueryService.showInventoryAlert(scanner, employee);
                    break;
                case "4":
                    System.out.println("功能: 查询销售业绩榜（待实现, 对应需求Q2）");
                    break;
                case "5":
                    System.out.println("功能: 查询畅销车型排行（待实现, 对应需求Q3）");
                    break;
                case "6":
                    System.out.println("功能: 生成月度销售统计（待实现, 需调用存储过程sp_get_monthly_report）");
                    break;
                case "7":
                    System.out.println("退出登录");
                    return;
                default:
                    System.out.println("无效选择，请重新输入");
            }
        }
    }
}