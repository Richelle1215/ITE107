import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class CalculatorApp2 {

    private static final ArrayList<String> history = loadHistory();
    private static JFrame mainFrame;
    private static final String HISTORY_FILE = "calculator_history.dat";

    public static void main(String[] args) {
        mainFrame = new JFrame("Calculator Menu");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(300, 150);
        mainFrame.setLayout(new FlowLayout());
        mainFrame.getContentPane().setBackground(Color.BLACK);

        JButton computeButton = new JButton("Compute");
        computeButton.setBackground(new Color(204, 153, 255));
        computeButton.setForeground(Color.BLACK);
        computeButton.addActionListener(e -> openCalculator());

        JButton historyButton = new JButton("View History");
        historyButton.setBackground(new Color(204, 153, 255));
        historyButton.setForeground(Color.BLACK);
        historyButton.addActionListener(e -> showHistory());

        JButton exitButton = new JButton("Exit");
        exitButton.setBackground(new Color(204, 153, 255));
        exitButton.setForeground(Color.BLACK);
        exitButton.addActionListener(e -> exitApp());

        mainFrame.add(computeButton);
        mainFrame.add(historyButton);
        mainFrame.add(exitButton);
        mainFrame.setVisible(true);
    }

    private static void openCalculator() {
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.BLACK);

        JTextField display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.BLACK);
        display.setForeground(Color.WHITE);
        frame.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));
        buttonPanel.setBackground(Color.BLACK);

        String[] buttonTexts = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "History", "Clear"
        };

        for (String text : buttonTexts) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setForeground(Color.WHITE);

            if (text.matches("[0-9\\.]") || text.equals("Clear") || text.equals("History")) {
                button.setBackground(Color.decode(text.equals("Clear") ? "#FBCC58" : (text.equals("History") ? "#507041" : "#f89DAE")));
            } else {
                button.setBackground(Color.decode(text.equals("=") ? "#FBCC58" : "#507041"));
            }

            buttonPanel.add(button);
            button.addActionListener(e -> {
                String command = e.getActionCommand();
                if (command.equals("=")) {
                    try {
                        String expression = display.getText();
                        double result = evaluateExpression(expression);
                        String resultText = (result == (long) result) ? String.valueOf((long) result) : String.valueOf(result);
                        history.add(expression + " = " + resultText);
                        display.setText(resultText);
                    } catch (Exception ex) {
                        display.setText("Error");
                        JOptionPane.showMessageDialog(null, "Invalid expression: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (command.equals("History")) {
                    showHistory();
                } else if (command.equals("Clear")) {
                    display.setText("");
                } else {
                    display.setText(display.getText() + command);
                }
            });
        }
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void showHistory() {
        StringBuilder historyString = new StringBuilder();
        for (String entry : history) {
            historyString.append(entry).append("\n");
        }
        JOptionPane.showMessageDialog(null, historyString.toString(), "Calculation History", JOptionPane.INFORMATION_MESSAGE);
    }

    private static double evaluateExpression(String expression) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return Double.parseDouble(engine.eval(expression).toString());
        } catch (ScriptException | NullPointerException e) {
            return 0; // Or handle the error as appropriate
        }
    }

    private static void saveHistory() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE))) {
            oos.writeObject(history);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving history: " + e.getMessage() + "\n" + e.getStackTrace(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace to the console for debugging
        }
    }

    private static ArrayList<String> loadHistory() {
        ArrayList<String> loadedHistory = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HISTORY_FILE))) {
            loadedHistory = (ArrayList<String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace(); //Uncomment for debugging
        }
        return loadedHistory;
    }

    private static void exitApp() {
        saveHistory();
        System.exit(0);
    }
}
