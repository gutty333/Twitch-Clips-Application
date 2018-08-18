package com.Entity;

import java.time.LocalDate;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class Comment
{
	private int id;
	private String author;
	private String commentPost;
	private LocalDate datePosted;
	
	public Comment()
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

	public String getAuthor()
	{
		return author;
	}

	public void setAuthor(String author)
	{
		this.author = author;
	}

	public String getCommentPost()
	{
		return commentPost;
	}

	public void setCommentPost(String commentPost)
	{
		this.commentPost = commentPost;
	}

	public LocalDate getDatePosted()
	{
		return datePosted;
	}

	public void setDatePosted(LocalDate datePosted)
	{
		this.datePosted = datePosted;
	}
}
