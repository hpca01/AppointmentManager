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
import appointmentcal.models.FromQuery;
import appointmentcal.models.reporting.AppointmentReport;
import appointmentcal.models.reporting.ReportWriter;
import appointmentcal.views.ViewFactory;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author hp
 */
public class AppointmentViewController extends BaseController implements Initializable {

    enum ViewType {
        WEEK,
        MONTH
    }

    private ViewType view = ViewType.MONTH;
    //utc date/time
    private LocalDateTime cal = LocalDateTime.now(ZoneOffset.UTC).with(LocalTime.MIN);

    private MenuItem editAppointment = new MenuItem("Edit Appointment");
    private MenuItem deleteAppointment = new MenuItem("Delete Appointment");

    private final String SCREEN_NAME = "Appointments View";

    FilteredList<Appointment> appointments = null;
    
     @FXML
    private MenuItem logFileMenuItem;

    @FXML
    private MenuItem allCustomerAppt;

    @FXML
    private MenuItem reportingApptByMonth;

    @FXML
    private MenuItem reportingSchedule;

    @FXML
    private MenuItem customersView;

    @FXML
    private MenuItem helpAbout;

    @FXML
    private Label currentDate;

    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableColumn<Appointment, String> eventColumn;

    @FXML
    private TableColumn<Appointment, String> typeColumn;

    @FXML
    private TableColumn<Appointment, String> startColumn;

    @FXML
    private TableColumn<Appointment, String> endColumn;

    @FXML
    private RadioButton week;

    @FXML
    private RadioButton month;
    
     @FXML
    private TextField searchBox;
     
    SimpleStringProperty search = new SimpleStringProperty();
    

    @FXML
    void addAppointment(ActionEvent event) {
        viewFactory.showNewAppointmentView();
        viewFactory.closeStage((Stage) appointmentTable.getScene().getWindow());
    }

    @FXML
    void leftButton(ActionEvent event) {
        switch (view) {
            case WEEK:
                cal = cal.minusWeeks(1);
                manager.setCurrentDate(cal.toLocalDate());
                break;
            case MONTH:
                cal = cal.minusMonths(1);
                manager.setCurrentDate(cal.toLocalDate());
                break;
        }
        setDateText();
        addAppointmentsData();
    }

    @FXML
    void rightButton(ActionEvent event) {
        switch (view) {
            case WEEK:
                cal = cal.plusWeeks(1);
                manager.setCurrentDate(cal.toLocalDate());
                break;
            case MONTH:
                cal = cal.plusMonths(1);
                manager.setCurrentDate(cal.toLocalDate());
                break;
        }
        setDateText();
        addAppointmentsData();
    }

    private void setDateText() {
        String dateText;
        switch (view) {
            case WEEK:
                dateText = "Week " + cal.get(WeekFields.ISO.weekOfYear()) + " of year " + cal.getYear();
                currentDate.setText(dateText);
                break;
            case MONTH:
                dateText = "" + cal.getMonth() + " " + cal.getYear();
                currentDate.setText(dateText);
                break;
        }
    }

    Timestamp convertFromUTCtoLocal(Timestamp time) {
        return Timestamp.from(time.toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toInstant());
    }

    void setupUpAppointmentTable() {
        eventColumn.setCellValueFactory(cellData -> {
            Appointment currAppt = cellData.getValue();
            Customer associatedCustomer = Utils.getIfPresent(manager.getCustomers().stream().filter(cust -> cust.getCustomerId() == currAppt.getCustomerId()).findFirst());
            String eventString = associatedCustomer.getCustomerName() + " - " + currAppt.getTitle() + " " + currAppt.getDescription();
            return new SimpleStringProperty(eventString);
        });

        typeColumn.setCellValueFactory(cellData -> {
            Appointment currAppt = cellData.getValue();
            String typeString = currAppt.getType();
            return new SimpleStringProperty(typeString);
        });
        startColumn.setCellValueFactory(cellData -> {
            Appointment currAppt = cellData.getValue();
            String start = new SimpleDateFormat("MM-dd-YYYY HH:mm:ss").format(Utils.convertTimeStamp(currAppt.getStart(), ZoneId.systemDefault()));
            return new SimpleStringProperty(start);
        });
        endColumn.setCellValueFactory(cellData -> {
            Appointment currAppt = cellData.getValue();
            String end = new SimpleDateFormat("MM-dd-YYYY HH:mm:ss").format(Utils.convertTimeStamp(currAppt.getEnd(), ZoneId.systemDefault()));
            return new SimpleStringProperty(end);
        });
        appointmentTable.setOnMouseClicked(event -> {
            Appointment appointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (appointment != null) {
                manager.setCurrentAppointment(appointment);
            }
        });
        addAppointmentsData();
    }

