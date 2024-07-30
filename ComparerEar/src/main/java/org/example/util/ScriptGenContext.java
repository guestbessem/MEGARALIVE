package org.example.util;

public class ScriptGenContext {

    // Constants
    public static final String GRANT_ON_TABLES_TEMPLATE = "GRANT SELECT, INSERT, UPDATE, DELETE ON $1 TO %s;";
    public static final String GRANT_ON_VIEWS_TEMPLATE = "GRANT SELECT ON $1 TO %s.$1;";
    public static final String CREATE_SYNONYM_TEMPLATE = "CREATE OR REPLACE PUBLIC SYNONYM $1 FOR %s.$1;";
    public static final String LANZADOR_MEGA_CMP_SCRIPT_TEMPLATE =
            "spool \"%s.log\"%n" +
                    "show user WHENEVER SQLERROR EXIT;%n" +
                    "WHENEVER OSERROR EXIT;%n" +
                    "select ora_database_name from dual;%n" +
                    "select systimestamp from dual;%n" +
                    "select object_name from user_objects where status='INVALID';%n" +
                    "PROMPT ejecutando     .....................................%n" +
                    "@$1%n" +
                    "select to_char(sysdate,'dd/mm/yyyy hh24:mi:ssss') from dual;%n" +
                    "@$2%n" +
                    "select to_char(sysdate,'dd/mm/yyyy hh24:mi:ssss') from dual;%n" +
                    "@$3%n" +
                    "select to_char(sysdate,'dd/mm/yyyy hh24:mi:ssss') from dual;%n" +
                    "PROMPT fin ejecucion. ..................................... %n" +
                    "select object_name from user_objects where status='INVALID';%n" +
                    "spool off;";

    // Fields
    private static String dataSourceFilePath = "";
    private static String historySourceFilePath = "";
    private static String roleName = "";
    private static String logName = "";
    private static String outputFolder = "";
    private static String replaceTo = "";

    private static boolean exists02 = false;
    private static boolean exists03 = false;
    private static boolean exists12 = false;
    private static boolean exists13 = false;

    // Private constructor to prevent instantiation
    private ScriptGenContext() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Getters and Setters
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

    public static String getRoleName() {
        return roleName;
    }

    public static void setRoleName(String roleName) {
        ScriptGenContext.roleName = roleName;
    }

    public static String getLogName() {
        return logName;
    }

    public static void setLogName(String logName) {
        ScriptGenContext.logName = logName;
    }

    public static String getOutputFolder() {
        return outputFolder;
    }

    public static void setOutputFolder(String outputFolder) {
        ScriptGenContext.outputFolder = outputFolder;
    }

    public static String getReplaceTo() {
        return replaceTo;
    }

    public static void setReplaceTo(String replaceTo) {
        ScriptGenContext.replaceTo = replaceTo;
    }

    public static boolean isExists02() {
        return exists02;
    }

    public static void setExists02(boolean exists02) {
        ScriptGenContext.exists02 = exists02;
    }

    public static boolean isExists03() {
        return exists03;
    }

    public static void setExists03(boolean exists03) {
        ScriptGenContext.exists03 = exists03;
    }

    public static boolean isExists12() {
        return exists12;
    }

    public static void setExists12(boolean exists12) {
        ScriptGenContext.exists12 = exists12;
    }

    public static boolean isExists13() {
        return exists13;
    }

    public static void setExists13(boolean exists13) {
        ScriptGenContext.exists13 = exists13;
    }

    public static String getGrantOnTables() {
        return String.format(GRANT_ON_TABLES_TEMPLATE, roleName);
    }

    public static String getGrantOnViews() {
        return String.format(GRANT_ON_VIEWS_TEMPLATE, roleName);
    }

    public static String getCreateSynonym() {
        return String.format(CREATE_SYNONYM_TEMPLATE, roleName);
    }

    public static String getLanzadorMegaCmpScript() {
        return String.format(LANZADOR_MEGA_CMP_SCRIPT_TEMPLATE, logName);
    }
}
