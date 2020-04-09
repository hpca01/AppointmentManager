/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models;

import appointmentcal.Utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author hp
 */
public class Address implements FromQuery<Address> {

    final private static String INSERT_STATEMENT = "insert into address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?, ?, ?, ?, ?)";
    final private static String UPDATE_STATEMENT = "update address set address=?, address2=?, cityId=?, postalCode=?, phone=?, lastUpdateBy=? where addressId=?";
    final private static String DELETE_STATEMENT = "delete from address where addressId=?";
    final private static String REFRESH_CURRENT= "select * from address where addressId=?";

    private int addressId;
    private SimpleStringProperty address;
    private SimpleStringProperty address2;
    private int cityId;
    private SimpleStringProperty postalCode;
    private SimpleStringProperty phone;
    private Timestamp createDate;
    private SimpleStringProperty createdBy;
    private Timestamp lastUpdate;
    private SimpleStringProperty lastUpdateBy;

    public Address() {
        this.address = new SimpleStringProperty();
        this.address2 = new SimpleStringProperty();
        this.postalCode = new SimpleStringProperty();
        this.phone = new SimpleStringProperty();
        this.createDate = Utils.currentDateTime();
        this.createdBy = new SimpleStringProperty();
        this.lastUpdateBy = new SimpleStringProperty(); 
    }

    public static ArrayList<Address> getAllAddresses() {
        ArrayList addresses = new ArrayList<Address>();

        try (DataSource data = new DataSource();) {
            Optional<ResultSet> results = data.getResults("Select * from address");
            if (results.isPresent()) {
                ResultSet rs = results.get();
                while (rs.next()) {
                    addresses.add(new Address().from(rs));
                }

                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
        }
        return addresses;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
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

    @Override
    public String toString() {
        return "Address{" + "addressId=" + addressId + ", address=" + address + ", address2=" + address2 + ", cityId=" + cityId + ", postalCode=" + postalCode + ", phone=" + phone + ", createDate=" + createDate + ", createdBy=" + createdBy + ", lastUpdate=" + lastUpdate + ", lastUpdateBy=" + lastUpdateBy + '}';
    }

    @Override
    public Address from(ResultSet rs) {
        try {
            int id = rs.getInt("addressId");
            String address1 = rs.getString("address");
            String address2 = rs.getString("address2");
            int cityId = rs.getInt("cityId");
            String postalCode = rs.getString("postalCode");
            String phone = rs.getString("phone");
            Timestamp createDate = rs.getTimestamp("createDate");
            String createdBy = rs.getString("createdBy");
            Timestamp lastUpdate = rs.getTimestamp("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");

            this.setAddressId(id);
            this.setAddress(address1);
            this.setAddress2(address2);
            this.setCityId(cityId);
            this.setPostalCode(postalCode);
            this.setPhone(phone);
            this.setCreateDate(Utils.convertTimeStamp(createDate, ZoneId.systemDefault()));
            this.setCreatedBy(createdBy);
            this.setLastUpdate(Utils.convertTimeStamp(lastUpdate, ZoneId.systemDefault()));
            this.setLastUpdateBy(lastUpdateBy);

            return this;
        } catch (SQLException ex) {
            Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    @Override
    public int create() {
        if (this.getAddressId() == 0) {
            //"insert into address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?, ?, ?, ?, ?);";
            try (DataSource data = new DataSource();) {
                Connection conn = data.getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, this.getAddress());
                    stmt.setString(2, this.getAddress2());
                    stmt.setInt(3, this.getCityId());
                    stmt.setString(4, this.getPostalCode());
                    stmt.setString(5, this.getPhone());
                    stmt.setTimestamp(6, this.getCreateDate());
                    stmt.setString(7, this.getCreatedBy());
                    stmt.setString(8, this.getLastUpdateBy());

                    int update = stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.setAddressId(rs.getInt(1));
                        }
                    }
                    return update;
                }

            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
    
    public int update(){
        //update address set address=?, address2=?, cityId=?, postalCode=?, phone=?, lastUpdateBy=? where addressId=?
        if (this.getAddressId()>0){
            try (DataSource data = new DataSource();){
                Connection conn = data.getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT)){
                    stmt.setString(1, this.getAddress());
                    stmt.setString(2, this.getAddress2());
                    stmt.setInt(3, this.getCityId());
                    stmt.setString(4, this.getPostalCode());
                    stmt.setString(5, this.getPhone());
                    stmt.setString(6, this.getLastUpdateBy());
                    stmt.setInt(7, this.getAddressId());
                    
                    return stmt.executeUpdate();
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int delete() {
        if (this.getAddressId()>0){
            try(DataSource data = new DataSource();){
                Connection conn = data.getConnection();
                try(PreparedStatement stmt = conn.prepareStatement(DELETE_STATEMENT);){
                    stmt.setInt(1, this.getAddressId());
                    int update = stmt.executeUpdate();
                    return update;
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
    
    public int refresh(){
        if(this.getAddressId()>0){
            try(Connection conn = new DataSource().getConnection()){
                try(PreparedStatement stmt = conn.prepareStatement(REFRESH_CURRENT)){
                    stmt.setInt(1, this.getAddressId());
                    ResultSet result = stmt.executeQuery();
                    while(result.next()){
                        this.from(result);
                    }
                    return 1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(Address.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
}
