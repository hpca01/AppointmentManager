/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointmentcal.models.reporting;

import appointmentcal.models.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class AppointmentReport extends ReportWriter{
        
    public AppointmentReport(String sql) {
        super(sql);
    }

    @Override
    public List<String> getQuery() {
        List output = new ArrayList<String>();
        try(Connection conn = new DataSource().getConnection();){
            try(PreparedStatement stmt = conn.prepareStatement(getSql())){
                ResultSet rs = stmt.executeQuery();
                
                StringBuilder buf = new StringBuilder();
                
                ResultSetMetaData metaData = rs.getMetaData();
                int colCount = metaData.getColumnCount();
                
                String header = "";
                for (int i=1; i<colCount+1;i++){
                    header += metaData.getColumnName(i);
                    if (i!=colCount){
                        header += ",";
                    }
                }
                buf.append(header);
                output.add(buf.toString());
                while(rs.next()){
                    StringBuilder rowBuf = new StringBuilder();
                    for(int i =1; i<colCount+1; i++){
                        rowBuf.append(rs.getString(metaData.getColumnName(i)));
                        if (i!=colCount){
                            rowBuf.append(",");
                        }
                    }
                    output.add(rowBuf.toString());
                }
                this.buf = output;
                return output;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.buf = output;
        return output;
    }    
    
}
