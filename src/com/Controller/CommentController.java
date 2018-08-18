package com.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import com.Connection.MyConnection;
import com.DAO.ClipDAO;
import com.DAO.CommentDAO;
import com.Entity.Comment;
import com.Entity.TwitchClip;

@ManagedBean
@SessionScoped
public class CommentController
{
	private static MyConnection instance;
	List<Comment> clipComments;
	
	// default constructor
	public CommentController() throws NamingException
	{
		instance = MyConnection.getInstance();
		
		clipComments = new ArrayList<>();
	}
	
	//getter for all the comments associated with a selected clip
	public List<Comment> getClipComments()
	{
		return clipComments;
	}
	
	// method handling the comment post for each clip
	public String addComment(Comment commentPosted)
	{
		// conditions to handle user input
		if (commentPosted.getCommentPost().isEmpty())
		{
			// getting clipID parameter associated with the link
			Map<String, String> param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			int clipID = Integer.parseInt(param.get("currentID"));
			
			// getting the selected clip
			TwitchClip currentClip = ClipDAO.getClip(clipID, instance);
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("twitchClip", currentClip);
			return "clipInfo";
		}
		else if (commentPosted.getAuthor().isEmpty())
		{
			commentPosted.setAuthor("Anonymous");
		}
		
		// getting clipID parameter associated with the link
		Map<String, String> param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		int clipID = Integer.parseInt(param.get("currentID"));
		
		// adding the comment to our database
		CommentDAO.addComment(commentPosted, instance);
		
		// getting the comment ID 
		int commentID = CommentDAO.getCommentID(commentPosted, instance);
		
		// creating the comment and clip pair
		CommentDAO.addPair(clipID, commentID, instance);
		
		// getting the selected clip and updating its comment count
		TwitchClip currentClip = ClipDAO.getClip(clipID, instance);
		currentClip.setTotalComments(currentClip.getTotalComments() + 1);
		ClipDAO.updateClipComment(currentClip, instance);
		
		// passing the parameter
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.put("twitchClip", currentClip);
		
		return "clipInfo";
	}
	
	
	// method to load up the comments associated with the selected clip
	public void loadComments(TwitchClip selectedClip)
	{
		clipComments = CommentDAO.getComments(selectedClip, instance);
	}
}
