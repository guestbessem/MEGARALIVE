package com.example.scripttransformer.util;

public class ScriptGenContext {
    public static String dataSourceFilePath = "";
    public static String historySourceFilePath = "";
    public static String roleName = "" ;
    public static String logName = "";
    public static String outputFolder = "";
    public static String replaceTo = "";


    public static String grantOnTables="GRANT SELECT, INSERT, UPDATE, DELETE ON $1 TO "+roleName+";";
    public static String grantOnViews =  "GRANT SELECT ON $1 TO "+roleName+".$1 ;";

    public static String createSynonym =  "CREATE OR REPLACE PUBLIC SYNONYM $1 FOR "+roleName+".$1 ;";

    public static String lanzador_megacmp_Script = "";

    public static boolean exists_02 = false;
    public static boolean exists_03 = false;
    public static boolean exists_12 = false;
    public static boolean exists_13 = false;

    public static String getDataSourceFilePath() {
        return dataSourceFilePath;
    }

    public static void setDataSourceFilePath(String dataSourceFilePath) {
        ScriptGenContext.dataSourceFilePath = dataSourceFilePath;
    }

    public static String getHistorySourceFilePath() {
        return historySourceFilePath;
    }

    public static void setHistorySourceFilePath(String historySourceFilePath) {
        ScriptGenContext.historySourceFilePath = historySourceFilePath;
    }

    public static boolean isExists_02() {
        return exists_02;
    }

    public static void setExists_02(boolean exists_02) {
        ScriptGenContext.exists_02 = exists_02;
    }

    public static boolean isExists_03() {
        return exists_03;
    }

    public static void setExists_03(boolean exists_03) {
        ScriptGenContext.exists_03 = exists_03;
    }

    public static boolean isExists_12() {
        return exists_12;
    }

    public static void setExists_12(boolean exists_12) {
        ScriptGenContext.exists_12 = exists_12;
    }

    public static boolean isExists_13() {
        return exists_13;
    }

    public static void setExists_13(boolean exists_13) {
        ScriptGenContext.exists_13 = exists_13;
    }


    public static String getRoleName() {
        return roleName;
    }

    public static void setRoleName(String roleName) {
        ScriptGenContext.roleName = roleName;
        ScriptGenContext.setCreateSynonym();
        ScriptGenContext.setGrantOnTables();
        ScriptGenContext.setGrantOnViews();
    }

    public static String getLogName() {
        return logName;
    }

    public static void setLogName(String logName) {
        ScriptGenContext.logName = logName;
        getLanzador_megacmp_Script();

    }

    public static String getOutputFolder() {
        return outputFolder;
    }

    public static void setOutputFolder(String outputFolder) {
        ScriptGenContext.outputFolder = outputFolder;
    }

    public static void setGrantOnTables() {
        ScriptGenContext.grantOnTables =  "GRANT SELECT, INSERT, UPDATE, DELETE ON $1 TO "+roleName+";";
    }


    public static  void setGrantOnViews() {
        ScriptGenContext.grantOnViews=  "GRANT SELECT ON $1 TO "+roleName+".$1 ;";
    }


    public static void setCreateSynonym() {
        ScriptGenContext.createSynonym = "CREATE OR REPLACE PUBLIC SYNONYM $1 FOR "+roleName+".$1 ;";
    }

    public static String getLanzador_megacmp_Script() {
     return   ScriptGenContext.lanzador_megacmp_Script =  "spool \""+logName+".log\";"
                + System.lineSeparator()
                + "show user WHENEVER SQLERROR EXIT;"
                + System.lineSeparator()
                + "WHENEVER OSERROR EXIT;"
                + System.lineSeparator()
                + "select ora_database_name from dual;"
                + System.lineSeparator()
                + "select systimestamp from dual;"
                + System.lineSeparator()
                + "select object_name from user_objects where status='INVALID';"
                + System.lineSeparator()
                + "PROMPT ejecutando     ....................................."
                + System.lineSeparator()
                + "@$1"
                + System.lineSeparator()
                + "select to_char(sysdate,'dd/mm/yyyy hh24:mi:ssss') from dual;"
                + System.lineSeparator()
                + "@$2"
                + System.lineSeparator()
                + "select to_char(sysdate,'dd/mm/yyyy hh24:mi:ssss') from dual;"
                + System.lineSeparator()
                + "@$3"
                + System.lineSeparator()
                + "select to_char(sysdate,'dd/mm/yyyy hh24:mi:ssss') from dual;"
                + System.lineSeparator()
                + "PROMPT fin ejecucion. ..................................... "
                + System.lineSeparator()
                + "select object_name from user_objects where status='INVALID';"
                + System.lineSeparator() + "spool off;";
    }


}
