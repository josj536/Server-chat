package interfaz;

import server.ServerInitializer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerControlGUI extends JFrame {
    private ServerInitializer serverInitializer;
    private JButton startButton;
    private JButton stopButton;

    public ServerControlGUI(ServerInitializer serverInitializer) {
        this.serverInitializer = serverInitializer;
        initialize();
    }

    private void initialize() {
        setTitle("Server Control Panel");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");

        add(startButton);
        add(stopButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });

        updateButtonState();
    }

    private void startServer() {
        SwingUtilities.invokeLater(() -> {
            if (!serverInitializer.isRunning()) {
                serverInitializer.startServer();
                JOptionPane.showMessageDialog(this, "Server started.");
            } else {
                JOptionPane.showMessageDialog(this, "Server is already running.");
            }
            updateButtonState();
        });
    }

    private void stopServer() {
        SwingUtilities.invokeLater(() -> {
            if (serverInitializer.isRunning()) {
                serverInitializer.stopServer();
                JOptionPane.showMessageDialog(this, "Server stopped.");
            } else {
                JOptionPane.showMessageDialog(this, "Server is not running.");
            }
            updateButtonState();
        });
    }

    private void updateButtonState() {
        startButton.setEnabled(!serverInitializer.isRunning());
        stopButton.setEnabled(serverInitializer.isRunning());
    }
}
