package org.example.windows;

import org.example.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ChampionshipParticipantWindow extends JFrame {
    private Connection connection;
    private DefaultTableModel participantTableModel;
    private JTable participantTable;
    private JComboBox<ComboItem> championshipComboBox;
    private JComboBox<ComboItem> dogComboBox;

    public ChampionshipParticipantWindow(Connection connection) {
        this.connection = connection;
        setTitle("Championship Participant Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        participantTableModel = new DefaultTableModel();
        participantTableModel.addColumn("Championship");
        participantTableModel.addColumn("Dog");

        participantTable = new JTable(participantTableModel);
        JScrollPane scrollPane = new JScrollPane(participantTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));

        JLabel championshipLabel = new JLabel("Championship:");
        championshipComboBox = new JComboBox<>();
        JLabel dogLabel = new JLabel("Dog:");
        dogComboBox = new JComboBox<>();

        inputPanel.add(championshipLabel);
        inputPanel.add(championshipComboBox);
        inputPanel.add(dogLabel);
        inputPanel.add(dogComboBox);

        add(inputPanel, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addChampionshipParticipant();
            }
        });
        buttonPanel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateChampionshipParticipant();
            }
        });
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteChampionshipParticipant();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        loadParticipants();
        loadChampionships();
        loadDogs();
    }

    private void loadParticipants() {
        participantTableModel.setRowCount(0);

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT championship.name AS championship_name, dog.name AS dog_name FROM championshipparticipant " +
                            "JOIN championship ON championshipparticipant.champid = championship.id " +
                            "JOIN dog ON championshipparticipant.dogid = dog.id"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String championshipName = resultSet.getString("championship_name");
                String dogName = resultSet.getString("dog_name");

                Object[] row = {championshipName, dogName};

                participantTableModel.addRow(row);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadChampionships() {
        championshipComboBox.removeAllItems();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id, name FROM championship");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                championshipComboBox.addItem(new ComboItem(id, name));
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

    private void addChampionshipParticipant() {
        ComboItem championshipItem = (ComboItem) championshipComboBox.getSelectedItem();
        ComboItem dogItem = (ComboItem) dogComboBox.getSelectedItem();

        if (championshipItem == null || dogItem == null) {
            JOptionPane.showMessageDialog(this, "Please select a championship and a dog");
            return;
        }

        int champId = championshipItem.getId();
        int dogId = dogItem.getId();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO championshipparticipant (champid, dogid) VALUES (?, ?)");
            statement.setInt(1, champId);
            statement.setInt(2, dogId);
            statement.executeUpdate();
            statement.close();

            // Clear the input fields
            championshipComboBox.setSelectedIndex(0);
            dogComboBox.setSelectedIndex(0);

            // Reload the participants
            loadParticipants();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Championship participant added successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteChampionshipParticipant() {
        int selectedRow = participantTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a championship participant to delete");
            return;
        }

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM championshipparticipant WHERE champid = ?");
            statement.setInt(1, selectedRow + 1);
            statement.executeUpdate();
            statement.close();

            // Reload the participants
            loadParticipants();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Championship participant deleted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateChampionshipParticipant() {
        int selectedRow = participantTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a championship participant to update");
            return;
        }

        ComboItem championshipItem = (ComboItem) championshipComboBox.getSelectedItem();
        ComboItem dogItem = (ComboItem) dogComboBox.getSelectedItem();

        if (championshipItem == null || dogItem == null) {
            JOptionPane.showMessageDialog(this, "Please select a championship and a dog");
            return;
        }

        int champId = championshipItem.getId();
        int dogId = dogItem.getId();

        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE championshipparticipant SET champid = ?, dogid = ? WHERE champid = ?");
            statement.setInt(1, champId);
            statement.setInt(2, dogId);
            statement.setInt(3, selectedRow + 1);
            statement.executeUpdate();
            statement.close();

            // Reload the participants
            loadParticipants();

            // Show a success message
            JOptionPane.showMessageDialog(this, "Championship participant updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}