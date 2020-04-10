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
import appointmentcal.views.ViewFactory;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 *
 * @author hp
 */
public class AppointmentNewView extends BaseController implements Initializable{
    
    private final String SCREEN_NAME="New Appointment";
    
    @FXML
    private TextField urlField;
    
    @FXML
    private Button saveBtn;

    @FXML
    private Button saveNewBtn;

    @FXML
    private Button deleteBtn;
    
    @FXML
    private TextField title;

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

    public AppointmentNewView(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        super(fxmlStringName, viewFactory, manager);
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
        String url = urlField.getText();
        int apptStartMinutes = startMinutes.valueProperty().intValue();
        int apptStartHours = startHours.valueProperty().intValue();
        int apptEndHours = endHours.valueProperty().intValue();
        int apptEndMinutes = endMinutes.valueProperty().intValue();
        
        if ((apptTitle.isEmpty())
             || (apptLocation.isEmpty()) || (apptStartDate == null) || (apptEndDate == null) || (apptType.isEmpty()) || (apptCustomer == null) || (url == null)  
                ){
            viewFactory.raiseInfoAlert("Appointment Validation", "Required Fields", "Please make sure to fill in the required fields");
            return false;
        }
        return true;
    }
    
    @FXML
    void delete(ActionEvent event) {
        //not needed
    }

    @FXML
    void save(ActionEvent event) {
        //not needed

    }

    @FXML
    void saveNew(ActionEvent event) {
        if(validateForm()&&newNotOverlapping()){
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
            
            newAppointment.setUrl(urlField.getText());
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
    boolean newNotOverlapping(){
        LocalDateTime startTime = Utils.buildDateTime(startDate.getValue(), startHours.valueProperty().intValue(), startMinutes.valueProperty().intValue());
        LocalDateTime endTime = Utils.buildDateTime(endDate.getValue(), endHours.valueProperty().intValue(), endMinutes.valueProperty().intValue());
        if (Utils.isOverLapping(startTime, endTime, manager.getAppointments())){
            viewFactory.raiseInfoAlert("Appointment Validation", "Overlapping Appointments", "Please ensure that you do not overlap start/end times with other existing appointments");
            return false;
        }
        return true;
    }
    
    @Override
    public String getScreenName() {
        return SCREEN_NAME;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableButtons();
        setupCustomerCombo();
        bindSliders();
    }
    
    void setupCustomerCombo(){
        customerCombo.setConverter(new StringConverter<Customer>(){
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
        
    }
    
    void bindSliders(){
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

    private void disableButtons() {
        saveBtn.disableProperty().set(true);
        deleteBtn.disableProperty().set(true);
    }
}
