import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame implements ActionListener, ItemListener {
	JTabbedPane tabbedPane;
	JLabel productName, price, qty, stocks;
	JPanel panelPurchasing, panelMain;
	JComboBox comboProductName; // drop downlist
	JTextField textHarga, textQuantity;
	JButton buttonBuy;
	JTable tableData;
	JScrollPane scrollPane; // scroll
	ResultSet rs; // access database
	ResultSet ListOfProduct; // access database
	ResultSet alldata; // access database
	ResultSet getIdProduct; // access database
	DefaultTableModel tableModel = new DefaultTableModel(); // model tablenya
	Vector<Object> rowData;
	int tempQty;

	public Main() {
		form();
		ViewAllData();
		mainPanel();
		setCanvas();
	}

	public void setCanvas() {
		setTitle("Purchasing");
		setSize(800, 200);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public void form() {

		productName = new JLabel("Product Name");
		price = new JLabel("Price");
		qty = new JLabel("Quantity");
		stocks = new JLabel("Stocks: ");

		comboProductName = new JComboBox();
		ListOfProduct = new Connect().ListOfProduct();
		comboProductName.addItem("-Product Name-");
		try {
			while (ListOfProduct.next()) {
				comboProductName.addItem(ListOfProduct.getString("ProductName"));
			}
			comboProductName.addItemListener(this);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		textHarga = new JTextField();
		textHarga.setEditable(false); // tidak bisa di edit

		textQuantity = new JTextField();
		buttonBuy = new JButton("BUY");
		buttonBuy.addActionListener(this);
	}

	public void ViewAllData() {
		alldata = new Connect().getAllTransaction();

		String[] column = { "PurchaseID", "Product Name", "Quantity", "Purchase Date" };
		tableModel.setColumnIdentifiers(column);

		try {
			while (alldata.next()) {
				rowData = new Vector<Object>();
				rowData.add(alldata.getString("PurchaseID"));
				rowData.add(alldata.getString("ProductName"));
				rowData.add(alldata.getString("Qty"));
				rowData.add(alldata.getString("PurchaseDate"));
				tableModel.addRow(rowData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		tableData = new JTable(tableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tableData.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableData.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(tableData);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

	}

	public void mainPanel() {
		add(purchaseForm(), BorderLayout.CENTER);
		add(scrollPane, BorderLayout.EAST);
	}

	public JPanel purchaseForm() {
		panelPurchasing = new JPanel(new GridLayout(4, 2));
		panelPurchasing.add(productName);
		panelPurchasing.add(comboProductName);
		panelPurchasing.add(price);
		panelPurchasing.add(textHarga);
		panelPurchasing.add(qty);
		panelPurchasing.add(textQuantity);
		panelPurchasing.add(stocks);
		panelPurchasing.add(buttonBuy);
		return panelPurchasing;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == buttonBuy) {
			if (comboProductName.getSelectedIndex() == 0) {
				JOptionPane.showMessageDialog(this, "Please choose product what do you wanted");
			} else if (textQuantity.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "Please Input Quantity First before checkout");
			} else if (!textQuantity.getText().matches("[0-9]+")) {
				JOptionPane.showMessageDialog(this, "Input Quantity must numeric");
			} else if (Integer.parseInt(textQuantity.getText()) < 1 || Integer.parseInt(textQuantity.getText()) > tempQty) {
				JOptionPane.showMessageDialog(this, "Quantity must be lower or same as stock");
			}else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				String PurchaseDate = sdf.format(new Date());
				new Connect().insertTransaction(comboProductName.getSelectedIndex(), Integer.parseInt(textQuantity.getText()), PurchaseDate);
				JOptionPane.showMessageDialog(this, "Input Data Successfully");
			}

		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		if (e.getSource() == comboProductName) {
			if (comboProductName.getSelectedIndex() != 0) {
				rs = new Connect().getDataProduct(comboProductName.getSelectedItem().toString());
				try {
					while (rs.next()) {
						textHarga.setText(rs.getString("price"));
						stocks.setText("Stock: " + Integer.toString(rs.getInt("Stock")));
						tempQty = rs.getInt("Stock");
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			} else {
				textHarga.setText(null);
				stocks.setText("Stock: ");
			}
		}
	}

	public static void main(String[] args) {
		new Main();
	}

}
