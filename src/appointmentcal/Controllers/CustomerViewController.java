/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.Controllers;

import appointmentcal.AppointmentManager;
import appointmentcal.Utils;
import appointmentcal.models.Address;
import appointmentcal.models.Appointment;
import appointmentcal.models.City;
import appointmentcal.models.Country;
import appointmentcal.models.Customer;
import appointmentcal.models.reporting.AppointmentReport;
import appointmentcal.models.reporting.ReportWriter;
import appointmentcal.views.ViewFactory;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author hp
 */
public class CustomerViewController extends BaseController implements Initializable {
    
    private MenuItem editCustomer = new MenuItem("Edit Customer");
    private MenuItem editAddress = new MenuItem("Edit Address");
    
    private final String SCREEN_NAME = "Customers";
    
     @FXML
    private MenuItem logFileMenuItem; //todo - need to add an actions

    @FXML
    private MenuItem allCustomerAppt; //todo - need to add a customer view

    @FXML
    private MenuItem reportingApptByMonth;//todo - need to add reporting view to text file

    @FXML
    private MenuItem reportingSchedule;//todo - need to add reporting view to text file

    @FXML
    private MenuItem appointmentsView;

    @FXML
    private MenuItem helpAbout;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, String> customerName;

    @FXML
    private TableColumn<Customer, String> customerAddress;

    @FXML
    private TableColumn<Customer, String> customerCity;

    @FXML
    private TableColumn<Customer, String> customerCountry;

    public CustomerViewController(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        super(fxmlStringName, viewFactory, manager);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableView();
        setupContextMenu();
        setupCustomerSelection();
        setupAppointmentMenuItems();
        setupHelpMenuItem();
        setupLoggingMenuItem();
        setupReporting();
        checkAppointments();
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
    
    private void checkAppointments(){
        ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        //lambda - to check if any appointment start times are within 15 minutes.
        Stream<Appointment> filter = manager.getAppointments().stream().filter(appt->appt.getStart().toLocalDateTime().atZone(ZoneId.systemDefault()).isBefore(now.plusMinutes(15)));
        if (filter.findFirst().isPresent()){
            viewFactory.raiseInfoAlert("Appointment Alert", "Appointment within 15 minutes", "There is an appointment scheduled that will occur within 15 minutes");
        }
    }
    
    private void setupReporting(){
        allCustomerAppt.setOnAction(event->{
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV Files", "((*.csv)");
            fileChooser.getExtensionFilters().add(extFilter);
            
            File file = fileChooser.showSaveDialog(customerTable.getScene().getWindow());
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
            
            File file = fileChooser.showSaveDialog(customerTable.getScene().getWindow());
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
            
            File file = fileChooser.showSaveDialog(customerTable.getScene().getWindow());
            if (file!=null){
                ReportWriter rpt = new AppointmentReport("SELECT * FROM user_schedule");
                List<String> csv = rpt.getQuery();
                rpt.saveTo(file);
            }
        });
    }
    
    private void setupAppointmentMenuItems(){
        appointmentsView.setOnAction(event->{
            viewFactory.showAppointmentView();
            viewFactory.closeStage((Stage)customerTable.getScene().getWindow());
        });
    }
    
    private void setupHelpMenuItem(){
        helpAbout.setOnAction(event->{
            viewFactory.raiseInfoAlert("Info", "About", "An appointment management application implementation.");
        });
    }
    
    private void setupContextMenu(){
        customerTable.setContextMenu(new ContextMenu(editAddress, editCustomer));
        editAddress.setOnAction(event->{
            FilteredList<Address> addresses = manager.getAddresses().filtered(address->address.getAddressId() == manager.getCurrentCustomer().getAddressId());
            manager.setCurrentAddress(addresses.get(0));
            viewFactory.showAddressEdit();
            viewFactory.closeStage((Stage)customerTable.getScene().getWindow());
        });
        
        editCustomer.setOnAction(event->{
            viewFactory.showCustomerEdit();
            viewFactory.closeStage((Stage)customerTable.getScene().getWindow());
        });
    }
    
    private void setupCustomerSelection(){
        customerTable.setOnMouseClicked(event->{
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null){
                manager.setCurrentCustomer(selectedCustomer);
            }
        });
    }
    
    private void setupTableView() {
        customerName.setCellValueFactory(customer -> customer.getValue().getCustomerNameProperty());
        customerAddress.setCellValueFactory(customer->{
            int addressId = ((Customer)customer.getValue()).getAddressId();
            FilteredList<Address> filtered = manager.getAddresses().filtered(address->address.getAddressId() == addressId);
            if(!filtered.isEmpty()){
                Address result = filtered.get(0);
                return new SimpleStringProperty(result.getAddress()+" "+result.getAddress2());
            }
            return null;
        });
        
        customerCity.setCellValueFactory(customer->{
            int addressId = ((Customer)customer.getValue()).getAddressId();
            FilteredList<Address> filtered = manager.getAddresses().filtered(address->address.getAddressId() == addressId);
            FilteredList<City> filteredCity = manager.getCities().filtered(city->city.getCityId()==filtered.get(0).getCityId());
            return new SimpleStringProperty(filteredCity.get(0).getCity());
        });
        
        customerCountry.setCellValueFactory(customer->{
            int addressId = ((Customer)customer.getValue()).getAddressId();
            FilteredList<Address> filtered = manager.getAddresses().filtered(address->address.getAddressId() == addressId);
            FilteredList<City> filteredCity = manager.getCities().filtered(city->city.getCityId()==filtered.get(0).getCityId());
            FilteredList<Country> filteredCountry = manager.getCountries().filtered(country->country.getCountryId() == filteredCity.get(0).getCountryId());            
            return new SimpleStringProperty(filteredCountry.get(0).getCountry());
        });
                
        customerTable.setItems(manager.getCustomers());
        
    }    

    @Override
    public String getScreenName() {
        return SCREEN_NAME;
    }
}
