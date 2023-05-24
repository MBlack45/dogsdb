package org.example.windows;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DoctorWindow extends JFrame {
    private JTable doctorTable;
    private DefaultTableModel doctorTableModel;
    private Connection connection;
    private JTextField nameField;
    private JTextField phoneField;
    private JComboBox<String> qualificationComboBox;

    public DoctorWindow(Connection connection) {
        this.connection = connection;
        initializeUI();
        loadDoctors();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Doctor Management");
        setLayout(new BorderLayout());

        doctorTableModel = new DefaultTableModel();
        doctorTable = new JTable(doctorTableModel);
        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Qualification:"));
        qualificationComboBox = new JComboBox<>(new String[]{"Терапевт", "Хірург", "Дерматолог", "Невропатолог", "Гастроінтеролог"});
        inputPanel.add(qualificationComboBox);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDoctor();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDoctor();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDoctor();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadDoctors() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM doctor");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Clear existing table data
            doctorTableModel.setRowCount(0);
            doctorTableModel.setColumnCount(0);

            // Add column names to the table model
            for (int i = 1; i <= columnCount; i++) {
                doctorTableModel.addColumn(metaData.getColumnName(i));
            }

            // Add rows to the table model
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                doctorTableModel.addRow(rowData);
            }

            // Close the result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addDoctor() {
        try {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String qualification = (String) qualificationComboBox.getSelectedItem();

            PreparedStatement statement = connection.prepareStatement("INSERT INTO doctor (name, phone, qualification) VALUES (?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, qualification);
            statement.executeUpdate();

            // Clear the input fields
            nameField.setText("");
            phoneField.setText("");

            // Reload the doctors
            loadDoctors();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Doctor added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDoctor() {
        try {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor to update");
                return;
            }

            int doctorId = (int) doctorTableModel.getValueAt(selectedRow, 0);

            // Get the values from the text fields
            String newName = nameField.getText().trim();
            String newPhone = phoneField.getText().trim();
            String newQualification = (String) qualificationComboBox.getSelectedItem();

            // Get the existing values from the table model
            String currentName = (String) doctorTableModel.getValueAt(selectedRow, 1);
            String currentPhone = (String) doctorTableModel.getValueAt(selectedRow, 2);
            String currentQualification = (String) doctorTableModel.getValueAt(selectedRow, 3);

            // Use the existing values if the text field is empty
            String updatedName = newName.isEmpty() ? currentName : newName;
            String updatedPhone = newPhone.isEmpty() ? currentPhone : newPhone;
            String updatedQualification = newQualification.isEmpty() ? currentQualification : newQualification;

            PreparedStatement statement = connection.prepareStatement("UPDATE doctor SET name = ?, phone = ?, qualification = ? WHERE id = ?");
            statement.setString(1, updatedName);
            statement.setString(2, updatedPhone);
            statement.setString(3, updatedQualification);
            statement.setInt(4, doctorId);
            statement.executeUpdate();

            // Clear the input fields
            nameField.setText("");
            phoneField.setText("");

            // Reload the doctors
            loadDoctors();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Doctor updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteDoctor() {
        try {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a doctor to delete");
                return;
            }

            int doctorId = (int) doctorTableModel.getValueAt(selectedRow, 0);

            PreparedStatement statement = connection.prepareStatement("DELETE FROM doctor WHERE id = ?");
            statement.setInt(1, doctorId);
            statement.executeUpdate();

            // Clear the input fields
            nameField.setText("");
            phoneField.setText("");

            // Reload the doctors
            loadDoctors();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Doctor deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}