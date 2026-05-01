// 创建意向客户功能
package com.automobile.service;

import com.automobile.dao.CarDAO;
import com.automobile.dao.CustomerDAO;
import com.automobile.dao.IntentionDAO;
import com.automobile.db.DBUtil;
import com.automobile.model.Employee;
import java.sql.Connection;
import java.util.Scanner;

public class IntentionService {

    /**
     * 创建意向客户的交互界面
     */
    public static void createIntentionCustomer(Scanner scanner, Employee employee) {
        System.out.println("\n========== 创建意向客户 ==========");
        
        // 使用当前登录员工作为跟进顾问
        int employeeId = employee.getEmployeeId();
        String employeeName = employee.getName();
        System.out.println("跟进顾问: " + employeeName + " (ID: " + employeeId + ")");
        
        // 1. 输入客户信息
        System.out.println("\n请输入客户信息：");
        System.out.print("姓名: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("错误：姓名不能为空！");
            return;
        }
        
        System.out.print("性别: ");
        String gender = scanner.nextLine().trim();
        
        System.out.print("手机号: ");
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) {
            System.out.println("错误：手机号不能为空！");
            return;
        }
        
        System.out.print("身份证号: ");
        String idCard = scanner.nextLine().trim();
        
        System.out.print("地址: ");
        String address = scanner.nextLine().trim();
        
        // 2. 检查客户是否已存在
        Integer existingCustomerId = CustomerDAO.getCustomerIdByPhone(phone);
        int customerId;
        
        if (existingCustomerId != null) {
            customerId = existingCustomerId;
            String customerName = CustomerDAO.getCustomerName(customerId);
            System.out.println("\n客户已存在: " + customerName + " (ID: " + customerId + ")");
            System.out.print("是否继续为该客户创建意向？(y/n): ");
            String confirm = scanner.nextLine().trim();
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("操作已取消");
                return;
            }
        } else {
            // 3. 创建新客户
            System.out.println("\n正在创建新客户...");
            customerId = CustomerDAO.createCustomer(name, gender, phone, idCard, address);
            if (customerId == -1) {
                System.out.println("错误：客户创建失败！");
                return;
            }
            System.out.println("客户创建成功，客户ID: " + customerId);
        }
        
        // 4. 显示车型列表并选择意向车型
        CarDAO.listModels();
        System.out.print("\n请输入意向车型ID: ");
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
        
        // 5. 输入意向信息
        System.out.println("\n请输入意向信息：");
        System.out.print("意向级别 (如: 高意向/中意向/低意向): ");
        String level = scanner.nextLine().trim();
        
        System.out.print("备注: ");
        String note = scanner.nextLine().trim();
        
        System.out.print("下次联系时间 (格式: yyyy-MM-dd HH:mm:ss，留空则不设置): ");
        String nextContactTime = scanner.nextLine().trim();
        if (nextContactTime.isEmpty()) {
            nextContactTime = null;
        }
        
        // 6. 使用事务创建意向
        System.out.println("\n正在创建意向...");
        boolean success = createIntentionWithTransaction(customerId, modelId, level, note, employeeId, nextContactTime);
        
        // 7. 显示结果
        if (success) {
            System.out.println("\n========== 意向创建成功 ==========");
            System.out.println("客户ID: " + customerId);
            System.out.println("客户姓名: " + name);
            System.out.println("跟进顾问: " + employeeName);
            System.out.println("意向车型: " + modelInfo);
            System.out.println("意向级别: " + level);
            if (note != null && !note.isEmpty()) {
                System.out.println("备注: " + note);
            }
            if (nextContactTime != null) {
                System.out.println("下次联系时间: " + nextContactTime);
            }
            System.out.println("===================================");
        } else {
            System.out.println("\n错误：意向创建失败，请检查输入信息！");
        }
    }

    /**
     * 使用事务创建意向记录
     */
    private static boolean createIntentionWithTransaction(int customerId, int modelId, String level, 
                                                          String note, int followEmployeeId, String nextContactTime) {
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 开启事务
            
            // 插入意向表
            boolean intentionCreated = IntentionDAO.createIntention(conn, customerId, modelId, level, note, followEmployeeId, nextContactTime);
            
            if (intentionCreated) {
                conn.commit(); // 提交事务
                success = true;
            } else {
                conn.rollback(); // 回滚事务
            }
            
        } catch (Exception e) {
            System.out.println("创建意向失败！");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // 回滚事务
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return success;
    }
}
