package com.automobile;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import com.automobile.db.DBUtil;

public class App {
    public static void main(String[] args) {
        try {
            Connection conn = DBUtil.getConnection();

            Statement stmt = conn.createStatement();
            System.out.println("准备执行SQL...");

            ResultSet rs = stmt.executeQuery("SELECT 1");
            System.out.println("SQL执行完成");

            if (!rs.next()) {
                System.out.println("没有返回任何结果！");
            } else {
                do {
                    System.out.println("测试查询成功：" + rs.getInt(1));
                } while (rs.next());
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("连接失败！");
            e.printStackTrace();
        }
    }
}