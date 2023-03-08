/*
Name: Aymen Hasnain
Course: CNT 4714 Spring 2023
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 9, 2023
Class: Enterprise Computing
*/
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import com.mysql.cj.jdbc.MysqlDataSource;


public class Server extends JPanel {


    private Connection connection;

    private ResultSetTableModel resultSetModel;

    public Server() {



        //border layout titles
        TitledBorder title;
        title = BorderFactory.createTitledBorder("Connection Details");
        title.setTitleColor(Color.blue);

        TitledBorder title2;
        title2 = BorderFactory.createTitledBorder("Enter an SQL Command");
        title2.setTitleColor(Color.BLUE);


        TitledBorder title3;
        title3 = BorderFactory.createTitledBorder("SQL Execution Window");
        title3.setTitleColor(Color.blue);


        JLabel props = new JLabel("Properties File");


        JTextField input = new JTextField(10);
        input.setCaretColor(Color.green);
        input.setBounds(185, 85, 165, 25);
        Border borderInput = BorderFactory.createLineBorder(Color.blue,1);
        input.setBorder(borderInput);
        add(input);

        ImageIcon image = new ImageIcon("logoMySQL.png"); //replace java logo on this




        JLabel connectionStatus = new JLabel("NOT CONNECTED", SwingConstants.CENTER);
        connectionStatus.setForeground(Color.RED);
        connectionStatus.setBackground(Color.BLACK);
        connectionStatus.setBounds(20, 230, 850, 25);
        connectionStatus.setOpaque(true);
        add(connectionStatus);

        JPanel redPanel = new JPanel();
        redPanel.setBackground(Color.white);
        redPanel.setBounds(15,10,400,200);

//        redPanel.setLayout(new GridLayout(4,2, 3,50));
//
//        TitledBorder title;
//        title = BorderFactory.createTitledBorder("Connection Details");
//        redPanel.setBorder(title);


        setPreferredSize(new Dimension(900, 600));
        setLayout(null);

        JTextArea querySQL = new JTextArea(5, 5);
        querySQL.setCaretColor(Color.blue);
        querySQL.setBackground(Color.white);
        querySQL.setWrapStyleWord(true);
        querySQL.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(querySQL, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        Box sqlSquare = Box.createHorizontalBox();
        sqlSquare.add(scroll);

        //bottom table for display result
        TableModel table = new DefaultTableModel();
        JTable showTable = new JTable(table);
        showTable.setModel(table);
        showTable.setGridColor(Color.blue);

        final Box square = Box.createHorizontalBox();
        square.add(showTable);
        square.add(new JScrollPane(showTable));



        String s1[] = { "root.properties", "client.properties", "project3app.properties", "db3.properties",
                "db4.properties" };

        JComboBox dropDown = new JComboBox(s1);
        Border borderDropdown = BorderFactory.createLineBorder(Color.blue,1);
        dropDown.setBorder(borderDropdown);
        dropDown.setBounds(185, 45, 165, 25);
        add(dropDown);


        JButton connectDetails = new JButton("Connect to Database");
        connectDetails.setBackground(Color.blue);
        connectDetails.setForeground(Color.yellow);
        connectDetails.setBounds(130, 160, 190, 30);
        //redPanel.add(connectDetails);
        add(connectDetails);


        JButton ExecuteSQL = new JButton("Execute SQL");
        ExecuteSQL.setBackground(Color.green);
        ExecuteSQL.setForeground(Color.black);
        ExecuteSQL.setBounds(680, 170, 180, 40);
        add(ExecuteSQL);


        JButton windowClear = new JButton("Clear Result Window");
        windowClear.setBackground(Color.yellow);
        windowClear.setForeground(Color.black);
        windowClear.setBounds(300, 530, 300, 40);
        add(windowClear);

        JButton ClearSQL = new JButton("Clear SQL");
        ClearSQL.setBackground(Color.white);
        ClearSQL.setForeground(Color.red);
        ClearSQL.setBounds(440, 170, 180, 40);
        add(ClearSQL);


        JLabel username = new JLabel("USERNAME");
        username.setBounds(60, 80, 165, 25);
        add(username);

        JLabel password = new JLabel("PASSWORD");
        password.setBounds(60, 115, 165, 25);
        add(password);



        props.setBounds(60, 45, 170, 25);
        add(props);



        JPasswordField PasswordText = new JPasswordField(10);
        PasswordText.setBounds(185, 115, 165, 25);
        Border borderPassword = BorderFactory.createLineBorder(Color.blue,1);
        PasswordText.setBorder(borderPassword);
        add(PasswordText);

        square.setBounds(20, 300, 850, 220);
        sqlSquare.setBounds(450, 42, 420, 125);
        add(sqlSquare, BorderLayout.SOUTH);
        add(square, BorderLayout.NORTH);

        JPanel bluePanel = new JPanel();
        bluePanel.setBounds(15,260,880,330);
        bluePanel.setBorder(title3);
        bluePanel.setBackground(Color.white);
//        bluePanel.setPreferredSize(new Dimension(800,250));
//        bluePanel.setLayout(new GridLayout(4,2));
//
//
//        TitledBorder title2;
//        title2 = BorderFactory.createTitledBorder("Enter an SQL Command");
//        title2.setTitleColor(Color.BLUE);

//        //button panel
        JPanel greenPanel = new JPanel();
       greenPanel.setBorder(title2);
       greenPanel.setBounds(430,10,445,210);
        greenPanel.setBackground(Color.white);
//        greenPanel.setPreferredSize(new Dimension(600,250));
//        //greenPanel.setLayout(new FlowLayout(FlowLayout.LEFT));



        redPanel.setBorder(title);
        bluePanel.setBorder(title3);
        greenPanel.setBorder(title2);

        add(redPanel);
        add(greenPanel);
        add(bluePanel);

        windowClear.addActionListener(event -> showTable.setModel(table));

        ClearSQL.addActionListener(event -> querySQL.setText(""));


        //FOLLOW RESULTSETTABLEMODEL.JAVA CODE FROM WEBCOURSES
        connectDetails.addActionListener(event -> {
            boolean usernameMisMatch;
            boolean passwordMisMatch;
            boolean userCredentialsOK = false;
            try {

                 MysqlDataSource dataSource;
                 FileInputStream file = null;
                 Properties properties = new Properties();
                try {

                    file = new FileInputStream(dropDown.getSelectedItem().toString());
                    //file = new FileInputStream("root.properties");
                    properties.load(file);
                    dataSource = new MysqlDataSource();
                    dataSource.setURL(properties.getProperty("MYSQL_DB_URL"));
                    dataSource.setUser(properties.getProperty("MYSQL_DB_USERNAME"));
                    dataSource.setPassword(properties.getProperty("MYSQL_DB_PASSWORD"));


                    char[] arr = dataSource.getPassword().toCharArray();

                    if (input.getText().equals(dataSource.getUser())) {
                        usernameMisMatch = true;
                        userCredentialsOK = true;
                    }
                    if(Arrays.equals(PasswordText.getPassword(), arr))
                    {
                        passwordMisMatch = true;
                        userCredentialsOK = true;
                    }


                    if (userCredentialsOK) {
                        connectionStatus.setForeground(Color.yellow);
                        connectionStatus.setText("CONNECTED TO: " + dataSource.getURL());
                        connection = dataSource.getConnection();

                    } else {

                        connectionStatus.setText("NOT CONNECTED - User Credentials Do Not Match Properties File!");
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR IN DB", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR IN DB", JOptionPane.ERROR_MESSAGE);

            }
        });



        //Make this cleaner
        ExecuteSQL.addActionListener(event -> {
            try {
                String executeQuery = querySQL.getText();

                //if the query is to select, then set query, else set update
                resultSetModel = new ResultSetTableModel(connection);
                if (executeQuery.contains("select")) {

                    resultSetModel.setQuery(executeQuery);
                    showTable.setModel(resultSetModel);

                } else
                    JOptionPane.showMessageDialog(null, "Successful Update...." + resultSetModel.setUpdate(executeQuery) + " rows updated", "Successful Update", JOptionPane.PLAIN_MESSAGE);

            } catch (ClassNotFoundException | SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR IN DB", JOptionPane.ERROR_MESSAGE);
            }
        });

   }

    public static void main(String[] args) {
        MyFrame frame = new MyFrame();



//        frame.add(redPanel);
//        frame.add(greenPanel);
//        frame.add(bluePanel);



    }
}