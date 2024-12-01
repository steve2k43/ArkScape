package net.nocturne.utils.sql;

import net.nocturne.utils.sql.Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class LastInsertId {
    public static void main(String[] args) {
        System.out.println("Last inserted Id Example...");
        Connection con = null;
        Statement statement = null;
        ResultSet rs = null;
        String url = "jdbc:mysql://mysql.arctik.co.uk:3306/";
        String dbName = "bugs";
        String driverName = "com.mysql.jdbc.Driver";
        String userName = "ArkScape";
        String password = "ArkScape";
        try {
            // Connecting to the database
            Class.forName(driverName);
            con = DriverManager.getConnection(url + dbName, userName, password);
            try {
                // Creating object of Statement
                statement = con.createStatement();

                // Finding id of last inserted record
                String sql = "SELECT MAX(roll_no) from student";
                rs = statement.executeQuery(sql);
                while (rs.next()) {
                    System.out.println("Id of Last inserted student record : "
                            + rs.getInt(1));
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}