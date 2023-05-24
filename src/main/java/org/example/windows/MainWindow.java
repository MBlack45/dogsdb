package org.example.windows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class MainWindow extends JFrame {
    private ChampionshipWindow championshipWindow;
    private DogWindow dogWindow;
    private IllnessWindow illnessWindow;
    private OwnerWindow ownerWindow;
    private DoctorWindow doctorWindow;
    private ThemesWindow themesWindow;
    private MessageWindow messageWindow;
    private OwnerDogWindow ownerDogWindow;
    private ChampionshipParticipantWindow championshipParticipantWindow;

    private Connection connection;

    public MainWindow(Connection connection) {
        this.connection = connection;
        setTitle("Main Window");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(3, 3, 10, 10));
        setContentPane(contentPane);

        JButton championshipButton = new JButton("Championship Window");
        championshipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChampionshipWindow();
            }
        });
        contentPane.add(championshipButton);

        JButton dogButton = new JButton("Dog Window");
        dogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDogWindow();
            }
        });
        contentPane.add(dogButton);

        JButton illnessButton = new JButton("Illness Window");
        illnessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showIllnessWindow();
            }
        });
        contentPane.add(illnessButton);

        JButton ownerButton = new JButton("Owner Window");
        ownerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOwnerWindow();
            }
        });
        contentPane.add(ownerButton);

        JButton doctorButton = new JButton("Doctor Window");
        doctorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDoctorWindow();
            }
        });
        contentPane.add(doctorButton);

        JButton themesButton = new JButton("Themes Window");
        themesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showThemesWindow();
            }
        });
        contentPane.add(themesButton);

        JButton messageButton = new JButton("Message Window");
        messageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageWindow();
            }
        });
        contentPane.add(messageButton);

        JButton ownerDogButton = new JButton("Owner Dog Window");
        ownerDogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOwnerDogWindow();
            }
        });
        contentPane.add(ownerDogButton);

        JButton championshipParticipantButton = new JButton("Championship Participant Window");
        championshipParticipantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChampionshipParticipantWindow();
            }
        });
        contentPane.add(championshipParticipantButton);
    }

    private void showChampionshipWindow() {
        if (championshipWindow == null) {
            championshipWindow = new ChampionshipWindow(connection);
        }
        championshipWindow.setVisible(true);
    }

    private void showDogWindow() {
        if (dogWindow == null) {
            dogWindow = new DogWindow(connection);
        }
        dogWindow.setVisible(true);
    }

    private void showIllnessWindow() {
        if (illnessWindow == null) {
            illnessWindow = new IllnessWindow(connection);
        }
        illnessWindow.setVisible(true);
    }

    private void showOwnerWindow() {
        if (ownerWindow == null) {
            ownerWindow = new OwnerWindow(connection);
        }
        ownerWindow.setVisible(true);
    }

    private void showDoctorWindow() {
        if (doctorWindow == null) {
            doctorWindow = new DoctorWindow(connection);
        }
        doctorWindow.setVisible(true);
    }

    private void showThemesWindow() {
        if (themesWindow == null) {
            themesWindow = new ThemesWindow(connection);
        }
        themesWindow.setVisible(true);
    }

    private void showMessageWindow() {
        if (messageWindow == null) {
            messageWindow = new MessageWindow(connection);
        }
        messageWindow.setVisible(true);
    }

    private void showOwnerDogWindow() {
        if (ownerDogWindow == null) {
            ownerDogWindow = new OwnerDogWindow(connection);
        }
        ownerDogWindow.setVisible(true);
    }

    private void showChampionshipParticipantWindow() {
        if (championshipParticipantWindow == null) {
            championshipParticipantWindow = new ChampionshipParticipantWindow(connection);
        }
        championshipParticipantWindow.setVisible(true);
    }
}