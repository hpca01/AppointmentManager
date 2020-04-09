/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.views;

import appointmentcal.AppointmentManager;
import appointmentcal.Controllers.AddressViewController;
import appointmentcal.Controllers.AppointmentEditViewController;
import appointmentcal.Controllers.AppointmentNewView;
import appointmentcal.Controllers.AppointmentViewController;
import appointmentcal.Controllers.BaseController;
import appointmentcal.Controllers.CustomerEditViewController;
import appointmentcal.Controllers.CustomerViewController;
import appointmentcal.Controllers.LoginController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author hp
 */
public class ViewFactory {
    
    private AppointmentManager manager;
    private ArrayList<Stage> stages;
    private boolean mainViewInit;

    public ViewFactory(AppointmentManager manager) {
        this.manager = manager;
        this.stages = new ArrayList<Stage>();
    }
    
    public void showLoginScreen(){
        BaseController controller = new LoginController("Login.fxml", this, manager);
        initialize(controller);
    }
    
    private void initialize(BaseController baseCont){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseCont.getFxmlStringName()));
        fxmlLoader.setController(baseCont);
        
        Parent parent = null;
        try{
            parent = fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(ViewFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(baseCont.getScreenName());
        stage.setScene(scene);
        stage.show();
        this.stages.add(stage);
    }
    
    public void closeStage(Stage stageToClose){
        Platform.runLater(()->{
            stageToClose.close();
        });
        this.stages.remove(stageToClose);
    }
    
    public void raiseInfoAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
    
    public boolean raiseWarningAlertProceed(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING, content, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();

        return alert.getResult() == ButtonType.YES;
    }
    
    public void showCustomers() {
        BaseController controller = new CustomerViewController("CustomerView.fxml", this, manager);
        initialize(controller);
    }       

    public void showAddressEdit() {
        BaseController controller = new AddressViewController("AddressView.fxml", this, manager);
        initialize(controller);
    }
    public void showCustomerEdit(){
        BaseController controller = new CustomerEditViewController("CustomerEditView.fxml", this, manager);
        initialize(controller);
    }
    
    public void showAppointmentView(){
        BaseController controller = new AppointmentViewController("AppointmentView.fxml", this, manager);
        initialize(controller);
    }
    
    public void showAppointmentEdit(){
        BaseController controller = new AppointmentEditViewController("AppointmentEditView.fxml", this, manager);
        initialize(controller);
    }
    public void showNewAppointmentView(){
        BaseController controller = new AppointmentNewView("AppointmentEditView.fxml", this, manager);
        initialize(controller);
    }
    
}
