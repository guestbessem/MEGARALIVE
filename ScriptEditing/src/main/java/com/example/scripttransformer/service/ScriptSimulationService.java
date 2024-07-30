package com.example.scripttransformer.service;

import com.example.scripttransformer.entity.ParametreSimulation;
import com.example.scripttransformer.entity.ResultatSimulation;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

@Service
public class ScriptSimulationService {

    private static final Logger logger = LoggerFactory.getLogger(ScriptSimulationService.class);

    public ResultatSimulation simulate(ResultatSimulation rs) {
        ParametreSimulation ps = rs.getPs();
        File file = rs.getScript();

        try {
            // Get the database connection
            Connection con = DriverManager.getConnection(ps.getUrl(), ps.getUser(), ps.getPwd());
            logger.info("Connection established......");

            // Initialize ScriptRunner and execute the script
            try (Reader reader = new BufferedReader(new FileReader(file))) {
                ScriptRunner sr = new ScriptRunner(con);
                sr.setStopOnError(true);
                sr.runScript(reader);
                rs.setRapport("SQL SCRIPT EXECUTED SUCCESSFULLY!");
            } catch (Exception e) {
                logger.error("Error running SQL script: ", e);
                rs.setRapport(e.getMessage());
                rs.setWithError(Boolean.TRUE);
            }
        } catch (SQLException e) {
            logger.error("SQL Error: ", e);
            rs.setRapport(e.getMessage());
            rs.setWithError(Boolean.TRUE);
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            rs.setRapport(e.getMessage());
            rs.setWithError(Boolean.TRUE);
        }

        rs.setDateDeSimulation(new Date());
        return rs;
    }
}
