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
public class Appointment implements FromQuery<Appointment>, Comparable<Appointment> {

    final private static String INSERT_STATEMENT = "insert into appointment(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdateBy) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    final private static String UPDATE_STATEMENT = "update appointment set customerId=?, userId=?, title=?, description=?, location=?, contact=?, type=?, url=?, start=?, end=?, createDate=?, createdBy=?, lastUpdateBy=? where appointmentId=?";
    final private static String DELETE_STATEMENT = "delete from appointment where appointmentId=?";
    final private static String REFRESH_CURRENT= "select * from appointment where appointmentId=?";

    private int appointmentId;
    private int customerId;
    private int userId;
    private SimpleStringProperty title;
    private SimpleStringProperty description;
    private SimpleStringProperty location;
    private SimpleStringProperty contact;
    private SimpleStringProperty type;
    private SimpleStringProperty url;
    private Timestamp start;
    private Timestamp end;
    private Timestamp createDate;
    private SimpleStringProperty createdBy;
    private Timestamp lastUpdate;
    private SimpleStringProperty lastUpdateBy;

    public Appointment() {
        this.title = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.contact = new SimpleStringProperty();
        this.type = new SimpleStringProperty();
        this.url = new SimpleStringProperty();
        this.createdBy = new SimpleStringProperty();
        this.lastUpdateBy= new SimpleStringProperty();
    }
    
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getContact() {
        return contact.get();
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp startTime) {
        this.start = startTime;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp endTime) {
        this.end = endTime;
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
        return "Appointment{" + "appointmentId=" + appointmentId + ", customerId=" + customerId + ", userId=" + userId + ", title=" + title + ", description=" + description + ", location=" + location + ", contact=" + contact + ", type=" + type + ", url=" + url + ", start=" + start + ", end=" + end + ", createDate=" + createDate + ", createdBy=" + createdBy + ", lastUpdate=" + lastUpdate + ", lastUpdateBy=" + lastUpdateBy + '}';
    }

    public static ArrayList<Appointment> getAllAppointments() {
        ArrayList appointments = new ArrayList<Appointment>();

        try (DataSource data = new DataSource();) {
            Optional<ResultSet> results = data.getResults("Select * from appointment order by start");
            if (results.isPresent()) {
                ResultSet rs = results.get();
                while (rs.next()) {
                    appointments.add(new Appointment().from(rs));
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return appointments;
    }

    @Override
    public Appointment from(ResultSet rs) {
        try {
            int appointmentId = rs.getInt("appointmentId");
            int customerId = rs.getInt("customerId");
            int userId = rs.getInt("userId");
            String title = rs.getString("title");
            String description = rs.getString("description");
            String location = rs.getString("location");
            String contact = rs.getString("contact");
            String type = rs.getString("type");
            String url = rs.getString("url");
            Timestamp start = rs.getTimestamp("start");
            Timestamp end = rs.getTimestamp("end");
            Timestamp createDate = rs.getTimestamp("createDate");
            String createdBy = rs.getString("createdBy");
            Timestamp lastUpdate = rs.getTimestamp("lastUpdate");
            String lastUpdateBy = rs.getString("lastUpdateBy");

            this.setAppointmentId(appointmentId);
            this.setCustomerId(customerId);
            this.setUserId(userId);
            this.setTitle(title);
            this.setDescription(description);
            this.setLocation(location);
            this.setContact(contact);
            this.setType(type);
            this.setUrl(url);
            this.setStart(Utils.convertTimeStamp(start, ZoneId.systemDefault()));
            this.setEnd(Utils.convertTimeStamp(end, ZoneId.systemDefault()));
            this.setCreateDate(Utils.convertTimeStamp(createDate, ZoneId.systemDefault()));
            this.setCreatedBy(createdBy);
            this.setLastUpdate(Utils.convertTimeStamp(lastUpdate, ZoneId.systemDefault()));
            this.setLastUpdateBy(lastUpdateBy);

            return this;
        } catch (SQLException ex) {
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    @Override
    public int create() {
        //insert into appointment(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdateBy) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        if (this.getAppointmentId() == 0) {
            try (DataSource data = new DataSource();) {
                Connection conn = data.getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(INSERT_STATEMENT, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, this.getCustomerId());
                    stmt.setInt(2, this.getUserId());
                    stmt.setString(3, this.getTitle());
                    stmt.setString(4, this.getDescription());
                    stmt.setString(5, this.getLocation());
                    stmt.setString(6, this.getContact());
                    stmt.setString(7, this.getType());
                    stmt.setString(8, this.getUrl());
                    stmt.setTimestamp(9, this.getStart());
                    stmt.setTimestamp(10, this.getEnd());
                    stmt.setTimestamp(11, this.getCreateDate());
                    stmt.setString(12, this.getCreatedBy());
                    stmt.setString(13, this.getLastUpdateBy());

                    int update = stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        while (rs.next()) {
                            this.setAppointmentId(rs.getInt(1));
                        }
                    }
                    return update;
                }

            } catch (SQLException ex) {
                Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int update() {
        //update appointment set customerId=?, userId=?, title=?, description=?, location=?, contact=?, type=?, url=?, start=?, end=?, createDate=?, createdBy=?, lastUpdateBy=? where appointmentId=?
        if (this.getAppointmentId() > 0) {
            try (Connection conn = new DataSource().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(UPDATE_STATEMENT);) {
                stmt.setInt(1, this.getCustomerId());
                stmt.setInt(2, this.getUserId());
                stmt.setString(3, this.getTitle());
                stmt.setString(4, this.getDescription());
                stmt.setString(5, this.getLocation());
                stmt.setString(6, this.getContact());
                stmt.setString(7, this.getType());
                stmt.setString(8, this.getUrl());
                stmt.setTimestamp(9, this.getStart());
                stmt.setTimestamp(10, this.getEnd());
                stmt.setTimestamp(11, this.getCreateDate());
                stmt.setString(12, this.getCreatedBy());
                stmt.setString(13, this.getLastUpdateBy());

                stmt.setInt(14, this.getAppointmentId());

                return stmt.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int delete() {
        if (this.getAppointmentId() > 0) {
            try (Connection conn = new DataSource().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(DELETE_STATEMENT)) {
                stmt.setInt(1, this.getAppointmentId());
                return stmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int refresh() {
        if (this.getAppointmentId() > 0) {
            try (Connection conn = new DataSource().getConnection();
                    PreparedStatement stmt = conn.prepareStatement(REFRESH_CURRENT)) {
                stmt.setInt(1, this.getAppointmentId());
                ResultSet result = stmt.executeQuery();
                while(result.next()) this.from(result);
                
                return 1;
            } catch (SQLException ex) {
                Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Appointment o) {
        //helps sort appointment by timestamp
        return this.getStart().compareTo(o.getStart());
    }

}
