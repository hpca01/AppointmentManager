/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author hp
 */
public class Customer implements FromQuery<Customer>{
    
    final private static String INSERT_STATEMENT="insert into customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?, ?, ?)";
    final private static String UPDATE_STATEMENT="update customer set customerName=?, addressId=?, active=?, lastUpdateBy=? where customerId=?";
    final private static String DELETE_STATEMENT="delete from customer where customerId=?";
    final private static String REFRESH_CURRENT= "select * from customer where customerId=?";
    
    private int customerId;
    private SimpleStringProperty customerName;
    private int addressId;
    private SimpleIntegerProperty active;
    private Timestamp createDate;
    private SimpleStringProperty createdBy;
    private Timestamp lastUpdate;
    private SimpleStringProperty lastUpdateBy;

    public Customer() {
        this.customerName = new SimpleStringProperty();
        this.active = new SimpleIntegerProperty();
        this.createdBy = new SimpleStringProperty();
        this.lastUpdateBy = new SimpleStringProperty();
    }

    @Override
    public String toString() {
        return "Customer{" + "customerId=" + customerId + ", customerName=" + customerName + ", addressId=" + addressId + ", active=" + active + ", createDate=" + createDate + ", createdBy=" + createdBy + ", lastUpdate=" + lastUpdate + ", lastUpdateBy=" + lastUpdateBy + '}';
    }
    
    
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName.get();
    }
    
    public SimpleStringProperty getCustomerNameProperty(){
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active.get();
    }

    public void setActive(int active) {
        this.active.set(active);
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy.get();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy.set(createdBy);
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy.get();
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy.set(lastUpdateBy);
    }
    
    public static ArrayList<Customer> getAllCustomers(){
        ArrayList customers = new ArrayList<Customer>();
        
        
        try (DataSource data = new DataSource();){
            Optional<ResultSet> results = data.getResults("Select * from customer");
            if (results.isPresent()){
                ResultSet rs = results.get();
                while(rs.next()){
                    customers.add(new Customer().from(rs));
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        return customers;
    }
    

    @Override
    public Customer from(ResultSet rs) {
        try {
            int customerId = rs.getInt("customerId");
            String customerName = rs.getString("customerName");
            int addressId = rs.getInt("addressId");
            int active = rs.getInt("active");
            Timestamp createDate = rs.getTimestamp("createDate");
            String createdBy = rs.getString("createdBy");
            Timestamp lastUpdate = rs.getTimestamp("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            this.setCustomerId(customerId);
            this.setCustomerName(customerName);
            this.setAddressId(addressId);
            this.setActive(active);
            this.setCreateDate(createDate);
            this.setCreatedBy(createdBy);
            this.setLastUpdate(lastUpdate);
            this.setLastUpdateBy(lastUpdateBy);
            
            return this;
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    @Override
    public int create() {
        //insert into customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?, ?, ?)
        if(this.getCustomerId() == 0){
            try(Connection conn = new DataSource().getConnection()){
                try(PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS)){
                    stmt.setString(1, this.getCustomerName());
                    stmt.setInt(2, this.getAddressId());
                    stmt.setInt(3, this.getActive());
                    stmt.setTimestamp(4, this.getCreateDate());
                    stmt.setString(5, this.getCreatedBy());
                    stmt.setString(6, this.getLastUpdateBy());
                    
                    int update = stmt.executeUpdate();
                    
                    try(ResultSet rs = stmt.getGeneratedKeys();){
                        if(rs.next()){
                            this.setCustomerId(rs.getInt(1));
                        }
                    }
                    return update;
                    
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int update() {
        //update customer set customerName=?, addressId=?, active=?, lastUpdateBy=? where customerId=?
        if (this.getCustomerId()>0){
            try(Connection conn = new DataSource().getConnection()){
                try(PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT)){
                    stmt.setString(1, this.getCustomerName());
                    stmt.setInt(2, this.getAddressId());
                    stmt.setInt(3, this.getActive());
                    stmt.setString(4, this.getLastUpdateBy());
                    stmt.setInt(5, this.getCustomerId());
                    
                    return stmt.executeUpdate();
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int delete() {
        //delete from customer where customerId=?
        if (this.getCustomerId()>0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(DELETE_STATEMENT);){
                    stmt.setInt(1, this.getCustomerId());
                    return stmt.executeUpdate();
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
    
    public int refresh(){
        if(this.getCustomerId() >0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(REFRESH_CURRENT);){
                    stmt.setInt(1, this.getCustomerId());
                    ResultSet result = stmt.executeQuery();
                    while(result.next()){
                        this.from(result);
                    }
                    return 1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
    
}
