/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.Controllers;

import appointmentcal.AppointmentManager;
import appointmentcal.models.User;
import appointmentcal.views.ViewFactory;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author hp
 */
public class LoginController extends BaseController implements Initializable{
    
    private final String SCREEN_NAME = "Login";
    private final String INFO_ERROR = "Login Error";
    
    private ResourceBundle currentLanguage;
    
    @FXML
    private TextField usrInput;

    @FXML
    private PasswordField usrPass;

    @FXML
    private ComboBox<String> langCombo;

    @FXML
    private Button loginButton;

    @FXML
    void loginAction(ActionEvent event) {
        if(isValid()){
            Optional<User> validatedUser = manager.validateUser(usrInput.getText(), usrPass.getText());
            if(validatedUser.isPresent()){
                manager.setCurrentUser(validatedUser.get());
                viewFactory.showCustomers();
                viewFactory.closeStage((Stage)usrPass.getScene().getWindow());
                return;
            }else{
                //no user and password found
                viewFactory.raiseInfoAlert(SCREEN_NAME, INFO_ERROR, currentLanguage.getString("login_auth_err"));
            }
        }
    }

    
    public LoginController(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        super(fxmlStringName, viewFactory, manager);
    }
    
    private boolean isValid(){
        if((usrInput.getText().isEmpty()) || (usrPass.getText().isEmpty())){
            viewFactory.raiseInfoAlert(SCREEN_NAME, INFO_ERROR,  currentLanguage.getString("login_info_err"));//make this multi-locale based on combobox
        }else{
            return true;
        }
        return false;
    }
    
    Locale  getLocale(){
        Locale currentLocale = Locale.getDefault();
        return currentLocale;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String[] languages = "English,Spanish".toString().split(",");
        langCombo.setItems(FXCollections.observableArrayList(languages));
        
        
        langCombo.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue)->{
            if (((String)newValue).equals("Spanish") ){
                ResourceBundle language = ResourceBundle.getBundle("appointmentcal.views.resources.lang", new Locale("es", "ES"));
                this.currentLanguage = language;
            }else{
                ResourceBundle language = ResourceBundle.getBundle("appointmentcal.views.resources.lang", new Locale("en", "EN"));
                this.currentLanguage = language;
            }
        });
        
        if (getLocale().getDisplayLanguage() == "English"){
            ResourceBundle language = ResourceBundle.getBundle("appointmentcal.views.resources.lang", new Locale("en", "EN"));
            this.currentLanguage = language;
            langCombo.getSelectionModel().select("English");
        }else if(getLocale().getDisplayLanguage() == "Spanish"){
            ResourceBundle language = ResourceBundle.getBundle("appointmentcal.views.resources.lang", new Locale("es", "ES"));
            this.currentLanguage = language;
            langCombo.getSelectionModel().select("Spanish");
        }else{
            //last case scenario == english
            ResourceBundle language = ResourceBundle.getBundle("appointmentcal.views.resources.lang", new Locale("en", "EN"));
            this.currentLanguage = language;
            langCombo.getSelectionModel().select("English");
        }
    }    

    @Override
    public String getScreenName() {
        return this.SCREEN_NAME;
    }
    
}
