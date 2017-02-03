package edu.gatech.health.manager;

import edu.gatech.health.objects.Condition;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trevoris Jefferson on 4/10/2016.
 * Class to manage queries to MySql DB
 */
public class QueryManger {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public List<Condition> getListOfConditions(String patientId, List<Condition> conditions) throws Exception{
        try {

            statement = createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from conditions where patientid = '" + patientId + "';");
            writeResultSet(resultSet, conditions);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
        return conditions;
    }

    public List<Condition> getSpecificOfCondition(String patientId, String conditionId, List<Condition> conditions) throws Exception{
        try {

            statement = createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from conditions where patientid = '" + patientId + "' AND conditionid = '" + conditionId + "';");
            writeResultSet(resultSet, conditions);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
        return conditions;
    }

    public int addCondition(String patientId, Condition condition) {
        int result = 0;
        try{
            connect = createConnection();
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  conditions values (?, ?, ?, ? )");

            // Parameters start with 1
            preparedStatement.setString(1, patientId);
            preparedStatement.setString(2, condition.getConditionId());
            preparedStatement.setString(3, String.valueOf(condition.getConditionx()));
            preparedStatement.setString(4, String.valueOf(condition.getConditiony()));
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Insert failed: " + e);
            result = -1;
        } finally {
            close();
        }
        return result;
    }

    public int moveCondition(Condition condition) {
        int result = 0;
        try{
            connect = createConnection();
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("UPDATE conditions SET conditionx = ?, conditiony = ? WHERE patientid = ? AND conditionid = ?");

            // Parameters start with 1
            preparedStatement.setString(1, String.valueOf(condition.getConditionx()));
            preparedStatement.setString(2, String.valueOf(condition.getConditiony()));
            preparedStatement.setString(3, condition.getPatientId());
            preparedStatement.setString(4, condition.getConditionId());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Insert failed: " + e);
            result = -1;
        } finally {
            close();
        }
        return result;
    }


    public void readDataBase() throws Exception {
        /*try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/feedback?"
                            + "user=sqluser&password=sqluserpw");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from feedback.comments");
            writeResultSet(resultSet);

            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)");
            // "myuser, webpage, datum, summery, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setString(1, "Test");
            preparedStatement.setString(2, "TestEmail");
            preparedStatement.setString(3, "TestWebpage");
            preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
            preparedStatement.setString(5, "TestSummary");
            preparedStatement.setString(6, "TestComment");
            preparedStatement.executeUpdate();

            preparedStatement = connect
                    .prepareStatement("SELECT myuser, webpage, datum, summery, COMMENTS from feedback.comments");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);

            // Remove again the insert comment
            preparedStatement = connect
                    .prepareStatement("delete from feedback.comments where myuser= ? ; ");
            preparedStatement.setString(1, "Test");
            preparedStatement.executeUpdate();

            resultSet = statement
                    .executeQuery("select * from feedback.comments");
            writeMetaData(resultSet);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }*/

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //   Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
            System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet, List<Condition> conditions) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            String patientid = resultSet.getString("patientid");
            String conditionid = resultSet.getString("conditionid");
            String conditionx = resultSet.getString("conditionx");
            String conditiony = resultSet.getString("conditiony");

            for (int i=0; i<conditions.size(); i++){
                if (conditions.get(i).getConditionId().equals(conditionid)
                        && !conditionx.isEmpty()
                        && !conditiony.isEmpty()){
                    conditions.get(i).setConditionx(conditionx);
                    conditions.get(i).setConditiony(conditiony);
                    break;
                }
            }

            /*System.out.println("patientid: " + patientid);
            System.out.println("conditionid: " + conditionid);
            System.out.println("conditionx: " + conditionx);
            System.out.println("conditiony: " + conditiony);*/
        }
    }

    private Statement createStatement() throws Exception{
        // This will load the MySQL driver, each DB has its own driver
        //Class.forName("com.mysql.jdbc.Driver");
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Setup the connection with the DB
        connect = DriverManager
                .getConnection("jdbc:mysql://medimap-cs6440.c67t2hnnil4w.us-west-2.rds.amazonaws.com:3306/meddb?"
                        + "user=admin&password=omscscs6440Medimap");

        // Statements allow to issue SQL queries to the database
        statement = connect.createStatement();
        return statement;
    }

    private Connection createConnection() throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");
        // Setup the connection with the DB
        connect = DriverManager
                .getConnection("jdbc:mysql://medimap-cs6440.c67t2hnnil4w.us-west-2.rds.amazonaws.com:3306/meddb?"
                        + "user=admin&password=omscscs6440Medimap");
        return connect;
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            System.out.println("Could not close: " + e);
        }
    }


}
