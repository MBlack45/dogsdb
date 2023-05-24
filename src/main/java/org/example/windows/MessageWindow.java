package org.example.windows;

import org.example.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class MessageWindow extends JFrame {
    private Connection connection;
    private DefaultTableModel messageTableModel;
    private JTable messageTable;
    private JTextField textField;
    private JComboBox<ComboItem> ownerComboBox;
    private JComboBox<ComboItem> themeComboBox;

    public MessageWindow(Connection connection) {
        this.connection = connection;
        setTitle("Message Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        messageTableModel = new DefaultTableModel();
        messageTableModel.addColumn("ID");
        messageTableModel.addColumn("Text");
        messageTableModel.addColumn("Owner");
        messageTableModel.addColumn("Theme");

        messageTable = new JTable(messageTableModel);
        JScrollPane scrollPane = new JScrollPane(messageTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));

        JLabel textLabel = new JLabel("Text:");
        textField = new JTextField();
        JLabel ownerLabel = new JLabel("Owner:");
        ownerComboBox = new JComboBox<>();
        JLabel themeLabel = new JLabel("Theme:");
        themeComboBox = new JComboBox<>();

        inputPanel.add(textLabel);
        inputPanel.add(textField);
        inputPanel.add(ownerLabel);
        inputPanel.add(ownerComboBox);
        inputPanel.add(themeLabel);
        inputPanel.add(themeComboBox);

        add(inputPanel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMessage();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMessage();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMessage();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        loadMessages();
        loadOwners();
        loadThemes();
    }

    private void loadMessages() {
        messageTableModel.setRowCount(0);

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT message.id, message.text, owner.name AS owner_name, themes.name AS theme_name FROM message " +
                            "JOIN owner ON message.ownerid = owner.id " +
                            "JOIN themes ON message.themeid = themes.id"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String text = resultSet.getString("text");
                String ownerName = resultSet.getString("owner_name");
                String themeName = resultSet.getString("theme_name");

                Vector<Object> row = new Vector<>();
                row.add(id);
                row.add(text);
                row.add(ownerName);
                row.add(themeName);

                messageTableModel.addRow(row);
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

    private void loadThemes() {
        themeComboBox.removeAllItems();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM themes");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                themeComboBox.addItem(new ComboItem(id, name));
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMessage() {
        String text = textField.getText().trim();
        ComboItem ownerItem = (ComboItem) ownerComboBox.getSelectedItem();
        ComboItem themeItem = (ComboItem) themeComboBox.getSelectedItem();
        int ownerId = ownerItem.getId();
        int themeId = themeItem.getId();

        if (text.isEmpty() || ownerItem == null || themeItem == null) {
            JOptionPane.showMessageDialog(this, "Please enter a text, select an owner, and select a theme");
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO message (text, ownerid, themeid) VALUES (?, ?, ?)");
            statement.setString(1, text);
            statement.setInt(2, ownerId);
            statement.setInt(3, themeId);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            textField.setText("");
            ownerComboBox.setSelectedIndex(0);
            themeComboBox.setSelectedIndex(0);

            // Reload the messages
            loadMessages();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Message added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage() {
        int selectedRow = messageTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message to delete");
            return;
        }

        int messageId = (int) messageTableModel.getValueAt(selectedRow, 0);

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM message WHERE id = ?");
            statement.setInt(1, messageId);
            statement.executeUpdate();
            statement.close();

            // Reload the messages
            loadMessages();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Message deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateMessage() {
        int selectedRow = messageTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message to update");
            return;
        }

        int messageId = (int) messageTableModel.getValueAt(selectedRow, 0);
        String text = textField.getText().trim();
        ComboItem ownerItem = (ComboItem) ownerComboBox.getSelectedItem();
        ComboItem themeItem = (ComboItem) themeComboBox.getSelectedItem();
        int ownerId = ownerItem.getId();
        int themeId = themeItem.getId();

        // Get the existing values from the table model
        String currentText = (String) messageTableModel.getValueAt(selectedRow, 1);
        int currentOwnerId = ownerItem.getId();
        int currentThemeId = themeItem.getId();

        // Use the existing values if the corresponding input fields are empty
        String updatedText = text.isEmpty() ? currentText : text;
        int updatedOwnerId = (ownerId == 0) ? currentOwnerId : ownerId;
        int updatedThemeId = (themeId == 0) ? currentThemeId : themeId;

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE message SET text = ?, ownerid = ?, themeid = ? WHERE id = ?");
            statement.setString(1, updatedText);
            statement.setInt(2, updatedOwnerId);
            statement.setInt(3, updatedThemeId);
            statement.setInt(4, messageId);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            textField.setText("");
            ownerComboBox.setSelectedIndex(0);
            themeComboBox.setSelectedIndex(0);

            // Reload the messages
            loadMessages();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Message updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
