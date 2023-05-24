package org.example.windows;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OwnerWindow extends JFrame {
    private JTable ownerTable;
    private DefaultTableModel ownerTableModel;
    private Connection connection;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField addressField;

    public OwnerWindow(Connection connection) {
        this.connection = connection;
        initializeUI();
        loadOwners();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Owner Management");
        setLayout(new BorderLayout());

        ownerTableModel = new DefaultTableModel();
        ownerTable = new JTable(ownerTableModel);
        JScrollPane scrollPane = new JScrollPane(ownerTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        inputPanel.add(addressField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOwner();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOwner();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOwner();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadOwners() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM owner");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing table data
            ownerTableModel.setRowCount(0);
            ownerTableModel.setColumnCount(0);

            // Add column names to the table model
            for (int i = 1; i <= columnCount; i++) {
                ownerTableModel.addColumn(metaData.getColumnName(i));
            }

            // Add rows to the table model
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                ownerTableModel.addRow(rowData);
            }

            // Close the result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOwner() {
        try {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO owner (name, phone, address) VALUES (?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, address);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            nameField.setText("");
            phoneField.setText("");
            addressField.setText("");

            // Reload the owners
            loadOwners();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Owner added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateOwner() {
        try {
            int selectedRow = ownerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an owner to update");
                return;
            }

            int ownerId = (int) ownerTableModel.getValueAt(selectedRow, 0); // Assuming the ID is in the first column

            // Get the values from the text fields
            String newName = nameField.getText().trim(); // Trim the input to remove leading/trailing whitespace
            String newPhone = phoneField.getText().trim();
            String newAddress = addressField.getText().trim();

            // Get the existing values from the table model
            String currentName = (String) ownerTableModel.getValueAt(selectedRow, 1); // Assuming the name is in the second column
            String currentPhone = (String) ownerTableModel.getValueAt(selectedRow, 2); // Assuming the phone is in the third column
            String currentAddress = (String) ownerTableModel.getValueAt(selectedRow, 3); // Assuming the address is in the fourth column

            // Use the existing values if the text field is empty
            String updatedName = newName.isEmpty() ? currentName : newName;
            String updatedPhone = newPhone.isEmpty() ? currentPhone : newPhone;
            String updatedAddress = newAddress.isEmpty() ? currentAddress : newAddress;

            PreparedStatement statement = connection.prepareStatement("UPDATE owner SET name = ?, phone = ?, address = ? WHERE id = ?");
            statement.setString(1, updatedName);
            statement.setString(2, updatedPhone);
            statement.setString(3, updatedAddress);
            statement.setInt(4, ownerId);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            nameField.setText("");
            phoneField.setText("");
            addressField.setText("");

            // Reload the owners
            loadOwners();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Owner updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOwner() {
        try {
            int selectedRow = ownerTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an owner to delete");
                return;
            }

            int ownerId = (int) ownerTableModel.getValueAt(selectedRow, 0); // Assuming the ID is in the first column

            PreparedStatement statement = connection.prepareStatement("DELETE FROM owner WHERE id = ?");
            statement.setInt(1, ownerId);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            nameField.setText("");
            phoneField.setText("");
            addressField.setText("");

            // Reload the owners
            loadOwners();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Owner deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}