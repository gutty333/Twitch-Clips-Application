package com.Entity;

import java.time.LocalDate;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class TwitchClip
{
	private int id;
	private String title;
	private String game;
	private String originalURL;
	private String embeddedURL;
	private String description;
	private int viewCount;
	private LocalDate dateCreated;
	private int upvotes;
	private int downvotes;
	private int rating;
	private int totalComments;
	
	// default constructor
	public TwitchClip()
	{
		
	}

	// getters and setters
	public int getId()
	{
		return id;
	}

	
	public void setId(int id)
	{
		this.id = id;
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getOriginalURL()
	{
		return originalURL;
	}


	public void setOriginalURL(String originalURL)
	{
		this.originalURL = originalURL;
	}


	public String getEmbeddedURL()
	{
		return embeddedURL;
	}


	public void setEmbeddedURL(String embeddedURL)
	{
		this.embeddedURL = embeddedURL;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}


	public int getViewCount()
	{
		return viewCount;
	}


	public void setViewCount(int viewCount)
	{
		this.viewCount = viewCount;
	}


	public LocalDate getDateCreated()
	{
		return dateCreated;
	}


	public void setDateCreated(LocalDate dateCreated)
	{
		this.dateCreated = dateCreated;
	}


	public int getUpvotes()
	{
		return upvotes;
	}


	public void setUpvotes(int upvotes)
	{
		this.upvotes = upvotes;
	}


	public int getDownvotes()
	{
		return downvotes;
	}


	public void setDownvotes(int downvotes)
	{
		this.downvotes = downvotes;
	}


	public int getRating()
	{
		return rating;
	}


	public void setRating(int rating)
	{
		this.rating = rating;
	}


	public int getTotalComments()
	{
		return totalComments;
	}


	public void setTotalComments(int totalComments)
	{
		this.totalComments = totalComments;
	}
	
	
	public String getGame()
	{
		return game;
	}

	public void setGame(String game)
	{
		this.game = game;
	}
}
