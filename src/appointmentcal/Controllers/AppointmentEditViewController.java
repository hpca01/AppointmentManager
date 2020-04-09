/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.Controllers;

import appointmentcal.AppointmentManager;
import appointmentcal.Utils;
import appointmentcal.models.Appointment;
import appointmentcal.models.Customer;
import appointmentcal.models.DataSource;
import appointmentcal.views.ViewFactory;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author hp
 */
public class AppointmentEditViewController extends BaseController implements Initializable {

    private final String SCREEN_NAME = "Appointment Edit";

    @FXML
    private TextField title;
    
    @FXML
    private TextField urlField;

    @FXML
    private TextField location;

    @FXML
    private TextArea description;

    @FXML
    private TextField contact;

    @FXML
    private TextField type;

    @FXML
    private ComboBox<Customer> customerCombo;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private Slider startMinutes;

    @FXML
    private Slider startHours;

    @FXML
    private Label startHoursLabel;

    @FXML
    private Label startMinutesLabel;

    @FXML
    private Slider endHours;

    @FXML
    private Slider endMinutes;

    @FXML
    private Label endHoursLabel;

    @FXML
    private Label endMinutesLabel;

    public AppointmentEditViewController(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        super(fxmlStringName, viewFactory, manager);
    }

    @FXML
    void delete(ActionEvent event) {
        boolean proceed = viewFactory.raiseWarningAlertProceed("Appointment", "Delete Appointment Alert", "Are you sure you want to delete the appointment for " + manager.getCurrentAppointment().getTitle());
        if (proceed) {
            manager.getCurrentAppointment().delete();
            manager.getAppointments().remove(manager.getCurrentAppointment());
            manager.refreshAppointments();
            
            viewFactory.showAppointmentView();
            viewFactory.closeStage((Stage)customerCombo.getScene().getWindow());
        }
    }

    @FXML
    void save(ActionEvent event) {
        Appointment currentAppointment = manager.getCurrentAppointment();
        if (validateForm()){
            String apptTitle = title.getText().trim();
            String apptLocation = location.getText().trim();
            String apptDescription = description.getText();
            String apptContact = contact.getText();
            String apptType = type.getText();
            Customer apptCustomer = Utils.getIfPresent(Utils.getComboSelection(customerCombo));
            LocalDate apptStartDate = startDate.getValue();
            LocalDate apptEndDate = endDate.getValue();
            int apptStartMinutes = startMinutes.valueProperty().intValue();
            int apptStartHours = startHours.valueProperty().intValue();
            int apptEndHours = endHours.valueProperty().intValue();
            int apptEndMinutes = endMinutes.valueProperty().intValue();
            
            LocalDateTime startTime = Utils.buildDateTime(startDate.getValue(), startHours.valueProperty().intValue(), startMinutes.valueProperty().intValue());
            LocalDateTime endTime = Utils.buildDateTime(endDate.getValue(), endHours.valueProperty().intValue(), endMinutes.valueProperty().intValue());
            
            if(Utils.isWithinBusinessHours(startTime)){
                //within business hours
                currentAppointment.setTitle(apptTitle);
                currentAppointment.setLocation(apptLocation);
                currentAppointment.setDescription(apptDescription);
                currentAppointment.setContact(apptContact);
                currentAppointment.setType(apptType);
                currentAppointment.setCustomerId(apptCustomer.getCustomerId());

                currentAppointment.setUrl(urlField.getText());
                currentAppointment.setStart(Utils.convertTimeStamp(startTime));
                currentAppointment.setEnd(Utils.convertTimeStamp(endTime));
                currentAppointment.setLastUpdateBy(manager.getCurrentUser().getUserName());
                
                currentAppointment.update();
                viewFactory.showAppointmentView();
                viewFactory.closeStage((Stage)customerCombo.getScene().getWindow());
            }else{
                viewFactory.raiseInfoAlert("Appointment Validation", "Business Hours", "Please schedule appointment start time within business hours only(9 AM - 530 PM)");
            }
        }

    }

