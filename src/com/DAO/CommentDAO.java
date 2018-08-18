package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.Connection.MyConnection;
import com.Entity.Comment;
import com.Entity.TwitchClip;

public class CommentDAO
{
	// method for adding a new comment to our database
	public static void addComment(Comment commentPosted, MyConnection instance)
	{
		try (Connection connection = instance.getConnection())
		{
			String query = "INSERT INTO `comment` (`Author`, `Post`) VALUES (?, ?)";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			statement.setString(1, commentPosted.getAuthor());
			statement.setString(2, commentPosted.getCommentPost());
			
			statement.execute();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	// method to retrieve an id based on the comment information provided
	public static int getCommentID(Comment commentPosted, MyConnection instance)
	{
		try(Connection connection = instance.getConnection())
		{
			String query = "select * from comment where Author=? and Post=?";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			statement.setString(1, commentPosted.getAuthor());
			statement.setString(2, commentPosted.getCommentPost());
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next())
			{
				return resultSet.getInt("ID");
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return -1;
	}

	// method for recording the comment and clip pair
	public static void addPair(int clipID, int commentID, MyConnection instance)
	{
		try(Connection connection = instance.getConnection())
		{
			String query = "INSERT INTO `clip_comment` (`ClipID`, `CommentID`) VALUES (?, ?)";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			statement.setInt(1, clipID);
			statement.setInt(2, commentID);
			
			statement.execute();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	// method for retrieve all the comments associated with the provided clip
	public static List<Comment> getComments(TwitchClip selectedClip, MyConnection instance)
	{
		List<Comment> clipComments = new ArrayList<>();
		
		try(Connection connection = instance.getConnection())
		{
			String query = "SELECT * FROM comment inner join clip_comment " + 
					"on clip_comment.ClipID = ? " + 
					"where clip_comment.CommentID = comment.ID"
					+ " order by comment.Date desc;";
			
			PreparedStatement statement = connection.prepareStatement(query);
			
			statement.setInt(1, selectedClip.getId());
			
			ResultSet resultSet = statement.executeQuery();
			
			while (resultSet.next())
			{				
				clipComments.add(getComment(resultSet));
			}
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return clipComments;
	}

	// method for collecting comment data
	private static Comment getComment(ResultSet resultSet) throws SQLException
	{
		Comment temp = new Comment();
		
		temp.setId(resultSet.getInt("ID"));
		temp.setAuthor(resultSet.getString("Author"));
		temp.setCommentPost(resultSet.getString("Post"));
		temp.setDatePosted(resultSet.getDate("Date").toLocalDate());
		return temp;
	}
}
