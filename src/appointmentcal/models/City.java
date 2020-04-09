/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class City implements FromQuery<City> {
    
    final private static String INSERT_STATEMENT="insert into city (city, countryId, createDate, createdBy, lastUpdateBy) values (?,?,?,?,?)";
    final private static String UPDATE_STATEMENT="update city set city=?, countryId=?, lastUpdatedBy=? where cityId=?";
    final private static String DELETE_STATEMENT="delete from city where cityId=?";
    final private static String REFRESH_CURRENT= "select * from city where cityId=?";

    private int cityId;
    private String city;
    private int countryId;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdateBy;

    @Override
    public String toString() {
        return "City{" + "cityId=" + cityId + ", city=" + city + ", countryId=" + countryId + ", createDate=" + createDate + ", createdBy=" + createdBy + ", lastUpdate=" + lastUpdate + ", lastUpdateBy=" + lastUpdateBy + '}';
    }

    
    public static ArrayList<City> getAllCities() {
        ArrayList cities = new ArrayList<City>();

        try (DataSource data = new DataSource();) {
            Optional<ResultSet> results = data.getResults("Select * from city");
            if (results.isPresent()) {
                ResultSet rs = results.get();
                while (rs.next()) {
                    cities.add(new City().from(rs));
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cities;
    }

    public City from(ResultSet rs) {
        try {
            int id = rs.getInt("cityId");
            String cityName = rs.getString("city");
            int countryId = rs.getInt("countryId");
            Timestamp createDate = rs.getTimestamp("createDate");
            String createBy = rs.getString("createdBy");
            Timestamp lastUpdate = rs.getTimestamp("lastUpdate");
            String lastUpdateby = rs.getString("lastUpdateBy");

            this.setCityId(id);
            this.setCity(cityName);
            this.setCountry(countryId);
            this.setCreateDate(createDate);
            this.setCreatedBy(createBy);
            this.setLastUpdate(lastUpdate);
            this.setLastUpdateBy(lastUpdateby);

            return this;
        } catch (SQLException ex) {
            Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
        }

        return this;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountry(int country) {
        this.countryId = country;
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
    public int create() {
        //insert into city (city, countryId, createDate, createdBy, lastUpdateBy) values (?,?,?,?,?);
        if (this.getCityId() == 0) {
            try(Connection conn =  new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS);){
                    stmt.setString(1, this.getCity());
                    stmt.setInt(2, this.getCountryId());
                    stmt.setTimestamp(3, this.getCreateDate());
                    stmt.setString(4, this.getCreatedBy());
                    stmt.setString(5, this.getLastUpdateBy());
                    
                    int update = stmt.executeUpdate();
                    try(ResultSet rs = stmt.getGeneratedKeys();){
                        if(rs.next()){
                            this.setCityId(rs.getInt(1));
                        }
                    }
                    return update;
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int update() {
        //update city set city=?, countryId=?, lastUpdatedBy=? where cityId=?;
        if (this.getCityId()>0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT)){
                    stmt.setString(1, this.getCity());
                    stmt.setInt(2, this.getCountryId());
                    stmt.setString(3, this.getLastUpdateBy());
                    stmt.setInt(4, this.getCityId());
                    
                    return stmt.executeUpdate();
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int delete() {
        if(this.getCityId()>0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(DELETE_STATEMENT);){
                    stmt.setInt(1, this.getCityId());
                    return stmt.executeUpdate();
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int refresh() {
        if(this.getCityId()>0){
            try(Connection conn = new DataSource().getConnection();){
                try(PreparedStatement stmt = conn.prepareStatement(REFRESH_CURRENT);){
                    stmt.setInt(1, this.getCityId());
                    ResultSet result = stmt.executeQuery();
                    while(result.next()) this.from(result);
                    
                    return 1;
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(City.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

}
