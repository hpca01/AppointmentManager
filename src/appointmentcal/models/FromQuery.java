/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models;

import java.sql.ResultSet;

/**
 *
 * @author hp
 */
public interface FromQuery<T> {
    public <T> T from(ResultSet rs);
    int create();
    int update();
    int delete();
    int refresh();
}