    @FXML
    void saveNew(ActionEvent event) {
        if(validateForm()){
            //save new
            Appointment newAppointment = new Appointment();
            newAppointment.setTitle(title.getText().trim());
            newAppointment.setLocation(location.getText().trim());
            newAppointment.setDescription(description.getText().trim());
            newAppointment.setContact(contact.getText().trim());
            newAppointment.setType(type.getText().trim());
            newAppointment.setCustomerId(Utils.getIfPresent(Utils.getComboSelection(customerCombo)).getCustomerId());
            newAppointment.setUserId(manager.getCurrentUser().getUserId());
            
            LocalDateTime startTime = Utils.buildDateTime(startDate.getValue(), startHours.valueProperty().intValue(), startMinutes.valueProperty().intValue());
            LocalDateTime endTime = Utils.buildDateTime(endDate.getValue(), endHours.valueProperty().intValue(), endMinutes.valueProperty().intValue());
            
            newAppointment.setUrl(urlField.getText().trim());
            newAppointment.setCreateDate(Utils.currentDateTime());
            newAppointment.setCreatedBy(manager.getCurrentUser().getUserName());
            newAppointment.setLastUpdateBy(manager.getCurrentUser().getUserName());
            if(Utils.isWithinBusinessHours(startTime)){
                // within business hours;
                newAppointment.setStart(Utils.convertTimeStamp(startTime));
                newAppointment.setCreateDate(Utils.currentDateTime());
                newAppointment.setEnd(Utils.convertTimeStamp(endTime));
                
                newAppointment.create();
                manager.getAppointments().add(newAppointment);
                viewFactory.showAppointmentView();
                viewFactory.closeStage((Stage)customerCombo.getScene().getWindow());
            }else{
                //not in business hours;
                viewFactory.raiseInfoAlert("Appointment Validation", "Business Hours", "Please schedule appointment start time within business hours only(9 AM - 530 PM)");
            }
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupForm();
        setupCustomerCombo();
        setupDateTime();
        bindSliders();
    }

    void setupForm() {
        Appointment currentAppointment = manager.getCurrentAppointment();
        title.setText(currentAppointment.getTitle());
        location.setText(currentAppointment.getLocation());
        description.setText(currentAppointment.getDescription());
        contact.setText(currentAppointment.getContact());
        type.setText(currentAppointment.getType());
        urlField.setText(currentAppointment.getUrl());
    }

    void setupCustomerCombo() {
        Appointment currentAppointment = manager.getCurrentAppointment();
        customerCombo.setConverter(new StringConverter<Customer>() {
            @Override
            public String toString(Customer object) {
                return object.getCustomerName();
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });
        customerCombo.setItems(manager.getCustomers());
        Optional<Customer> currentAppointmentCustomer = manager.getCustomers().filtered(cust -> cust.getCustomerId() == currentAppointment.getCustomerId()).stream().findFirst();
        if (currentAppointmentCustomer.isPresent()) {
            customerCombo.getSelectionModel().select(currentAppointmentCustomer.get());
        }
    }

    void setupDateTime() {
        Appointment currentAppointment = manager.getCurrentAppointment();
        startHoursLabel.textProperty().set(String.valueOf(Utils.convertTimeStamp(currentAppointment.getStart(), ZoneId.systemDefault()).toLocalDateTime().getHour()));
        startMinutesLabel.textProperty().set(String.valueOf(Utils.convertTimeStamp(currentAppointment.getStart(), ZoneId.systemDefault()).toLocalDateTime().getMinute()));
        endHoursLabel.textProperty().set(String.valueOf(Utils.convertTimeStamp(currentAppointment.getEnd(), ZoneId.systemDefault()).toLocalDateTime().getHour()));
        endMinutesLabel.textProperty().set(String.valueOf(Utils.convertTimeStamp(currentAppointment.getEnd(), ZoneId.systemDefault()).toLocalDateTime().getMinute()));

        startDate.setValue(Utils.convertTimeStamp(currentAppointment.getStart(), ZoneId.systemDefault()).toLocalDateTime().toLocalDate());
        endDate.setValue(Utils.convertTimeStamp(currentAppointment.getEnd(), ZoneId.systemDefault()).toLocalDateTime().toLocalDate());

        startMinutes.setValue(Utils.convertTimeStamp(currentAppointment.getStart(), ZoneId.systemDefault()).toLocalDateTime().getMinute());
        startHours.setValue(Utils.convertTimeStamp(currentAppointment.getStart(), ZoneId.systemDefault()).toLocalDateTime().getHour());

        endMinutes.setValue(Utils.convertTimeStamp(currentAppointment.getEnd(), ZoneId.systemDefault()).toLocalDateTime().getMinute());
        endHours.setValue(Utils.convertTimeStamp(currentAppointment.getEnd(), ZoneId.systemDefault()).toLocalDateTime().getHour());
    }

    void bindSliders() {
        startMinutes.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            startMinutesLabel.textProperty().set(String.valueOf(newValue.intValue()));
        });
        startHours.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            startHoursLabel.textProperty().set(String.valueOf(newValue.intValue()));
        });
        endMinutes.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            endMinutesLabel.textProperty().set(String.valueOf(newValue.intValue()));
        });
        endHours.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            endHoursLabel.textProperty().set(String.valueOf(newValue.intValue()));
        });
    }

    @Override
    public String getScreenName() {
        return SCREEN_NAME;
    }
    
    boolean validateForm(){
        String apptTitle = title.getText().trim();
        String apptLocation = location.getText().trim();
        String apptDescription = description.getText();
        String apptContact = contact.getText();
        String apptType = type.getText();
        Customer apptCustomer = Utils.getIfPresent(Utils.getComboSelection(customerCombo));
        LocalDate apptStartDate = startDate.getValue();
        LocalDate apptEndDate = endDate.getValue();
        int apptStartMinutes = startMinutes.valueProperty().intValue();
        int apptStartHours = startHours.valueProperty().intValue();
        int apptEndHours = endHours.valueProperty().intValue();
        int apptEndMinutes = endMinutes.valueProperty().intValue();
        
        if ((apptTitle.isEmpty())
             || (apptLocation.isEmpty()) || (apptStartDate == null) || (apptEndDate == null) || (apptType.isEmpty()) || (apptCustomer == null)   
                ){
            viewFactory.raiseInfoAlert("Appointment Validation", "Required Fields", "Please make sure to fill in the required fields");
            return false;
        }
        return true;
    }

}
