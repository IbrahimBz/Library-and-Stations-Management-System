package org.example;

import org.stations.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));


    }
}