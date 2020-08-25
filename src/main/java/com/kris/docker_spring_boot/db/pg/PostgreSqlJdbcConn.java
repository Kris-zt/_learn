package com.kris.docker_spring_boot.db.pg;

import java.sql.Connection;
import java.sql.DriverManager;

public class PostgreSqlJdbcConn {

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db_person", "postgres", "123456");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
}
