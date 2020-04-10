/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal;

import appointmentcal.models.Appointment;
import appointmentcal.models.FromQuery;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Optional;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
    
    public static boolean isOverLapping(LocalDateTime startCurrentAppt, LocalDateTime endCurrentAppt, ObservableList<Appointment> appointments){
        LocalDate todayDate = startCurrentAppt.toLocalDate();
        LocalDateTime startDateTime = LocalDateTime.of(todayDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(todayDate, LocalTime.MAX);
        //get appointments for today
        FilteredList<Appointment> todaysAppointments = appointments.filtered(appt->appt.getStart().toLocalDateTime().isAfter(startDateTime) && appt.getEnd().toLocalDateTime().isBefore(endDateTime));
        
        //first get all the appointments after the current start time and check to see if the current end time overlaps with any of their start time
        FilteredList<Appointment> appointmentsAfterStart = todaysAppointments.filtered(appt -> appt.getStart().toLocalDateTime().isAfter(startCurrentAppt));
        //check to see if all the appointments after the current start time have a start time before the current appointment's endtime if so return true
        FilteredList<Appointment> appointmentsOverlappingStart = appointmentsAfterStart.filtered(appt->appt.getStart().toLocalDateTime().isBefore(endCurrentAppt));
        if (appointmentsOverlappingStart.size() > 0) return true;
        
        //get all the appointments BEFORE the current end time and check to see if the current start time overlaps with any of their end time
        FilteredList<Appointment> appointmentsBeforeEnd = todaysAppointments.filtered(appt->appt.getEnd().toLocalDateTime().isBefore(endCurrentAppt));
        //check to see if all the appointments BEFORE the current end time have an end time after the current appointment's start time if so return true
        FilteredList<Appointment> appointmentsOverlappingEnd = appointmentsBeforeEnd.filtered(appt->appt.getEnd().toLocalDateTime().isAfter(startCurrentAppt));
        
        if (appointmentsOverlappingEnd.size()>0) return true;
        return false;
    }
    
    public static boolean isOverLapping(LocalDateTime startCurrentAppt, LocalDateTime endCurrentAppt, ObservableList<Appointment> appointments, Appointment currentAppointment){
        //remove current appointment from the equation first
        appointments.remove(currentAppointment);
        
        LocalDate todayDate = startCurrentAppt.toLocalDate();
        LocalDateTime startDateTime = LocalDateTime.of(todayDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(todayDate, LocalTime.MAX);
        //get appointments for today
        FilteredList<Appointment> todaysAppointments = appointments.filtered(appt->appt.getStart().toLocalDateTime().isAfter(startDateTime) && appt.getEnd().toLocalDateTime().isBefore(endDateTime));
        
        
        //first get all the appointments after the current start time and check to see if the current end time overlaps with any of their start time
        FilteredList<Appointment> appointmentsAfterStart = todaysAppointments.filtered(appt -> appt.getStart().toLocalDateTime().isAfter(startCurrentAppt));
        //check to see if all the appointments after the current start time have a start time before the current appointment's endtime if so return true
        FilteredList<Appointment> appointmentsOverlappingStart = appointmentsAfterStart.filtered(appt->appt.getStart().toLocalDateTime().isBefore(endCurrentAppt));
        if (appointmentsOverlappingStart.size() > 0){
            appointments.add(currentAppointment);
            return true;
        }
        //get all the appointments BEFORE the current end time and check to see if the current start time overlaps with any of their end time
        FilteredList<Appointment> appointmentsBeforeEnd = todaysAppointments.filtered(appt->appt.getEnd().toLocalDateTime().isBefore(endCurrentAppt));
        //check to see if all the appointments BEFORE the current end time have an end time before the current appointment's start time if so return true
        FilteredList<Appointment> appointmentsOverlappingEnd = appointmentsBeforeEnd.filtered(appt->appt.getEnd().toLocalDateTime().isAfter(startCurrentAppt));
        if (appointmentsOverlappingEnd.size()>0) {
            appointments.add(currentAppointment);
            return true;
        }
        //put it back in
        appointments.add(currentAppointment);
        return false;
    }
}
