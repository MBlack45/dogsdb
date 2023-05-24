package org.example.windows;

import org.example.ChampionshipTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ChampionshipWindow extends JFrame {
    private JTable championshipTable;
    private ChampionshipTableModel championshipTableModel;
    private Connection connection;
    private JTextField nameField;
    private JTextField dateField;
    private JTextField addressField;
    private JTextField prizesField;

    public ChampionshipWindow(Connection connection) {
        this.connection = connection;
        initializeUI();
        loadChampionships();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Championship Management");
        setLayout(new BorderLayout());

        championshipTableModel = new ChampionshipTableModel();
        championshipTable = new JTable(championshipTableModel);
        JScrollPane scrollPane = new JScrollPane(championshipTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Date:"));
        dateField = new JTextField();
        inputPanel.add(dateField);
        inputPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Prizes:"));
        prizesField = new JTextField();
        inputPanel.add(prizesField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addChampionship();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateChampionship();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteChampionship();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadChampionships() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM championship");

            championshipTableModel.setResultSet(resultSet);

            // Close the result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addChampionship() {
        try {
            String name = nameField.getText();
            String date = dateField.getText();
            String address = addressField.getText();
            String prizes = prizesField.getText();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO championship (name, date, address, prizes) VALUES (?, ?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, date);
            statement.setString(3, address);
            statement.setString(4, prizes);
            statement.executeUpdate();

            // Clear the input fields
            nameField.setText("");
            dateField.setText("");
            addressField.setText("");
            prizesField.setText("");

            // Reload the championships
            loadChampionships();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Championship added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateChampionship() {
        try {
            int selectedRow = championshipTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a championship to update");
                return;
            }

            int championshipId = (int) championshipTableModel.getValueAt(selectedRow, 0); // Assuming the ID is in the first column

            // Get the values from the text fields
            String newName = nameField.getText().trim(); // Trim the input to remove leading/trailing whitespace
            String newAddress = addressField.getText().trim();
            String newPrizes = prizesField.getText().trim();
            String dateString = dateField.getText().trim();
            Date newDate = null;

            if (!dateString.isEmpty()) {
                newDate = Date.valueOf(dateString);
            }

            // Get the existing values from the table modeld
            String currentName = (String) championshipTableModel.getValueAt(selectedRow, 1); // Assuming the name is in the second column
            Date currentDate = (Date) championshipTableModel.getValueAt(selectedRow, 2); // Assuming the date is in the third column
            String currentAddress = (String) championshipTableModel.getValueAt(selectedRow, 3); // Assuming the address is in the fourth column
            String currentPrizes = (String) championshipTableModel.getValueAt(selectedRow, 4); // Assuming the prizes is in the fifth column

            // Use the existing values if the text field is empty
            String updatedName = newName.isEmpty() ? currentName : newName;
            Date updatedDate =  newDate==null ||newDate.toString().isEmpty() ? currentDate : newDate;
            String updatedAddress = newAddress.isEmpty() ? currentAddress : newAddress;
            String updatedPrizes = newPrizes.isEmpty() ? currentPrizes : newPrizes;

            PreparedStatement statement = connection.prepareStatement("UPDATE championship SET name = ?, date = ?, address = ?, prizes = ? WHERE id = ?");
            statement.setString(1, updatedName);
            statement.setDate(2, updatedDate);
            statement.setString(3, updatedAddress);
            statement.setString(4, updatedPrizes);
            statement.setInt(5, championshipId);
            statement.executeUpdate();

            // Clear the input fields
            nameField.setText("");
            addressField.setText("");
            prizesField.setText("");

            // Reload the championships
            loadChampionships();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Championship updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteChampionship() {
        try {
            int selectedRow = championshipTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a championship to delete");
                return;
            }

            String name = (String) championshipTableModel.getValueAt(selectedRow, 1);

            PreparedStatement statement = connection.prepareStatement("DELETE FROM championship WHERE name = ?");
            statement.setString(1, name);
            statement.executeUpdate();

            // Clear the input fields
            nameField.setText("");
            dateField.setText("");
            addressField.setText("");
            prizesField.setText("");

            // Reload the championships
            loadChampionships();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Championship deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}