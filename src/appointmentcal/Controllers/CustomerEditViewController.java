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
import appointmentcal.models.Customer;
import appointmentcal.models.DataSource;
import appointmentcal.views.ViewFactory;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author hp
 */
public class CustomerEditViewController extends BaseController implements Initializable {

    private final String SCREEN_NAME = "Edit Customer";

    @FXML
    private ToggleButton activeSwitch;

    @FXML
    private TextField customerName;

    @FXML
    private TextField createdDate;

    @FXML
    private TextField lastUpdated;

    @FXML
    private TextField lastUpdateBy;

    @FXML
    private ComboBox<Address> addressCombo;

    public CustomerEditViewController(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        super(fxmlStringName, viewFactory, manager);
    }

    @FXML
    void delete(ActionEvent event) {
        boolean proceed = viewFactory.raiseWarningAlertProceed("Delete Warning", "Attention", "Deleting this customer will delete all associated records(such as appointments). Would you like to proceed?");
        if (proceed){
            //yes delete
            Customer currentCustomer = manager.getCurrentCustomer();
            FilteredList<Appointment> appointmentsByCustomer = manager.getAppointments().filtered(appointment->appointment.getCustomerId() == currentCustomer.getCustomerId());
            for(Appointment app: appointmentsByCustomer){
                app.delete();
                manager.getAppointments().remove(app);
            }
            currentCustomer.delete();
            manager.getCustomers().remove(currentCustomer);
            returnToCustomersTable();
        }
    }

    private int activeConverter() {
        boolean active = activeSwitch.selectedProperty().get();
        return active == true ? 1 : 0;
    }

    @FXML
    void save(ActionEvent event) {

        Customer currentCustomer = manager.getCurrentCustomer();
        currentCustomer.setCustomerName(customerName.getText());
        currentCustomer.setAddressId(addressCombo.getSelectionModel().getSelectedItem().getAddressId());
        currentCustomer.setActive(activeConverter());

        currentCustomer.update();
        
        returnToCustomersTable();

    }

    public void returnToCustomersTable(){
        Stage stage = (Stage)activeSwitch.getScene().getWindow();
        viewFactory.showCustomers();
        viewFactory.closeStage(stage);
    }
    
    @FXML
    void saveNew(ActionEvent event) {
        Customer newCustomer = new Customer();
        newCustomer.setActive(activeConverter());
        newCustomer.setCustomerName(customerName.getText());
        newCustomer.setAddressId(addressCombo.getSelectionModel().getSelectedItem().getAddressId());
        newCustomer.setCreatedBy(manager.getCurrentUser().getUserName());
        newCustomer.setCreateDate(Utils.currentDateTime());
        newCustomer.setLastUpdateBy(manager.getCurrentUser().getUserName());

        newCustomer.create();
        newCustomer.refresh();
        
        manager.getCustomers().add(newCustomer);
        returnToCustomersTable();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUpForm();
    }

    public void setActiveSwitch() {
        activeSwitch.setOnAction(event -> {
            if (activeSwitch.isSelected()) {
                activeSwitch.setStyle("-fx-background-color:green");
            } else {
                activeSwitch.setStyle("-fx-background-color:red");
            }
        });

        if (manager.getCurrentCustomer().getActive() == 1) {
            activeSwitch.selectedProperty().set(true);
            activeSwitch.setStyle("-fx-background-color:green");
        } else {
            activeSwitch.selectedProperty().set(false);
            activeSwitch.setStyle("-fx-background-color:red");
        }
    }

    public void setAddressCombo() {
        FilteredList<Address> currentCustomerAddress = manager.getAddresses().filtered(address -> address.getAddressId() == manager.getCurrentCustomer().getAddressId());
        addressCombo.setConverter(new StringConverter<Address>() {
            @Override
            public String toString(Address object) {
                return "" + object.getAddress() + " " + object.getAddress2() + " " + object.getPhone();
            }

            @Override
            public Address fromString(String string) {
                return null;
            }
        });
        addressCombo.setItems(manager.getAddresses());
        addressCombo.getSelectionModel().select(currentCustomerAddress.get(0));
    }

    public void setUpForm() {
        customerName.setText(manager.getCurrentCustomer().getCustomerName());
        createdDate.textProperty().bind(new SimpleStringProperty(manager.getCurrentCustomer().getCreateDate().toString()));
        lastUpdated.textProperty().bind(new SimpleStringProperty(manager.getCurrentCustomer().getLastUpdate().toString()));
        lastUpdateBy.textProperty().bind(new SimpleStringProperty(manager.getCurrentCustomer().getLastUpdateBy()));
        setAddressCombo();
        setActiveSwitch();
    }

    @Override
    public String getScreenName() {
        return SCREEN_NAME;
    }

}
