/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.Controllers;

import appointmentcal.AppointmentManager;
import appointmentcal.views.ViewFactory;

/**
 *
 * @author hp
 */
public abstract class BaseController {
    
    protected ViewFactory viewFactory;
    protected AppointmentManager manager;
    private String fxmlStringName;
    
    public BaseController(String fxmlStringName, ViewFactory viewFactory, AppointmentManager manager) {
        this.fxmlStringName = fxmlStringName;
        this.viewFactory = viewFactory;
        this.manager = manager;
    }

    public String getFxmlStringName() {
        return fxmlStringName;
    }

    public abstract String getScreenName();
    
    
}
