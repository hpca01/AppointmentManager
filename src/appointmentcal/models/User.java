/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class User implements FromQuery<User>{
    
    final private static String INSERT_STATEMENT="insert into user (userName, password, active, createDate, createdBy, lastUpdateBy) values (?,?,?,?,?,?)";
    final private static String UPDATE_STATEMENT="update user set userName=?, password=?, active=?, lastUpdateBy=? where userId=?";
    final private static String DELETE_STATEMENT="delete from user where userId=?";
    final private static String REFRESH_CURRENT= "select * from user where userId=?";

    
    private int userId;
    private String userName;
    private String password;
    private int active;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdateBy;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
    
    public static ArrayList<User> getAllUsers(){
        ArrayList users = new ArrayList<User>();
        
        
        try (DataSource data = new DataSource();){
            Optional<ResultSet> results = data.getResults("Select * from user");
            if(results.isPresent()){
                ResultSet rs = results.get();
                while(rs.next()){
                    users.add(new User().from(rs));
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return users;
    }

    @Override
    public User from(ResultSet rs) {
        try {
            int userId = rs.getInt("userId");
            String userName = rs.getString("userName");
            String password = rs.getString("password");
            int active = rs.getInt("active");
            Timestamp createDate = rs.getTimestamp("createDate");
            String createdBy = rs.getString("createdBy");
            Timestamp lastUpdate = rs.getTimestamp("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            this.setUserId(userId);
            this.setUserName(userName);
            this.setPassword(password);
            this.setActive(active);
            this.setCreateDate(createDate);
            this.setCreatedBy(createdBy);
            this.setLastUpdate(lastUpdate);
            this.setLastUpdateBy(lastUpdateBy);
            
            return this;
            
        } catch (SQLException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this;
    }

    @Override
    public int create() {
        //insert into user (userName, password, active, createDate, createdBy, lastUpdateBy) values (?,?,?,?,?,?);
        if(this.getUserId() == 0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS);){
                    stmt.setString(1, this.getUserName());
                    stmt.setString(2, this.getPassword());
                    stmt.setInt(3, this.getActive());
                    stmt.setTimestamp(4, this.getCreateDate());
                    stmt.setString(5, this.getCreatedBy());
                    stmt.setString(6, this.getLastUpdateBy());
                    
                    int update = stmt.executeUpdate();
                    
                    try(ResultSet rs = stmt.getGeneratedKeys()){
                        if(rs.next()){
                            this.setUserId(rs.getInt(1));
                        }
                    }
                    return update;
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int update() {
        //update user set userName=?, password=?, active=?, lastUpdateBy=? where userId=?;
        if (this.getUserId() > 0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT);){
                    stmt.setString(1, this.getUserName());
                    stmt.setString(2, this.getPassword());
                    stmt.setInt(3, this.getActive());
                    stmt.setString(4, this.getLastUpdateBy());
                    stmt.setInt(5, this.getUserId());
                    
                    return stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int delete() {
        //delete from user where userId=?;
        if(this.getUserId() > 0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(DELETE_STATEMENT);){
                    stmt.setInt(1, this.getUserId());
                    return stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int refresh() {
        if(this.getUserId() > 0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(REFRESH_CURRENT);){
                    stmt.setInt(1, this.getUserId());
                    ResultSet result = stmt.executeQuery();
                    while(result.next()) this.from(result);
                    
                    return 1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
   
}
