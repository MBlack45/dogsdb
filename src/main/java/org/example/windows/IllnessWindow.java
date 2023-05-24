package org.example.windows;

import org.example.ComboItem;
import org.example.IllnessStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class IllnessWindow extends JFrame {
    private Connection connection;
    private DefaultTableModel illnessTableModel;
    private JTable illnessTable;
    private JTextField descriptionField;
    private JTextField recommendationsField;
    private JComboBox<ComboItem> dogIdComboBox;
    private JComboBox<ComboItem> doctorIdComboBox;
    private JComboBox<IllnessStatus> statusComboBox;
    private JTextField dateField;

    public IllnessWindow(Connection connection) {
        this.connection = connection;
        setTitle("Illness Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        illnessTableModel = new DefaultTableModel();
        illnessTableModel.addColumn("ID");
        illnessTableModel.addColumn("Dog Name");
        illnessTableModel.addColumn("Doctor Name");
        illnessTableModel.addColumn("Description");
        illnessTableModel.addColumn("Recommendations");
        illnessTableModel.addColumn("Status");
        illnessTableModel.addColumn("Date");

        illnessTable = new JTable(illnessTableModel);
        JScrollPane scrollPane = new JScrollPane(illnessTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(8, 2));

        JLabel dogIdLabel = new JLabel("Dog:");
        dogIdComboBox = new JComboBox<>();
        JLabel doctorIdLabel = new JLabel("Doctor:");
        doctorIdComboBox = new JComboBox<>();
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField();
        JLabel recommendationsLabel = new JLabel("Recommendations:");
        recommendationsField = new JTextField();
        JLabel statusLabel = new JLabel("Status:");
        statusComboBox = new JComboBox<>(IllnessStatus.values());
        JLabel dateLabel = new JLabel("Date:");
        dateField = new JTextField();

        inputPanel.add(dogIdLabel);
        inputPanel.add(dogIdComboBox);
        inputPanel.add(doctorIdLabel);
        inputPanel.add(doctorIdComboBox);
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionField);
        inputPanel.add(recommendationsLabel);
        inputPanel.add(recommendationsField);
        inputPanel.add(statusLabel);
        inputPanel.add(statusComboBox);
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);

        add(inputPanel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addIllness();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateIllness();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteIllness();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        loadIllnesses();
        loadDogs();
        loadDoctors();
    }

    private void loadIllnesses() {
        illnessTableModel.setRowCount(0);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT i.id, d.name AS dogName, dt.name AS doctorName, i.description, i.recomendations, i.status, i.date FROM illnesslist i INNER JOIN dog d ON i.dogId = d.id INNER JOIN doctor dt ON i.doctorId = dt.id");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String dogName = resultSet.getString("dogName");
                String doctorName = resultSet.getString("doctorName");
                String description = resultSet.getString("description");
                String recommendations = resultSet.getString("recomendations");
                String statusValue = resultSet.getString("status");
                java.sql.Date date = resultSet.getDate("date");

                IllnessStatus status = mapStatus(statusValue);

                Vector<Object> row = new Vector<>();
                row.add(id);
                row.add(dogName);
                row.add(doctorName);
                row.add(description);
                row.add(recommendations);
                row.add(status);
                row.add(date);

                illnessTableModel.addRow(row);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDogs() {
        dogIdComboBox.removeAllItems();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM dog");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                ComboItem item = new ComboItem(id, name);
                dogIdComboBox.addItem(item);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDoctors() {
        doctorIdComboBox.removeAllItems();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM doctor");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                ComboItem item = new ComboItem(id, name);
                doctorIdComboBox.addItem(item);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private IllnessStatus mapStatus(String statusValue) {
        for (IllnessStatus status : IllnessStatus.values()) {
            if (status.toString().equals(statusValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + statusValue);
    }

    private void addIllness() {
        String description = descriptionField.getText().trim();
        String recommendations = recommendationsField.getText().trim();
        IllnessStatus status = (IllnessStatus) statusComboBox.getSelectedItem();
        String date = dateField.getText().trim();
        int dogId = ((ComboItem) dogIdComboBox.getSelectedItem()).getId();
        int doctorId = ((ComboItem) doctorIdComboBox.getSelectedItem()).getId();

        if (description.isEmpty() || recommendations.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all required fields");
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO illnesslist (dogId, doctorId, description, recomendations, status, date) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setInt(1, dogId);
            statement.setInt(2, doctorId);
            statement.setString(3, description);
            statement.setString(4, recommendations);
            statement.setString(5, status.toString());
            statement.setDate(6, java.sql.Date.valueOf(date));
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            descriptionField.setText("");
            recommendationsField.setText("");
            statusComboBox.setSelectedIndex(0);
            dateField.setText("");

            // Reload the illnesses
            loadIllnesses();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Illness added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteIllness() {
        int selectedRow = illnessTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an illness to delete");
            return;
        }

        int illnessId = (int) illnessTableModel.getValueAt(selectedRow, 0);

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM illnesslist WHERE id = ?");
            statement.setInt(1, illnessId);
            statement.executeUpdate();
            statement.close();

            // Reload the illnesses
            loadIllnesses();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Illness deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateIllness() {
        int selectedRow = illnessTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an illness to update");
            return;
        }

        int illnessId = (int) illnessTableModel.getValueAt(selectedRow, 0);
        String description = descriptionField.getText().trim();
        String recommendations = recommendationsField.getText().trim();
        IllnessStatus status = (IllnessStatus) statusComboBox.getSelectedItem();
        String dateString = dateField.getText().trim();
        Date date = null;

        if (!dateString.isEmpty()) {
            date = Date.valueOf(dateString);
        }
        int dogId = ((ComboItem) dogIdComboBox.getSelectedItem()).getId();
        int doctorId = ((ComboItem) doctorIdComboBox.getSelectedItem()).getId();

        // Get the existing values from the table model
        String currentDescription = (String) illnessTableModel.getValueAt(selectedRow, 3);
        String currentRecommendations = (String) illnessTableModel.getValueAt(selectedRow, 4);
        IllnessStatus currentStatus = (IllnessStatus) illnessTableModel.getValueAt(selectedRow, 5);
        Date currentDate = (Date) illnessTableModel.getValueAt(selectedRow, 6);

        // Use the existing values if the corresponding input fields are empty
        String updatedDescription = description.isEmpty() ? currentDescription : description;
        String updatedRecommendations = recommendations.isEmpty() ? currentRecommendations : recommendations;
        IllnessStatus updatedStatus = (status == null) ? currentStatus : status;
        Date updatedDate = date == null || date.toString().isEmpty() ? currentDate : date;

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE illnesslist SET dogId = ?, doctorId = ?, description = ?, recomendations = ?, status = ?, date = ? WHERE id = ?");
            statement.setInt(1, dogId);
            statement.setInt(2, doctorId);
            statement.setString(3, updatedDescription);
            statement.setString(4, updatedRecommendations);
            statement.setString(5, updatedStatus.toString());
            statement.setDate(6, updatedDate);
            statement.setInt(7, illnessId);
            statement.executeUpdate();
            statement.close();

            // Reload the illnesses
            loadIllnesses();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Illness updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}