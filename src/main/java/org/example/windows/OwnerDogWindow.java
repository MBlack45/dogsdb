package org.example.windows;

import org.example.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OwnerDogWindow extends JFrame {
    private Connection connection;
    private DefaultTableModel ownerDogTableModel;
    private JTable ownerDogTable;
    private JComboBox<ComboItem> dogComboBox;
    private JComboBox<ComboItem> ownerComboBox;

    public OwnerDogWindow(Connection connection) {
        this.connection = connection;
        setTitle("Owner Dog Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        ownerDogTableModel = new DefaultTableModel();
        ownerDogTableModel.addColumn("Dog");
        ownerDogTableModel.addColumn("Owner");

        ownerDogTable = new JTable(ownerDogTableModel);
        JScrollPane scrollPane = new JScrollPane(ownerDogTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));

        JLabel dogLabel = new JLabel("Dog:");
        dogComboBox = new JComboBox<>();
        JLabel ownerLabel = new JLabel("Owner:");
        ownerComboBox = new JComboBox<>();

        inputPanel.add(dogLabel);
        inputPanel.add(dogComboBox);
        inputPanel.add(ownerLabel);
        inputPanel.add(ownerComboBox);

        add(inputPanel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOwnerDog();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOwnerDog();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOwnerDog();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        loadOwnerDogs();
        loadDogs();
        loadOwners();
    }

    private void loadOwnerDogs() {
        ownerDogTableModel.setRowCount(0);

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT dog.name AS dog_name, owner.name AS owner_name FROM ownerdog " +
                            "JOIN dog ON ownerdog.dogid = dog.id " +
                            "JOIN owner ON ownerdog.ownerid = owner.id"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String dogName = resultSet.getString("dog_name");
                String ownerName = resultSet.getString("owner_name");

                Object[] row = {dogName, ownerName};

                ownerDogTableModel.addRow(row);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDogs() {
        dogComboBox.removeAllItems();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM dog");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                dogComboBox.addItem(new ComboItem(id, name));
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOwners() {
        ownerComboBox.removeAllItems();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM owner");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                ownerComboBox.addItem(new ComboItem(id, name));
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addOwnerDog() {
        ComboItem dogItem = (ComboItem) dogComboBox.getSelectedItem();
        ComboItem ownerItem = (ComboItem) ownerComboBox.getSelectedItem();

        if (dogItem == null || ownerItem == null) {
            JOptionPane.showMessageDialog(this, "Please select a dog and an owner");
            return;
        }

        int dogId = dogItem.getId();
        int ownerId = ownerItem.getId();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO ownerdog (dogid, ownerid) VALUES (?, ?)");
            statement.setInt(1, dogId);
            statement.setInt(2, ownerId);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            dogComboBox.setSelectedIndex(0);
            ownerComboBox.setSelectedIndex(0);

            // Reload the owner dogs
            loadOwnerDogs();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Owner dog added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteOwnerDog() {
        int selectedRow = ownerDogTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an owner dog to delete");
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM ownerdog WHERE dogid = ?");
            statement.setInt(1, selectedRow + 1);
            statement.executeUpdate();
            statement.close();

            // Reload the owner dogs
            loadOwnerDogs();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Owner dog deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateOwnerDog() {
        int selectedRow = ownerDogTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an owner dog to update");
            return;
        }

        ComboItem dogItem = (ComboItem) dogComboBox.getSelectedItem();
        ComboItem ownerItem = (ComboItem) ownerComboBox.getSelectedItem();

        if (dogItem == null || ownerItem == null) {
            JOptionPane.showMessageDialog(this, "Please select a dog and an owner");
            return;
        }

        int dogId = dogItem.getId();
        int ownerId = ownerItem.getId();

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE ownerdog SET dogid = ?, ownerid = ? WHERE dogid = ?");
            statement.setInt(1, dogId);
            statement.setInt(2, ownerId);
            statement.setInt(3, selectedRow + 1);
            statement.executeUpdate();
            statement.close();

            // Reload the owner dogs
            loadOwnerDogs();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Owner dog updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}