package org.example.windows;

import org.example.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class ThemesWindow extends JFrame {
    private Connection connection;
    private DefaultTableModel themesTableModel;
    private JTable themesTable;
    private JTextField nameField;
    private JComboBox<ComboItem> ownerComboBox;

    public ThemesWindow(Connection connection) {
        this.connection = connection;
        setTitle("Themes Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        themesTableModel = new DefaultTableModel();
        themesTableModel.addColumn("ID");
        themesTableModel.addColumn("Name");
        themesTableModel.addColumn("Owner");

        themesTable = new JTable(themesTableModel);
        JScrollPane scrollPane = new JScrollPane(themesTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel ownerLabel = new JLabel("Owner:");
        ownerComboBox = new JComboBox<>();

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(ownerLabel);
        inputPanel.add(ownerComboBox);

        add(inputPanel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTheme();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTheme();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTheme();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        loadThemes();
        loadOwners();
    }

    private void loadThemes() {
        themesTableModel.setRowCount(0);

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT themes.id, themes.name, owner.name AS owner_name FROM themes " +
                            "JOIN owner ON themes.ownerid = owner.id"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String ownerName = resultSet.getString("owner_name");

                Vector<Object> row = new Vector<>();
                row.add(id);
                row.add(name);
                row.add(ownerName);

                themesTableModel.addRow(row);
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
                ComboItem item = new ComboItem(id, name);
                ownerComboBox.addItem(item);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTheme() {
        String name = nameField.getText().trim();
        ComboItem ownerItem = (ComboItem) ownerComboBox.getSelectedItem();
        int ownerId = ownerItem.getId();

        if (name.isEmpty() || ownerItem == null) {
            JOptionPane.showMessageDialog(this, "Please enter a name and select an owner");
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO themes (name, ownerid) VALUES (?, ?)");
            statement.setString(1, name);
            statement.setInt(2, ownerId);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            nameField.setText("");
            ownerComboBox.setSelectedIndex(0);

            // Reload the themes
            loadThemes();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Theme added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTheme() {
        int selectedRow = themesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a theme to delete");
            return;
        }

        int themeId = (int) themesTableModel.getValueAt(selectedRow, 0);

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM themes WHERE id = ?");
            statement.setInt(1, themeId);
            statement.executeUpdate();
            statement.close();

            // Reload the themes
            loadThemes();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Theme deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTheme() {
        int selectedRow = themesTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a theme to update");
            return;
        }

        int themeId = (int) themesTableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        ComboItem ownerItem = (ComboItem) ownerComboBox.getSelectedItem();
        int ownerId = ownerItem.getId();

        // Get the existing values from the table model
        String currentName = (String) themesTableModel.getValueAt(selectedRow, 1);
        int currentOwnerId = ownerItem.getId();

        // Use the existing values if the corresponding input fields are empty
        String updatedName = name.isEmpty() ? currentName : name;
        int updatedOwnerId = (ownerId == 0) ? currentOwnerId : ownerId;

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE themes SET name = ?, ownerid = ? WHERE id = ?");
            statement.setString(1, updatedName);
            statement.setInt(2, updatedOwnerId);
            statement.setInt(3, themeId);
            statement.executeUpdate();
            statement.close();

            // Reload the themes
            loadThemes();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Theme updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