    void addAppointmentsData() {
        if (appointments == null){
            appointments = new FilteredList<>(manager.getAppointments(), s->true);
            appointmentTable.setItems(appointments);
        }
        switch (view) {
            case WEEK:
                //need to get current first day of week and end of week
                LocalDateTime beginning = cal.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
                LocalDateTime endOfWeek = cal.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).with(LocalTime.MAX);
                appointments.setPredicate(appointment -> (appointment.getStart().after(Timestamp.valueOf(beginning)) && (appointment.getEnd().before(Timestamp.valueOf(endOfWeek)))));
                break;
            case MONTH:
                LocalDateTime beginningOfMonth = cal.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
                LocalDateTime endOfMonth = cal.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
                appointments.setPredicate(appointment -> (appointment.getStart().after(Timestamp.valueOf(beginningOfMonth))) && (appointment.getEnd().before(Timestamp.valueOf(endOfMonth))));
                break;
        }
    }

    void setupContextMenu() {
        appointmentTable.setContextMenu(new ContextMenu(editAppointment, deleteAppointment));
        editAppointment.setOnAction(event -> {
            //open edit view
            viewFactory.showAppointmentEdit();
            viewFactory.closeStage((Stage) appointmentTable.getScene().getWindow());
        });
        deleteAppointment.setOnAction(event -> {
            boolean proceed = viewFactory.raiseWarningAlertProceed("Appointment", "Delete Appointment Alert", "Are you sure you want to delete the appointment for " + manager.getCurrentAppointment().getTitle());
            if (proceed) {
                manager.getCurrentAppointment().delete();
                manager.getAppointments().remove(manager.getCurrentAppointment());
                addAppointmentsData();
            }
        });
    }

    void setupToggleButtons() {
        ToggleGroup wkmthBtnGroup = new ToggleGroup();
        week.setToggleGroup(wkmthBtnGroup);
        month.setToggleGroup(wkmthBtnGroup);

        wkmthBtnGroup.selectToggle(month);
        view = ViewType.MONTH;

        wkmthBtnGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (newValue == week) {
                view = ViewType.WEEK;
                setDateText();
            } else {
                view = ViewType.MONTH;
                setDateText();
            }
            addAppointmentsData();
        });
    }

    public AppointmentViewController(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        super(fxmlStringName, viewFactory, manager);
        if (manager.getCurrentDate() != null) {
            this.cal = manager.getCurrentDate().atStartOfDay();
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchBox.textProperty().bindBidirectional(search);
        setupTitleMenu();
        setDateText();
        setupUpAppointmentTable();
        setupToggleButtons();
        setupContextMenu();

    }
    
    @FXML
    void search(Event event) {
        if(!search.getValueSafe().isEmpty()){
            System.out.println("search property "+search.getValue());
            Predicate<Appointment> title = s -> s.getTitle().toUpperCase().contains(search.getValue().toUpperCase());
            Predicate<Appointment> type = s -> s.getType().toUpperCase().contains(search.getValue().toUpperCase());
            appointments.setPredicate(title.or(type));
            appointmentTable.getItems().forEach(each-> System.out.println("appt "+each.toString()));
        }else{
            appointments.setPredicate(s->true);
            appointmentTable.getItems().forEach(each-> System.out.println("appt "+each.toString()));

        }
    }
    
    private void setupTitleMenu(){
        setupHelpMenuItem();
        setupReporting();
        setupLoggingMenuItem();
        setUpCustomerMenuItem();
    }
    
    private void setupHelpMenuItem(){
        helpAbout.setOnAction(event->{
            viewFactory.raiseInfoAlert("Info", "About", "An appointment management application implementation.");
        });
    }
    
    private void setupReporting(){
        allCustomerAppt.setOnAction(event->{
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files", "((*.csv)");
            fileChooser.getExtensionFilters().add(extFilter);
            
            File file = fileChooser.showSaveDialog(appointmentTable.getScene().getWindow());
            if (file!=null){
                ReportWriter rpt = new AppointmentReport("SELECT * FROM customers_appointments_rpt");
                List<String> csv = rpt.getQuery();
                rpt.saveTo(file);
            }
        });
        reportingApptByMonth.setOnAction(event->{
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files", "((*.csv)");
            fileChooser.getExtensionFilters().add(extFilter);
            
            File file = fileChooser.showSaveDialog(appointmentTable.getScene().getWindow());
            if (file!=null){
                ReportWriter rpt = new AppointmentReport("SELECT * FROM appointment_type_count");
                List<String> csv = rpt.getQuery();
                rpt.saveTo(file);
            }
        });
        reportingSchedule.setOnAction(event->{
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files", "((*.csv)");
            fileChooser.getExtensionFilters().add(extFilter);
            
            File file = fileChooser.showSaveDialog(appointmentTable.getScene().getWindow());
            if (file!=null){
                ReportWriter rpt = new AppointmentReport("SELECT * FROM user_schedule");
                List<String> csv = rpt.getQuery();
                rpt.saveTo(file);
            }
        });
    }
    
    private void setupLoggingMenuItem(){
        logFileMenuItem.setOnAction(event->{
            try {
                String userDir = System.getProperty("user.home");
                File fp = new File(userDir+File.separator+"appointmentLog.log");
                Desktop.getDesktop().open(fp);
            } catch (IOException ex) {
                Logger.getLogger(CustomerViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void setUpCustomerMenuItem(){
        customersView.setOnAction(event->{
            viewFactory.showCustomers();
            viewFactory.closeStage((Stage)appointmentTable.getScene().getWindow());            
        });
    }

    @Override
    public String getScreenName() {
        return SCREEN_NAME;
    }

}
