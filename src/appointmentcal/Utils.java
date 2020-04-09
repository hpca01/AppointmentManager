/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal;

import appointmentcal.models.FromQuery;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Optional;
import javafx.scene.control.ComboBox;

/**
 *
 * @author hp
 */
public class Utils {

    public static <T extends FromQuery> T getIfPresent(Optional<T> item) {
        //to make it easier to unwrap optionals..
        if (item.isPresent()) {
            return item.get();
        } else {
            return null;
        }
    }
    
    public static <T extends FromQuery> Optional<T> getComboSelection(ComboBox<T> box){
        T item = box.getSelectionModel().getSelectedItem();
        if (item!=null){
            return Optional.of(item);
        }
        else return Optional.empty();
    }
    
    public static LocalDateTime buildDateTime(LocalDate date, int hours, int minutes){
        return date.atStartOfDay().plusHours(hours).plusMinutes(minutes);
    }

    public static boolean isWithinBusinessHours(LocalDateTime startTime) {
        if ((startTime.toLocalTime().isAfter(LocalTime.of(9, 0))) && (startTime.toLocalTime().isBefore(LocalTime.of(17, 30)))) {
            return true;
        } else {
            return false;
        }
    }

    public static Timestamp convertTimeStamp(Timestamp time, ZoneId zone) {
        ZonedDateTime zdt = time.toLocalDateTime().atZone(ZoneId.of("UTC"));
        return time;
    }
    
    public static Timestamp convertTimeStamp(LocalDateTime time){
        ZonedDateTime zdt = time.atZone(ZoneId.systemDefault());
        return Timestamp.valueOf(time);
    }

    public static Timestamp currentDateTime() {
        return Timestamp.from(ZonedDateTime.now().toInstant());
    }
}
