package com.Connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

// Connection Set up
public class MyConnection
{
	private static MyConnection instance;
	private DataSource dataSource;
	private String jndiTarget = "java:comp/env/myConnection";
	
	// Singleton Design Pattern
	public static MyConnection getInstance() throws NamingException
	{
		if (instance == null)
		{
			instance = new MyConnection();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	// constructor to initialize our connection pool
	public MyConnection() throws NamingException
	{
		dataSource = getDataSource();
	}

	// method to target our context configuration
	private DataSource getDataSource() throws NamingException
	{
		Context context = new InitialContext();
		
		DataSource theDataSource = (DataSource) context.lookup(jndiTarget);
		
		return theDataSource;
	}
	
	// method to get a Connection
	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
}
