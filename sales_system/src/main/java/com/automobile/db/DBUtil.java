package com.automobile.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.InputStream;

public class DBUtil {

    public static Connection getConnection() throws Exception {

        // 1. 加载配置文件（从 resources 读取）
        Properties props = new Properties();
        InputStream is = DBUtil.class
                .getClassLoader()
                .getResourceAsStream("db.properties");

        if (is == null) {
            throw new RuntimeException("找不到 db.properties 文件");
        }

        props.load(is);

        // 2. 读取配置
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // 3. 建立连接
        return DriverManager.getConnection(url, user, password);
    }
}