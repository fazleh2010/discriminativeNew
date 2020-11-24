/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author elahi SELECT * FROM `en_A_B_term` WHERE 1 DROP table `en_A_B_term`;
 */
public class MySQLAccess   {

    private Connection conn = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    //String url = "jdbc:mysql://localhost:3306/mysql";
    public static String host = "localhost";
    public static String port = "3306";
    private String url = "jdbc:mysql://" + host + ":" + port + "/test";

    public MySQLAccess() throws Exception {
        this.connectDataBase();
    }

    //command line location to /opt/lampp/bin/mysql -u root -p
    public void connectDataBase() throws Exception {
        
        String url = "jdbc:mysql://localhost:3306/mysql"; 
        String user = "root"; String password = "m326888";         
         
        try {   
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successfull!!");
        } catch (Exception e) {
            System.out.println("An error occurred. Maybe user/password is invalid"+e.getMessage());
        }
        finally {
            close();
        }

    }

    public void createTermTable(String tableName) throws Exception {

        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE " + tableName + " "
                    + "(id VARCHAR(255) not NULL, "
                    + " term VARCHAR(4000), "
                    + " originalUrl VARCHAR(4000), "
                    + " alternativeUrl VARCHAR(255), "
                    + " reliabilityCode VARCHAR(255), "
                    + " administrativeStatus VARCHAR(255), "
                    + " subjectField VARCHAR(255), "
                    + " subjectDescription VARCHAR(255), "
                    + " reference VARCHAR(255), "
                    + " language VARCHAR(255), "
                    + " pair VARCHAR(255), "
                    + " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

        } catch (Exception e) {
            System.out.println("An error occurred. Maybe user/password is invalid");
        }
    }

    public void createLinkingTable(String tableName) throws Exception {

        try {
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE " + tableName + " "
                    + "(id INTEGER not NULL, "
                    + " term_1 VARCHAR(255), "
                    + " term_1_url VARCHAR(255), "
                    + " term_1_alter_url VARCHAR(255), "
                    + " term_2 VARCHAR(255), "
                    + " term_2_url VARCHAR(255), "
                    + " term_2_alter_url VARCHAR(255), "
                    + " PRIMARY KEY ( id ))";

            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

        } catch (Exception e) {
            System.out.println("An error occurred. Maybe user/password is invalid");
        }

    }

    public void deleteTable(String tableName) throws Exception {

        try {
            Statement stmt = conn.createStatement();
            String sql = "Drop table " + tableName;
            stmt.executeUpdate(sql);
            System.out.println("delete table in given database...");

        } catch (Exception e) {
            System.out.println("An error occurred. Maybe user/password is invalid");
        }

    }

    public void insertDataTermTable(String tableName) {
        Set<String>urls=new HashSet<String>();
        try {
            Integer index = 1;

            for (String url : urls) {
                index=index+1;
                String id = index.toString();
                System.out.println(id);
                String query = " insert into " + tableName
                        + " (id, term, originalUrl, alternativeUrl, reliabilityCode, "
                        + " administrativeStatus, subjectField, subjectDescription, reference,"
                        + " language, pair)"
                        + " values (?,?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString(1, id);
                preparedStmt.setString(2, "2");
                preparedStmt.setString(3, "3");
                preparedStmt.setString(4, "4");
                preparedStmt.setString(5, "5");
                preparedStmt.setString(6, "6");
                preparedStmt.setString(7, "7");
                preparedStmt.setString(8, "8");
                preparedStmt.setString(9, "9");
                preparedStmt.setString(10, "10");
                preparedStmt.setString(11, "11");
                preparedStmt.execute();
            }

        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }

    public void insertDataLinkTable(String tableName) {
        Integer index = 0;
        try {

            String query = " insert into " + tableName
                    + " (id, term_1, term_1_url, term_1_alter_url, term_2, term_2_url, term_2_alter_url)"
                    + " values (?,?,?,?,?,?,?)";

            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setInt(1, index++);
            preparedStmt.setString(2, "term_1");
            preparedStmt.setString(3, "term_1_url");
            preparedStmt.setString(4, "term_1_alter_url");
            preparedStmt.setString(5, "term_2");
            preparedStmt.setString(6, "term_2_url");
            preparedStmt.setString(7, "term_2_alter_url");
            preparedStmt.execute();
        } catch (Exception e) {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }

    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            String user = resultSet.getString("myuser");
            String website = resultSet.getString("webpage");
            String summary = resultSet.getString("summary");
            Date date = resultSet.getDate("datum");
            String comment = resultSet.getString("comments");
            System.out.println("User: " + user);
            System.out.println("Website: " + website);
            System.out.println("summary: " + summary);
            System.out.println("Date: " + date);
            System.out.println("Comment: " + comment);
        }
    }

    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {

        }
    }

    // connect way #1
    /*// Statements allow to issue SQL queries to the database
            statement = connect.createStatement();
            // Result set get the result of the SQL query
            resultSet = statement
                    .executeQuery("select * from feedback.comments");
            writeResultSet(resultSet);
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)");
            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setString(1, "Test");
            preparedStatement.setString(2, "TestEmail");
            preparedStatement.setString(3, "TestWebpage");
            preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
            preparedStatement.setString(5, "TestSummary");
            preparedStatement.setString(6, "TestComment");
            preparedStatement.executeUpdate();
            preparedStatement = connect
                    .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            resultSet = preparedStatement.executeQuery();
            writeResultSet(resultSet);
            // Remove again the insert comment
            preparedStatement = connect
                    .prepareStatement("delete from feedback.comments where myuser= ? ; ");
            preparedStatement.setString(1, "Test");
            preparedStatement.executeUpdate();
            resultSet = statement
                    .executeQuery("select * from feedback.comments");
            writeMetaData(resultSet);*/
}