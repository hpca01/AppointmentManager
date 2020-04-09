/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal;

import appointmentcal.views.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author hp
 */
public class AppointmentCal extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        AppointmentManager manager = new AppointmentManager();
        ViewFactory view = new ViewFactory(manager);
        view.showLoginScreen();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
