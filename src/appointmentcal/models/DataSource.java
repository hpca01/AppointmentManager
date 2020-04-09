/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 *
 * @author hp
 */
public final class DataSource implements AutoCloseable{
//    private static String USER_NAME = "root";
//    private static String PASSWORD = "root";
//    private static String DB_CONN = "jdbc:mysql://127.0.0.1:3306/u06cuk?zeroDateTimeBehavior=convertToNull&useLegacyDatetimeCode=false";
    
    private static String USER_NAME = "U06cUK";
    private static String PASSWORD = "53688728734";
    private static String DB_CONN = "jdbc:mysql://3.227.166.251:3306/U06cUK?zeroDateTimeBehavior=convertToNull&useLegacyDatetimeCode=false";
//    
    
    Connection connection;
    Statement statement;
    public void createConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DB_CONN, USER_NAME, PASSWORD);
            System.out.println("Database connected");
        }catch (SQLException e){
            throw new IllegalStateException("Cannot connect to db \n" + e.toString());
        }catch (ClassNotFoundException e){
            throw new IllegalStateException("JDBC Class not found");
        }
    }    
    
    //main method for running queries
    public Optional<ResultSet> getResults(String query) throws SQLException{
        if (this.connection != null){
            statement = this.connection.createStatement();
            return Optional.of(statement.executeQuery(query));
        }
        return Optional.empty();
    }

    public DataSource() {
        this.createConnection();
    }
    
    public Connection getConnection(){
        return this.connection;
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null){
            System.out.println("Closing connection to db");
            this.connection.close();
        }
        if (this.statement != null){
            System.out.println("Closing statement");
            this.statement.close();
        }
    }
}
