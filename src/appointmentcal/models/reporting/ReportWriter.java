/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public abstract class ReportWriter {
    private String sql;
    List<String> buf;
    
    public abstract List<String> getQuery();
    
    public void saveTo(File fileLoc){
        try(FileWriter writer = new FileWriter(fileLoc);){
            buf.stream().forEach(each->{
                try {
                    writer.append(each);
                    writer.append(System.getProperty("line.separator"));
                } catch (IOException ex) {
                    Logger.getLogger(ReportWriter.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
        } catch (IOException ex) {
            Logger.getLogger(ReportWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ReportWriter(String sql) {
        this.sql = sql;
    }
    public String getSql(){
        return sql;
    }
}
