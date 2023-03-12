/*
Name: Aymen Hasnain
Course: CNT 4714 Spring 2023
Assignment title: Project 3 – A Two-tier Client-Server Application
Date: March 9, 2023
Class: Enterprise Computing
*/

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.GridLayout;
public class MyFrame extends JFrame{

    MyFrame() {


        ImageIcon image = new ImageIcon("logoMySQL.png"); //replace java logo on this


        this.setTitle("SQL Client App - (MJL - CNT 4714 - Spring 2023 - Project 3 - Aymen Hasnain)"); //title of the frame
        this.getContentPane().add(new Server());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closes the frame instead of hiding it
        //resizeable not working consistently
        //this.setResizable(false); //makes the frame not resizable
        setPreferredSize(new Dimension(910, 700));


        //this.setSize(1050, 550); //size of the frame

        //this.setLayout(new FlowLayout(FlowLayout.LEADING));
        this.getContentPane().setBackground(Color.white);

        this.setVisible(true); //makes the frame visible


        this.pack();

        //add bluePanel to frame

    }

}
