/*
Name: Aymen Hasnain
Course: CNT 4714 Spring 2023
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 9, 2023
Class: Enterprise Computing
*/

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.Properties;
import java.sql.ResultSet;
import javax.swing.table.AbstractTableModel;
import java.sql.Connection;
import java.util.Properties;

//Professor's code to connect to database

public class ResultSetTableModel extends AbstractTableModel {

   private Connection connection;
   private Statement statement;
   private Statement statement1;
   private ResultSet resultSet;
   private ResultSetMetaData metaData;
   private int numberOfRows;
   private Properties operationProp = new Properties();
   private FileInputStream filein = null;

   private Statement logIt;

   public boolean connectedToDatabase = false;
   public boolean connectedToDataBase2 = false;

   public ResultSetTableModel(Connection project3Connection) throws SQLException, ClassNotFoundException, IOException {
      // constructor initializes resultSet and obtains its meta data object;
      // determines number of row
      connection = project3Connection;

      statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      connectedToDatabase = true;

      ///////////////////////////
      //load and connect to operations db

//      MysqlDataSource operationsSource = new MysqlDataSource();
//
//      filein = new FileInputStream("operations.properties");
//
//
//      operationProp.load(filein);
//      operationsSource.setURL(operationProp.getProperty("MYSQL_DB_URL"));
//      operationsSource.setUser(operationProp.getProperty("MYSQL_DB_USERNAME"));
//      operationsSource.setPassword(operationProp.getProperty("MYSQL_DB_PASSWORD"));
//
//      Connection operationConnection = operationsSource.getConnection();
//
//      statement1 =
//              operationConnection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,
//                      ResultSet.CONCUR_READ_ONLY );

      //connectedToDataBase2 = true;


   }

   public Class getColumnClass(int column) throws IllegalStateException {
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected To Database");
      }
      try {
         String className = metaData.getColumnClassName(column + 1);
         return Class.forName(className);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return Object.class;
   }

   public int getColumnCount() throws IllegalStateException {
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected To Database");
      }
      try {
         return metaData.getColumnCount();
      } catch (SQLException sqlException) {
         sqlException.printStackTrace();
      }
      return 0;
   }

   public String getColumnName(int column) throws IllegalStateException {
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected To Database");
      }
      try {
         return metaData.getColumnName(column + 1);
      } catch (SQLException sqlException) {
         sqlException.printStackTrace();
      }
      return "";
   }

   public int getRowCount() throws IllegalStateException {
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected To Database");
      }

      return numberOfRows;
   }

   public Object getValueAt(int row, int column) throws IllegalStateException {
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected To Database");
      }
      // obtain a value at specified ResultSet row and column
      try {
         resultSet.next();
         resultSet.absolute(row + 1);
         return resultSet.getObject(column + 1);
      } catch (SQLException sqlException) {
         sqlException.printStackTrace();
      }
      return "";
   }

   public void setQuery(String query) throws SQLException, IllegalStateException, ClassNotFoundException, IOException {
      // ensure database connection is available
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected To Database");
      }



      resultSet = statement.executeQuery(query);

      metaData = resultSet.getMetaData();
      // determine number of rows in ResultSet
      resultSet.last();
      numberOfRows = resultSet.getRow();

      //connect to operations DB
      MysqlDataSource operationsSource = new MysqlDataSource();

      //load and connect to operations Db for logging
      filein = new FileInputStream("operations.properties");

      operationProp.load(filein);
      operationsSource.setURL(operationProp.getProperty("MYSQL_DB_URL"));
      operationsSource.setUser(operationProp.getProperty("MYSQL_DB_USERNAME"));
      operationsSource.setPassword(operationProp.getProperty("MYSQL_DB_PASSWORD"));


      Connection operationConnection = operationsSource.getConnection();

      //command to update operations counter
      String counterLog = "update operationscount set num_queries = num_queries + 1";

      logIt = operationConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      logIt.executeUpdate(counterLog);

      // notify JTable that model has changed
      fireTableStructureChanged();

      //disconnect safely
      //disconnectFromDataBase();
   }

   public int setUpdate(String query) throws SQLException, IllegalStateException, IOException {
      if (!connectedToDatabase) {
         throw new IllegalStateException("Not Connected To Database");
      }

      int res = statement.executeUpdate(query);

      MysqlDataSource operationsSource = new MysqlDataSource();
      //load and connect to operations Db for logging
      filein = new FileInputStream("operations.properties");

      operationProp.load(filein);
      operationsSource.setURL(operationProp.getProperty("MYSQL_DB_URL"));
      operationsSource.setUser(operationProp.getProperty("MYSQL_DB_USERNAME"));
      operationsSource.setPassword(operationProp.getProperty("MYSQL_DB_PASSWORD"));

      Connection operationConnection = operationsSource.getConnection();


      //command to update operations counter
      String counterLog = "update operationscount set num_updates = num_updates + 1";

      logIt = operationConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      logIt.executeUpdate(counterLog);

      // notify JTable that model has changed
      fireTableStructureChanged();

      //disconnectFromDataBase();

      return res;
   }

   //not sure if needed for setup
   public void disconnectFromDataBase() {
      if (!connectedToDatabase)
         return;
      else try {
         statement.close();
         connection.close();
      }
      catch (SQLException sqlException) {
         sqlException.printStackTrace();
      } finally {
         connectedToDatabase = false;
      }
   }
}