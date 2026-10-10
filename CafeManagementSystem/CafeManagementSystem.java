
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class CafeManagementSystem extends JFrame {

    private JComboBox<String> itemBox;
    private JTextField quantityField;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel subtotalLabel, taxLabel, totalLabel;
    private JTextArea receiptArea;

    private final String[] items = {
        "Coffee", "Tea", "Sandwich",
        "Burger", "Pizza", "French Fries",
        "Cold Coffee", "Juice"
    };

    private final double[] prices = {
        30, 15, 50, 80, 120, 60, 70, 40
    };

    private final DecimalFormat df = new DecimalFormat("0.00");

    public CafeManagementSystem() {

        setTitle("Cafe Management System");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel(
            "CAFE MANAGEMENT SYSTEM",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(120, 60, 20));
        add(title, BorderLayout.NORTH);

        createMenuPanel();
        createCartPanel();
        createReceiptPanel();
        createBottomPanel();
    }

    private void createMenuPanel() {

        JPanel menuPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        menuPanel.setBorder(
            BorderFactory.createTitledBorder("Menu and Order")
        );

        itemBox = new JComboBox<>(items);
        quantityField = new JTextField("1");

        JButton addButton = new JButton("Add Item");
        JButton removeButton = new JButton("Remove Selected");

        menuPanel.add(new JLabel("Select Food:"));
        menuPanel.add(itemBox);

        menuPanel.add(new JLabel("Enter Quantity:"));
        menuPanel.add(quantityField);

        menuPanel.add(addButton);
        menuPanel.add(removeButton);

        JButton clearButton = new JButton("Clear Cart");
        JButton showPriceButton = new JButton("Show Price");

        menuPanel.add(clearButton);
        menuPanel.add(showPriceButton);

        menuPanel.add(new JLabel("Cafe:"));
        menuPanel.add(new JLabel("Fresh Food & Drinks"));

        add(menuPanel, BorderLayout.WEST);

        addButton.addActionListener(e -> addItem());
        removeButton.addActionListener(e -> removeItem());
        clearButton.addActionListener(e -> clearCart());
        showPriceButton.addActionListener(e -> showPrice());
    }

    private void createCartPanel() {

        String[] columns = {
            "Item", "Price", "Quantity", "Amount"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(
            BorderFactory.createTitledBorder("Shopping Cart")
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createReceiptPanel() {

        receiptArea = new JTextArea(8, 20);
        receiptArea.setEditable(false);
        receiptArea.setFont(
            new Font("Monospaced", Font.PLAIN, 13)
        );

        JScrollPane receiptScroll = new JScrollPane(receiptArea);
        receiptScroll.setBorder(
            BorderFactory.createTitledBorder("Bill Receipt")
        );

        add(receiptScroll, BorderLayout.EAST);
    }

    private void createBottomPanel() {

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        JPanel amountPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 8)
        );

        subtotalLabel = new JLabel("Subtotal: Rs. 0.00");
        taxLabel = new JLabel("Tax (5%): Rs. 0.00");
        totalLabel = new JLabel("Total: Rs. 0.00");

        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        amountPanel.add(subtotalLabel);
        amountPanel.add(taxLabel);
        amountPanel.add(totalLabel);

        JPanel buttonPanel = new JPanel();

        JButton checkoutButton = new JButton("Generate Bill");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(checkoutButton);
        buttonPanel.add(exitButton);

        bottomPanel.add(amountPanel);
        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        checkoutButton.addActionListener(e -> generateBill());

        exitButton.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
            );

            if (answer == JOptionPane.YES_OPTION) {
                dispose();
            }
        });
    }

    private void addItem() {

        try {
            int quantity = Integer.parseInt(
                quantityField.getText().trim()
            );

            if (quantity <= 0) {
                showMessage("Quantity must be greater than zero!");
                return;
            }

            int index = itemBox.getSelectedIndex();

            String itemName = items[index];
            double price = prices[index];
            double amount = price * quantity;

            boolean found = false;

            for (int i = 0; i < tableModel.getRowCount(); i++) {

                String existingItem =
                    tableModel.getValueAt(i, 0).toString();

                if (existingItem.equals(itemName)) {

                    int oldQuantity = Integer.parseInt(
                        tableModel.getValueAt(i, 2).toString()
                    );

                    int newQuantity = oldQuantity + quantity;

                    tableModel.setValueAt(
                        newQuantity, i, 2
                    );

                    tableModel.setValueAt(
                        df.format(price * newQuantity), i, 3
                    );

                    found = true;
                    break;
                }
            }

            if (!found) {
                tableModel.addRow(new Object[] {
                    itemName,
                    df.format(price),
                    quantity,
                    df.format(amount)
                });
            }

            updateTotals();
            quantityField.setText("1");

        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid whole number!");
        }
    }

    private void removeItem() {

        int selectedRow = cartTable.getSelectedRow();

        if (selectedRow == -1) {
            showMessage("Please select an item to remove!");
            return;
        }

        tableModel.removeRow(selectedRow);
        updateTotals();
    }

    private void clearCart() {

        tableModel.setRowCount(0);

        receiptArea.setText("");
        quantityField.setText("1");

        updateTotals();
    }

    private void showPrice() {

        int index = itemBox.getSelectedIndex();

        showMessage(
            items[index] + " price: Rs. "
            + df.format(prices[index])
        );
    }

    private double calculateSubtotal() {

        double subtotal = 0;

        for (int i = 0; i < tableModel.getRowCount(); i++) {

            int quantity = Integer.parseInt(
                tableModel.getValueAt(i, 2).toString()
            );

            double price = Double.parseDouble(
                tableModel.getValueAt(i, 1).toString()
            );

            subtotal += price * quantity;
        }

        return subtotal;
    }

    private void updateTotals() {

        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.05;
        double total = subtotal + tax;

        subtotalLabel.setText(
            "Subtotal: Rs. " + df.format(subtotal)
        );

        taxLabel.setText(
            "Tax (5%): Rs. " + df.format(tax)
        );

        totalLabel.setText(
            "Total: Rs. " + df.format(total)
        );
    }

    private void generateBill() {

        if (tableModel.getRowCount() == 0) {
            showMessage("Please add items before generating a bill!");
            return;
        }

        double subtotal = calculateSubtotal();
        double tax = subtotal * 0.05;
        double total = subtotal + tax;

        StringBuilder receipt = new StringBuilder();

        receipt.append("===== CAFE BILL =====\n");
        receipt.append("Welcome to Our Cafe!\n");
        receipt.append("---------------------\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {

            String name =
                tableModel.getValueAt(i, 0).toString();

            String quantity =
                tableModel.getValueAt(i, 2).toString();

            String amount =
                tableModel.getValueAt(i, 3).toString();

            receipt.append(name)
                   .append(" x ")
                   .append(quantity)
                   .append("\nRs. ")
                   .append(amount)
                   .append("\n");
        }

        receipt.append("---------------------\n");
        receipt.append(
            "Subtotal: Rs. " + df.format(subtotal) + "\n"
        );
        receipt.append(
            "Tax 5%: Rs. " + df.format(tax) + "\n"
        );
        receipt.append(
            "TOTAL: Rs. " + df.format(total) + "\n"
        );
        receipt.append("---------------------\n");
        receipt.append("Thank You! Visit Again!");

        receiptArea.setText(receipt.toString());

        JOptionPane.showMessageDialog(
            this,
            "Bill generated successfully!\nTotal: Rs. "
            + df.format(total),
            "Checkout",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showMessage(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "Cafe Management",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            CafeManagementSystem cafe =
                new CafeManagementSystem();

            cafe.setVisible(true);
        });
    }
}
