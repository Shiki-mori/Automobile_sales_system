package com.automobile;

import com.automobile.service.SalesOrderService;

public class App {
    public static void main(String[] args) {
        System.out.println("========== 汽车销售系统 ==========");
        
        // 调用创建销售订单功能
        SalesOrderService.createSalesOrder();
        
        System.out.println("\n程序结束，按任意键退出...");
    }
}