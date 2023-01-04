package ru.fixer;

import ru.fixer.frame.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.LogManager;

public class MainApplication {
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(
                    MainApplication.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e);
        }
        JFrame mainFrame = new MainFrame();
    }
}
