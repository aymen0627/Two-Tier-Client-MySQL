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
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.table.AbstractTableModel;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.Properties;

//Professor's code to connect to database

public class ResultSetTableModel extends AbstractTableModel {

   private Connection connection;
   private Statement statement;
   private ResultSet resultSet;
   private ResultSetMetaData metaData;
   private int numberOfRows;

   //connect to main project3 database
   //use properties file instead of hard coding
   private final String DATABASE_URL = "jdbc:mysql://localhost:3306/project3?useTimezone=true&serverTimezone=UTC";

   //connect to operationslog data base for testing, should try to do it through properties file
   private final String DATABASE_URL_2 = "jdbc:mysql://localhost:3306/operationslog?useTimezone=true&serverTimezone=UTC";
   private final String USERNAME = "root";
   private final String PASSWORD = "ucf0627";
   private Statement logIt;


   public boolean connectedToDataBase = false;

   //make cleaner
   public ResultSetTableModel(Connection incomingConnection) throws SQLException, ClassNotFoundException {
      // constructor initializes resultSet and obtains its meta data object;
      // determines number of row
      connection = incomingConnection;
      statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      connectedToDataBase = true;
      Properties testProp = new Properties();
      FileInputStream fileTest = null;
      MysqlDataSource testSource = null;

      try{
         fileTest = new FileInputStream("root.properties");
         testProp.load(fileTest);
         testSource = new MysqlDataSource();
         //connect to operationslog db
         testSource.setURL(testProp.getProperty("MYSQL_DB_URL2"));
         testSource.setURL(testProp.getProperty("MYSQL_DB_USERNAME"));
         testSource.setPassword(testProp.getProperty("MYSQL_DB_PASSWORD"));

      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public Class getColumnClass(int column) throws IllegalStateException {
      if (!connectedToDataBase) {
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
      if (!connectedToDataBase) {
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
      if (!connectedToDataBase) {
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
      if (!connectedToDataBase) {
         throw new IllegalStateException("Not Connected To Database");
      }

      return numberOfRows;
   }

   public Object getValueAt(int row, int column) throws IllegalStateException {
      if (!connectedToDataBase) {
         throw new IllegalStateException("Not Connected To Database");
      }

      try {
         resultSet.next();
         resultSet.absolute(row + 1);
         return resultSet.getObject(column + 1);
      } catch (SQLException sqlException) {
         sqlException.printStackTrace();
      }
      return "";
   }

   public void setQuery(String query) throws SQLException, IllegalStateException, ClassNotFoundException {
      // ensure database connection is available
      if (!connectedToDataBase) {
         throw new IllegalStateException("Not Connected To Database");
      }

      resultSet = statement.executeQuery(query);

      metaData = resultSet.getMetaData();
      // determine number of rows in ResultSet
      resultSet.last();
      numberOfRows = resultSet.getRow();



      MysqlDataSource dataSource = null;

      MysqlDataSource dataSource2 = null;

      dataSource = new MysqlDataSource();
      dataSource.setURL(DATABASE_URL);
      dataSource.setUser(USERNAME);
      dataSource.setPassword(PASSWORD);

      dataSource2 = new MysqlDataSource();
      dataSource2.setURL(DATABASE_URL_2);
      dataSource2.setUser(USERNAME);
      dataSource2.setPassword(PASSWORD);


      Connection logConnection = dataSource2.getConnection();

      String counterLog = "update operationscount set num_queries = num_queries + 1";

      logIt = logConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      logIt.executeUpdate(counterLog);

      // notify JTable that model has changed
      fireTableStructureChanged();
   }

   public int setUpdate(String query) throws SQLException, IllegalStateException {
      if (!connectedToDataBase) {
         throw new IllegalStateException("Not Connected To Database");
      }

      int res = statement.executeUpdate(query);

      MysqlDataSource dataSource = null;
      MysqlDataSource dataSource2 = null;

      dataSource = new MysqlDataSource();
      dataSource.setURL(DATABASE_URL);
      dataSource.setUser(USERNAME);
      dataSource.setPassword(PASSWORD);
      //Connection logConnection1 = dataSource.getConnection();


      dataSource2 = new MysqlDataSource();
      dataSource2.setURL(DATABASE_URL_2);
      dataSource2.setUser(USERNAME);
      dataSource2.setPassword(PASSWORD);
      Connection logConnection = dataSource2.getConnection();

      String counterLog = "update operationscount set num_updates = num_updates + 1";

      logIt = logConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      logIt.executeUpdate(counterLog);

      // notify JTable that model has changed
      fireTableStructureChanged();

      return res;
   }

   //not sure if needed for setup
   public void disconnectFromDataBase() {
      if (!connectedToDataBase)
         return;
      else try {
         statement.close();
         connection.close();
      }
      catch (SQLException sqlException) {
         sqlException.printStackTrace();
      } finally {
         connectedToDataBase = false;
      }
   }
}