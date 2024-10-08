package com.kris.docker_spring_boot.db.pg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class PostgreSqlJdbcConnCreateTable {

    public static void main(String[] args) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db_person", "postgres", "123456");
            System.out.println("连接数据库成功！");
            stmt = c.createStatement();
            String sql = "CREATE TABLE COMPANY02 " + "(ID INT PRIMARY KEY     NOT NULL,"
                    + " NAME           TEXT    NOT NULL, " + " AGE            INT     NOT NULL, "
                    + " ADDRESS        CHAR(50), " + " SALARY         REAL)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("新表创建成功！");
    }
}
