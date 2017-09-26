package tcc_metricas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteProvider {
	
		Connection connection;
		Statement statement;
	
	private void createConnection(){
		try {
			Class.forName("org.sqlite.JDBC");			
			connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/thiagog/Desktop/tcc_metricas.db3");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public ResultSet executeQuery(String query) throws SQLException{
		this.createConnection();
		this.statement = connection.createStatement();
		return statement.executeQuery(query);
	}
	
	public boolean execute(String query) throws SQLException{
		this.createConnection();
		this.statement = connection.createStatement();
		return statement.execute(query);
	}

}
