package com.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.Connection.MyConnection;
import com.Entity.TwitchClip;

public class ClipDAO
{
	// method for inserting a new clip into our database
	public static void insertClip(TwitchClip current, MyConnection instance)
	{
		try(Connection connection = instance.getConnection())
		{
			String query = "INSERT INTO clip (`Title`, `Game`, `OriginalURL`, `EmbeddedURL`, `Description`, `ViewCount`, `Date`) VALUES (?, ?, ?, ?, ?, ?, ?)";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			// binding parameters
			statement.setString(1, current.getTitle());
			statement.setString(2, current.getGame());
			statement.setString(3, current.getOriginalURL());
			statement.setString(4, current.getEmbeddedURL());
			statement.setString(5, current.getDescription());
			statement.setInt(6, current.getViewCount());
			statement.setDate(7, Date.valueOf(current.getDateCreated()));
			
			statement.execute();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	// gets the top 4 clips based on the most views
	public static List<TwitchClip> getMostViewed(MyConnection instance)
	{
		List<TwitchClip> currentList = new ArrayList<>();
		
		try(Connection connection = instance.getConnection())
		{
			String query = "SELECT * FROM clip order by ViewCount desc limit 4";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			ResultSet resultSet = statement.executeQuery();
			
			// adding the top 4 viewed clips
			while (resultSet.next())
			{
				currentList.add(currentClipInfo(resultSet));
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return currentList;
	}

	// gets the top 4 clips based on the best rating
	public static List<TwitchClip> getTopRated(MyConnection instance)
	{
		List<TwitchClip> currentList = new ArrayList<>();
		
		try(Connection connection = instance.getConnection())
		{
			String query = "SELECT * FROM clip order by Rating desc limit 4";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			ResultSet resultSet = statement.executeQuery();
			
			// adding the top 4 viewed clips
			while (resultSet.next())
			{
				currentList.add(currentClipInfo(resultSet));
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return currentList;
	}

	// gets the top 4 clips based on their date created
	public static List<TwitchClip> getMostRecent(MyConnection instance)
	{
		List<TwitchClip> currentList = new ArrayList<>();
		
		try(Connection connection = instance.getConnection())
		{
			String query = "SELECT * FROM clip order by Date desc limit 4";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			ResultSet resultSet = statement.executeQuery();
			
			// adding the top 4 viewed clips
			while (resultSet.next())
			{	
				currentList.add(currentClipInfo(resultSet));
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return currentList;
	}

	// gets the top 4 clips with the most comments
	public static List<TwitchClip> getMostCommment(MyConnection instance)
	{
		List<TwitchClip> currentList = new ArrayList<>();
		
		try(Connection connection = instance.getConnection())
		{
			String query = "SELECT * FROM clip order by TotalComments desc limit 4";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			ResultSet resultSet = statement.executeQuery();
			
			// adding the top 4 viewed clips
			while (resultSet.next())
			{
				currentList.add(currentClipInfo(resultSet));
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return currentList;
	}
	
	// method to extract current clip information from our database
	private static TwitchClip currentClipInfo(ResultSet resultSet) throws SQLException
	{
		TwitchClip temp = new TwitchClip();
		
		temp.setId(resultSet.getInt("ID"));
		temp.setTitle(resultSet.getString("Title"));
		temp.setGame(resultSet.getString("Game"));
		temp.setOriginalURL(resultSet.getString("OriginalURL"));
		temp.setEmbeddedURL(resultSet.getString("EmbeddedURL"));
		temp.setDescription(resultSet.getString("Description"));
		temp.setViewCount(resultSet.getInt("ViewCount"));
		temp.setDateCreated(resultSet.getDate("Date").toLocalDate());
		temp.setUpvotes(resultSet.getInt("UpVotes"));
		temp.setDownvotes(resultSet.getInt("DownVotes"));
		temp.setRating(resultSet.getInt("Rating"));
		temp.setTotalComments(resultSet.getInt("TotalComments"));
		return temp;
	}

	// method to update either the upvotes or downvotes, and the rating
	public static void updateClip(TwitchClip currentClip, String command, MyConnection instance)
	{
		try(Connection connection = instance.getConnection())
		{
			String query = new String();
			
			// condition to decide on the query to execute
			if (command.equals("upvote"))
			{
				query = "UPDATE clip SET `UpVotes`=?, `Rating`=? WHERE `ID`=?";
			}
			else if (command.equals("downvote"))
			{
				query = "UPDATE clip SET `Downvotes`=?, `Rating`=? WHERE `ID`=?";
			}
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			// binding the parameters
			if (command.equals("upvote"))
			{
				statement.setInt(1, currentClip.getUpvotes());
			}
			else if (command.equals("downvote"))
			{
				statement.setInt(1, currentClip.getDownvotes());
			}
			statement.setInt(2, currentClip.getRating());
			statement.setInt(3, currentClip.getId());
			
			statement.execute();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	// method for retrieving a clip based on the ID provided
	public static TwitchClip getClip(int currentID, MyConnection instance)
	{
		try (Connection connection = instance.getConnection())
		{
			String query = "select * from clip where ID=?";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			statement.setInt(1, currentID);
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next())
			{
				return currentClipInfo(resultSet);
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	// method for updating the total comments for the selected clip
	public static void updateClipComment(TwitchClip currentClip, MyConnection instance)
	{
		try(Connection connection = instance.getConnection())
		{
			String query = "UPDATE clip SET `TotalComments`=? WHERE `ID`=?";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			statement.setInt(1, currentClip.getTotalComments());
			statement.setInt(2, currentClip.getId());
			
			statement.execute();
			
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
