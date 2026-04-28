package com.automobile.dao;

import com.automobile.db.DBUtil;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

public class SalesOrderDAO {

    /**
     * 调用存储过程创建销售订单
     * 
     * @param customerId 客户ID
     * @param employeeId 销售顾问ID
     * @param vin 车辆VIN码
     * @param carPrice 车辆价格
     * @param insuranceFee 保险费用
     * @param taxFee 购置税
     * @param serviceFee 服务费
     * @param otherFee 其他费用
     * @param deposit 定金
     * @return 包含订单ID、结果代码和结果消息的数组
     */
    public static int[] createSalesOrder(int customerId, int employeeId, String vin,
            double carPrice, double insuranceFee, double taxFee,
            double serviceFee, double otherFee, double deposit) {
        
        int[] result = new int[2]; // result[0] = order_id, result[1] = result_code
        String resultMsg = "";
        
        Connection conn = null;
        CallableStatement cstmt = null;
        
        try {
            conn = DBUtil.getConnection();
            
            // 调用存储过程 sp_create_sales_order
            cstmt = conn.prepareCall("{call sp_create_sales_order(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            
            // 设置输入参数（使用参数化查询防SQL注入）
            cstmt.setInt(1, customerId);
            cstmt.setInt(2, employeeId);
            cstmt.setString(3, vin);
            cstmt.setDouble(4, carPrice);
            cstmt.setDouble(5, insuranceFee);
            cstmt.setDouble(6, taxFee);
            cstmt.setDouble(7, serviceFee);
            cstmt.setDouble(8, otherFee);
            cstmt.setDouble(9, deposit);
            
            // 注册输出参数
            cstmt.registerOutParameter(10, Types.INTEGER); // p_order_id
            cstmt.registerOutParameter(11, Types.INTEGER); // p_result_code
            cstmt.registerOutParameter(12, Types.VARCHAR);  // p_result_msg
            
            // 执行存储过程
            cstmt.execute();
            
            // 获取输出参数
            result[0] = cstmt.getInt(10); // order_id
            result[1] = cstmt.getInt(11); // result_code
            resultMsg = cstmt.getString(12); // result_msg
            
            System.out.println("存储过程执行结果: " + resultMsg);
            
        } catch (Exception e) {
            System.out.println("创建订单失败！");
            e.printStackTrace();
            result[1] = -1; // 异常
        } finally {
            try {
                if (cstmt != null) cstmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
}
