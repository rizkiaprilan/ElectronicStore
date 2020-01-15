import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.net.httpserver.Authenticator.Result;

public class Connect {
	Connection con;
	Statement st;
	ResultSet rs;

	public Connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbName = "purchase";
			String dbUser = "root";
			String dbPass = "12345678";
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, dbUser, dbPass);
			st = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet ListOfProduct() {
		try {
			rs = st.executeQuery("SELECT ProductName FROM products Order BY ProductName");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	public ResultSet getDataProduct(String ProductName) {
		try {
			String Query = String.format("SELECT * FROM products WHERE ProductName='%s'", ProductName);
			rs = st.executeQuery(Query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultSet getDataProductId(int ProductId) {
		try {
			String Query = String.format("SELECT * FROM products WHERE ProductId='%s'", ProductId);
			rs = st.executeQuery(Query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	

	public ResultSet getAllTransaction() {
		try {
			rs = st.executeQuery("SELECT purchase.PurchaseID, products.ProductName, purchase.Qty, purchase.PurchaseDate "
					+ "FROM purchase INNER JOIN products ON purchase.ProductID = products.ProductID");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
	
	public void insertTransaction(int productid, int qty, String date) {
		int stocknow = 0;
		int finalStock = 0;
		rs = new Connect().getDataProductId(productid);
		try {
			while (rs.next()) {
				stocknow = rs.getInt("Stock");
			}
			finalStock = stocknow - qty;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		 
		String insert = String.format("INSERT INTO purchase VALUES ('%d','%d','%s')", productid,qty,date);
		String updateQty = String.format("UPDATE products SET Stock = '%d' WHERE ProductId = '%d'",finalStock,productid);
		try {
			st.executeUpdate(insert);
			st.executeUpdate(updateQty);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
