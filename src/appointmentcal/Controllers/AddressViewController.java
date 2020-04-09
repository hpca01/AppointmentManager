/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.Controllers;

import appointmentcal.AppointmentManager;
import appointmentcal.models.Address;
import appointmentcal.models.City;
import appointmentcal.models.Customer;
import appointmentcal.views.ViewFactory;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author hp
 */
public class AddressViewController extends BaseController implements Initializable {
    
    private final String SCREEN_NAME = "Address";
    
    @FXML
    private TextField address;

    @FXML
    private TextField address2;

    @FXML
    private ComboBox<String> cityCombo;

    @FXML
    private TextField phoneNumber;

    @FXML
    private TextField zipcode;

    public AddressViewController(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        super(fxmlStringName, viewFactory, manager);
    }

    @FXML
    void save(ActionEvent event) {
        String selectedItem = cityCombo.getSelectionModel().getSelectedItem();
        FilteredList<City> filtered = manager.getCities().filtered(city->city.getCity().equals(selectedItem));
        int cityId = filtered.get(0).getCityId();
        
        Address selectedAddress = manager.getCurrentAddress();
        selectedAddress.setAddress(address.textProperty().get());
        selectedAddress.setAddress2(address2.textProperty().get());
        selectedAddress.setPhone(phoneNumber.textProperty().get());
        selectedAddress.setPostalCode(zipcode.textProperty().get());
        selectedAddress.setCityId(cityId);
        selectedAddress.setLastUpdateBy(manager.getCurrentUser().getUserName());
        
        selectedAddress.update();
        
        manager.refreshAddresses();
        
        closeScreen();
    }

    @FXML
    void saveNew(ActionEvent event) {
        Address newAddress = new Address();
        String selectedItem = cityCombo.getSelectionModel().getSelectedItem();
        FilteredList<City> filtered = manager.getCities().filtered(city->city.getCity().equals(selectedItem));
        int cityId = filtered.get(0).getCityId();
        
        newAddress.setAddress(address.textProperty().get());
        newAddress.setAddress2(address2.textProperty().get());
        newAddress.setPhone(phoneNumber.textProperty().get());
        newAddress.setPostalCode(zipcode.textProperty().get());
        newAddress.setCityId(cityId);
        newAddress.setCreatedBy(manager.getCurrentUser().getUserName());
        newAddress.setLastUpdateBy(manager.getCurrentUser().getUserName());
        
        newAddress.create();
        manager.getAddresses().add(newAddress);
        closeScreen();
    }
    
    void closeScreen(){
        viewFactory.showCustomers();
        Stage stage = (Stage)cityCombo.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Address selectedAddress = manager.getCurrentAddress();
        setupForm(selectedAddress);
        
        setupComboBox(selectedAddress);
    }

    private void setupForm(Address selectedAddress) {
        address.setText(selectedAddress.getAddress());
        address2.setText(selectedAddress.getAddress2());
        phoneNumber.setText(selectedAddress.getPhone());
        zipcode.setText(selectedAddress.getPostalCode());
    }

    private void setupComboBox(Address selectedAddress) {
        //setup combo box
        FilteredList<City> relatedCities = manager.getCities().filtered(city->city.getCityId() == selectedAddress.getCityId());
        String currentCityforAddress = relatedCities.get(0).getCity();
        Set<String> cities = manager.getCities().stream().map(city->city.getCity()).collect(Collectors.toSet());
        cityCombo.setItems(FXCollections.observableArrayList(cities.stream().sorted().collect(Collectors.toList())));
        cityCombo.getSelectionModel().select(currentCityforAddress);
    }

    @Override
    public String getScreenName() {
        return SCREEN_NAME;
    }
    
}
