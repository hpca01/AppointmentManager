/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models;

import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class Country implements FromQuery<Country>{
    
    final private static String INSERT_STATEMENT="insert into country (country, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?)";
    final private static String UPDATE_STATEMENT="update country set country=?, lastUpdatedBy=? where countryId=?";
    final private static String DELETE_STATEMENT="delete from country where countryId=?";
    final private static String REFRESH_CURRENT= "select * from country where countryId=?";

    
    private int countryId;
    private String country;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdateBy;
    
    
    
    public static ArrayList<Country> getAllCountries(){
        ArrayList countries = new ArrayList<Country>();
        
                
        try (DataSource data = new DataSource();) {
            Optional<ResultSet> returns = data.getResults("Select * from country");
            
            if (returns.isPresent()){
                ResultSet rs = returns.get();
                while(rs.next()){
                    countries.add(new Country().from(rs));
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Country.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return countries;
    }
    
        
    public Country from(ResultSet rs){
        
        Country country;
        country = this;
        
        try {
            int id = rs.getInt("countryId");
            String countryName = rs.getString("country");
            Timestamp createDate = rs.getTimestamp("createDate");
            String createdBy = rs.getString("createdBy");
            Timestamp lastUpdate = rs.getTimestamp("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");
            
            
            country.setCountryId(id);
            country.setCountry(countryName);
            country.setCreateDate(createDate);
            country.setCreatedBy(createdBy);
            country.setLastUpdate(lastUpdate);
            country.setLastUpdateBy(lastUpdateBy);
            
            return country;
        } catch (SQLException ex) {
            Logger.getLogger(Country.class.getName()).log(Level.SEVERE, null, ex);
        }
        return country;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    @Override
    public String toString() {
        return "Country{" + "countryId=" + countryId + ", country=" + country + ", createDate=" + createDate + ", createdBy=" + createdBy + ", lastUpdate=" + lastUpdate + ", lastUpdateBy=" + lastUpdateBy + '}';
    }
    
    

    @Override
    public int create() {
        //insert into country (country, createDate, createdBy, lastUpdateBy) values (?, ?, ?, ?);
        if(this.getCountryId() == 0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS);){
                    stmt.setString(1, this.getCountry());
                    stmt.setTimestamp(2, this.getCreateDate());
                    stmt.setString(3, this.getCreatedBy());
                    stmt.setString(4, this.getLastUpdateBy());
                    
                    int update = stmt.executeUpdate();
                    try(ResultSet rs = stmt.getGeneratedKeys();){
                        if (rs.next()){
                            this.setCountryId(rs.getInt(1));
                        }
                    }
                    return update;
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Country.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int update() {
        //update country set country=?, lastUpdatedBy=? where countryId=?;
        if(this.getCountryId()>0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT)){
                    stmt.setString(1, this.getCountry());
                    stmt.setString(2, this.getLastUpdateBy());
                    stmt.setInt(3, this.getCountryId());
                    
                    return stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Country.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int delete() {
        //delete from country where countryId=?;
        if(this.getCountryId()>0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(DELETE_STATEMENT)){
                    stmt.setInt(1, this.getCountryId());
                    return stmt.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Country.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int refresh() {
        if(this.getCountryId()>0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(REFRESH_CURRENT)){
                    stmt.setInt(1, this.getCountryId());
                    ResultSet result = stmt.executeQuery();
                    while(result.next()) this.from(result);
                    
                    return 1;
                }
            } catch (SQLException ex) {
                Logger.getLogger(Country.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }
    
    
    
}
