/*
 * Student Record System
 * Programmer: Camry S. Macalam
 * Student ID: 1-1981-459
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;

public class student_record_system_macalam extends JFrame {

    DefaultTableModel model;
    JTable table;

    JTextField txtId, txtName, txtGrade;

    public student_record_system_macalam() {
        setTitle("Student Record System - Camry S. Macalam 1-1981-459");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== HEADER (HTML-like) =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 41, 59));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Student Record System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Programmer: Camry S. Macalam | Student ID: 1-1981-459");
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);

        // ===== MAIN CONTENT PANEL =====
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(241, 245, 249));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== FORM PANEL =====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Student"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtName = new JTextField();
        txtGrade = new JTextField();

        addField(formPanel, gbc, 0, "Student ID", txtId);
        addField(formPanel, gbc, 1, "Name", txtName);
        addField(formPanel, gbc, 2, "Grade", txtGrade);

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");

        btnAdd.setBackground(new Color(59, 130, 246));
        btnAdd.setForeground(Color.WHITE);

        btnDelete.setBackground(new Color(220, 38, 38));
        btnDelete.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        formPanel.add(buttonPanel, gbc);

        // ===== TABLE =====
        model = new DefaultTableModel(new String[]{"ID", "Name", "Grade"}, 0);
        table = new JTable(model);
        table.setRowHeight(24);

        JTableHeader th = table.getTableHeader();
        th.setBackground(new Color(30, 41, 59));
        th.setForeground(Color.WHITE);
        th.setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Records"));

        loadCSV();

        // ===== BUTTON LOGIC =====
        btnAdd.addActionListener(e -> {
            model.addRow(new Object[]{
                    txtId.getText(),
                    txtName.getText(),
                    txtGrade.getText()
            });
            txtId.setText("");
            txtName.setText("");
            txtGrade.setText("");
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                model.removeRow(row);
            }
        });

        content.add(formPanel, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    // ===== HELPER METHOD =====
    private void addField(JPanel panel, GridBagConstraints gbc, int x,
                          String labelText, JTextField field) {
        gbc.gridx = x;
        gbc.gridy = 0;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridy = 1;
        field.setPreferredSize(new Dimension(200, 28));
        panel.add(field, gbc);
    }

    // ===== CSV LOAD =====
    private void loadCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader("MOCK_DATA.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                model.addRow(new Object[]{
                        data[0],
                        data[1] + " " + data[2],
                        data[3]
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV file.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new student_record_system_macalam().setVisible(true)
        );
    }
}
