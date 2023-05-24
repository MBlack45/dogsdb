package org.example;

import org.example.windows.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("MySQL Connection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);

        JLabel usernameLabel = new JLabel("MySQL username:");
        usernameLabel.setBounds(20, 20, 120, 25);
        frame.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 20, 120, 25);
        frame.add(usernameField);

        JLabel passwordLabel = new JLabel("MySQL password:");
        passwordLabel.setBounds(20, 60, 120, 25);
        frame.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 60, 120, 25);
        frame.add(passwordField);

        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(100, 100, 100, 25);
        frame.add(connectButton);

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                String url = "jdbc:mysql://localhost:3306/";
                String databaseName = "dogsdb";

                try {
                    Connection connection = DriverManager.getConnection(url, username, password);
                    System.out.println("Connected to the MySQL server.");

                    if (!databaseExists(connection, databaseName)) {
                        System.out.println("The database does not exist. Creating it...");
                        ScriptRunner.runScript(connection, "init.sql");
                        System.out.println("Database created and initialized successfully");
                        connection.setCatalog(databaseName);
                    } else {
                        System.out.println("The database already exists.");
                    }
                    connection.close();

                    DatabaseConnection dbConnection = new DatabaseConnection(username, password);
                    connection = dbConnection.getConnection();

                    MainWindow mainWindow = new MainWindow(connection);
                    mainWindow.setVisible(true);

                    frame.dispose();
                } catch (SQLException ex) {
                    System.out.println("Failed to connect to the MySQL server: " + ex.getMessage() + "-" + ex.getErrorCode() + "-" + ex.getSQLState());
                }
            }
        });

        frame.setVisible(true);
    }

    private static boolean databaseExists(Connection connection, String databaseName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet resultSet = metadata.getCatalogs();

        while (resultSet.next()) {
            String existingDatabaseName = resultSet.getString(1);
            if (existingDatabaseName.equalsIgnoreCase(databaseName)) {
                return true;
            }
        }

        return false;
    }
}