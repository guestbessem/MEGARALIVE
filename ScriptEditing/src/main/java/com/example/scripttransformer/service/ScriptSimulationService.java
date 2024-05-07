package com.example.scripttransformer.service;


import com.example.scripttransformer.entity.ParametreSimulation;
import com.example.scripttransformer.entity.ResultatSimulation;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

@Service
public class ScriptSimulationService {
    public ResultatSimulation simulate(ResultatSimulation rs) {
      ParametreSimulation ps= rs.getPs();
      File file=rs.getScript();
        try { //le driver permet la communication avec la base de données
            if(ps.getJdbcName().toString().equals("MySQL")) {
                //enregistrement du driver
               Class.forName( "com.mysql.jdbc.Driver");
            }
            else   if(ps.getJdbcName().toString().equals("Oracle")) {
                //enregistrement du driver
                Class.forName( "oracle.jdbc.OracleDriver");
            }
            //Getting the connection
            Connection con = DriverManager.getConnection(ps.getUrl(), ps.getUser(), ps.getPwd());
            System.out.println("Connection established......");
            //Initialize de classe d'exécution de script
            ScriptRunner sr = new ScriptRunner(con);
            //l'execution s'arrete lorsque une erreur se produit a l'exec
            sr.setStopOnError(true);
            // sr.setErrorLogWriter(new PrintWrit);
            Reader reader = new BufferedReader(new FileReader(file));
            //Running the script
            sr.runScript(reader);
            rs.setRapport(new String("SQL SCRIPT EXECUTED SUCCESSFULLY!"));
        }
        catch (RuntimeException e){
            rs.setRapport(e.getMessage());
            rs.setWithError(Boolean.TRUE);

        }
        catch (Exception e){
            rs.setRapport(e.getMessage());
            rs.setWithError(Boolean.TRUE);

        }
        rs.setDateDeSimulation(new Date());
        return rs;
    }
}
