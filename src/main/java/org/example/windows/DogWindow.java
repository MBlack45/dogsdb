package org.example.windows;

import org.example.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class DogWindow extends JFrame {
    private Connection connection;
    private DefaultTableModel dogTableModel;
    private JTable dogTable;
    private JTextField nameField;
    private JTextField breedField;
    private JTextField ageField;
    private JTextField descriptionField;
    private JTextField imgURLField;
    private JComboBox<ComboItem> ownerComboBox;

    public DogWindow(Connection connection) {
        this.connection = connection;
        setTitle("Dog Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        dogTableModel = new DefaultTableModel();
        dogTableModel.addColumn("ID");
        dogTableModel.addColumn("Name");
        dogTableModel.addColumn("Breed");
        dogTableModel.addColumn("Age");
        dogTableModel.addColumn("Description");
        dogTableModel.addColumn("Image URL");
        dogTableModel.addColumn("Owner");

        dogTable = new JTable(dogTableModel);
        JScrollPane scrollPane = new JScrollPane(dogTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel breedLabel = new JLabel("Breed:");
        breedField = new JTextField();
        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField();
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField();
        JLabel imgURLLabel = new JLabel("Image URL:");
        imgURLField = new JTextField();
        JLabel ownerLabel = new JLabel("Owner:");
        ownerComboBox = new JComboBox<>();

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(breedLabel);
        inputPanel.add(breedField);
        inputPanel.add(ageLabel);
        inputPanel.add(ageField);
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionField);
        inputPanel.add(imgURLLabel);
        inputPanel.add(imgURLField);
        inputPanel.add(ownerLabel);
        inputPanel.add(ownerComboBox);

        add(inputPanel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDog();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDog();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDog();
            }
        });
        buttonPanel.add(deleteButton);


        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        loadDogs();
        loadOwners();
    }

    private void loadDogs() {
        dogTableModel.setRowCount(0);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT d.*, o.name AS owner_name FROM dog d LEFT JOIN ownerdog od ON d.id = od.dogid LEFT JOIN owner o ON od.ownerid = o.id");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String breed = resultSet.getString("breed");
                int age = resultSet.getInt("age");
                String description = resultSet.getString("description");
                String imgURL = resultSet.getString("imgURL");
                String owner = resultSet.getString("owner_name");

                Vector<Object> row = new Vector<>();
                row.add(id);
                row.add(name);
                row.add(breed);
                row.add(age);
                row.add(description);
                row.add(imgURL);
                row.add(owner);

                dogTableModel.addRow(row);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadOwners() {
        ownerComboBox.removeAllItems();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM owner");
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

    private void addDog() {
        String name = nameField.getText().trim();
        String breed = breedField.getText().trim();
        String ageText = ageField.getText().trim();
        String description = descriptionField.getText().trim();
        String imgURL = imgURLField.getText().trim();
        ComboItem selectedOwner = (ComboItem) ownerComboBox.getSelectedItem();

        if (name.isEmpty() || breed.isEmpty() || ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all required fields");
            return;
        }

        try {
            int age = Integer.parseInt(ageText);

            PreparedStatement statement = connection.prepareStatement("INSERT INTO dog (name, breed, age, description, imgURL) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, name);
            statement.setString(2, breed);
            statement.setInt(3, age);
            statement.setString(4, description);
            statement.setString(5, imgURL);
            statement.executeUpdate();
            statement.close();

            int dogId = getLatestDogId();

            if (dogId != -1) {
                PreparedStatement ownerDogStatement = connection.prepareStatement("INSERT INTO ownerdog (dogid, ownerid) VALUES (?, ?)");
                ownerDogStatement.setInt(1, dogId);
                ownerDogStatement.setInt(2, selectedOwner.getId());
                ownerDogStatement.executeUpdate();
                ownerDogStatement.close();
            }

            // Clear the input fields
            nameField.setText("");
            breedField.setText("");
            ageField.setText("");
            descriptionField.setText("");
            imgURLField.setText("");
            ownerComboBox.setSelectedIndex(0);

            // Reload the dogs
            loadDogs();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Dog added successfully");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getLatestDogId() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) FROM dog");
        ResultSet resultSet = statement.executeQuery();
        int dogId = -1;

        if (resultSet.next()) {
            dogId = resultSet.getInt(1);
        }

        statement.close();
        return dogId;
    }

    private void deleteDog() {
        int selectedRow = dogTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dog to delete");
            return;
        }

        int dogId = (int) dogTableModel.getValueAt(selectedRow, 0);

        try {

            PreparedStatement participantStatement = connection.prepareStatement("DELETE FROM championshipparticipant WHERE dogid = ?");
            participantStatement.setInt(1, dogId);
            participantStatement.executeUpdate();
            participantStatement.close();

            PreparedStatement ownerDogStatement = connection.prepareStatement("DELETE FROM ownerdog WHERE dogid = ?");
            ownerDogStatement.setInt(1, dogId);
            ownerDogStatement.executeUpdate();
            ownerDogStatement.close();

            PreparedStatement illnessStatement = connection.prepareStatement("DELETE FROM illnesslist WHERE dogid = ?");
            illnessStatement.setInt(1, dogId);
            illnessStatement.executeUpdate();
            illnessStatement.close();

            PreparedStatement statement = connection.prepareStatement("DELETE FROM dog WHERE id = ?");
            statement.setInt(1, dogId);
            statement.executeUpdate();
            statement.close();

            // Reload the dogs
            loadDogs();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Dog deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateDog() {
        int selectedRow = dogTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a dog to update");
            return;
        }

        int dogId = (int) dogTableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        String breed = breedField.getText().trim();
        String ageText = ageField.getText().trim();
        String description = descriptionField.getText().trim();
        String imgURL = imgURLField.getText().trim();
        ComboItem selectedOwner = (ComboItem) ownerComboBox.getSelectedItem();

        if (name.isEmpty()) {
            name = (String) dogTableModel.getValueAt(selectedRow, 1);
        }
        if (breed.isEmpty()) {
            breed = (String) dogTableModel.getValueAt(selectedRow, 2);
        }
        if (ageText.isEmpty()) {
            ageText = String.valueOf(dogTableModel.getValueAt(selectedRow, 3));
        }
        if (description.isEmpty()) {
            description = (String) dogTableModel.getValueAt(selectedRow, 4);
        }
        if (imgURL.isEmpty()) {
            imgURL = (String) dogTableModel.getValueAt(selectedRow, 5);
        }

        try {
            int age = Integer.parseInt(ageText);

            PreparedStatement statement = connection.prepareStatement("UPDATE dog SET name = ?, breed = ?, age = ?, description = ?, imgURL = ? WHERE id = ?");
            statement.setString(1, name);
            statement.setString(2, breed);
            statement.setInt(3, age);
            statement.setString(4, description);
            statement.setString(5, imgURL);
            statement.setInt(6, dogId);
            statement.executeUpdate();
            statement.close();

            PreparedStatement ownerDogStatement = connection.prepareStatement("UPDATE ownerdog SET ownerid = ? WHERE dogid = ?");
            ownerDogStatement.setInt(1, selectedOwner.getId());
            ownerDogStatement.setInt(2, dogId);
            ownerDogStatement.executeUpdate();
            ownerDogStatement.close();

            // Reload the dogs
            loadDogs();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Dog updated successfully");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}