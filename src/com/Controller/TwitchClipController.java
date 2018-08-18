package com.Controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.json.JSONException;
import org.json.JSONObject;

import com.Connection.MyConnection;
import com.DAO.ClipDAO;
import com.Entity.TwitchClip;

@ManagedBean
@SessionScoped
public class TwitchClipController
{
	private static MyConnection instance;
	
	List<TwitchClip> mostViewed;
	List<TwitchClip> topRated;
	List<TwitchClip> mostRecent;
	List<TwitchClip> mostComment;
	
	// default constructor
	public TwitchClipController() throws NamingException
	{
		mostViewed = new ArrayList<>();
		topRated = new ArrayList<>();
		mostRecent = new ArrayList<>();
		mostComment = new ArrayList<>();
		
		instance = MyConnection.getInstance();
	}
	
	
	// methods to get the top 4 of each category from our collection
	public void mostViewedClips()
	{
		mostViewed = ClipDAO.getMostViewed(instance);
	}
	
	public void topRatedClips()
	{
		topRated = ClipDAO.getTopRated(instance);
	}
	
	public void mostRecentClips()
	{
		mostRecent = ClipDAO.getMostRecent(instance);
	}
	
	public void mostCommentClips()
	{
		mostComment = ClipDAO.getMostCommment(instance);
	}
	
	// getters for the different list of clips
	public List<TwitchClip> getMostViewed()
	{
		return mostViewed;
	}

	public List<TwitchClip> getTopRated()
	{
		return topRated;
	}

	public List<TwitchClip> getMostRecent()
	{
		return mostRecent;
	}

	public List<TwitchClip> getMostComment()
	{
		return mostComment;
	}
	
	// method to add a new clip to our collection
	public String addClip(TwitchClip current)
	{
		try
		{
			// getting all the data associated with this clip
			current = getClipData(current);
			
			// inserting the new clip into our collection
			ClipDAO.insertClip(current, instance);
			
			// redirecting back to the home page
			return "index?faces-redirect=true";
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		// if the URL was not valid we signal an error to the user
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.put("error", "Please provide a valid Twitch Clip URL");
		
		return "addClip?faces-redirect=true&includeViewParams=true";
	}

	/*
	 * method to collect various data for the clip provided
	 * this includes creating the embedded URL
	 * making a request to the API to gather information such as 
	 * view count, game name, title, etc
	 */
	private TwitchClip getClipData(TwitchClip current) throws IOException, JSONException
	{
		// isolating the title from the URL provided
		String clipSlug = getURLTitle(current.getOriginalURL());
		
		String clipURL = new String("https://api.twitch.tv/kraken/clips/" + clipSlug);
		
		// open the connection
		URL obj = new URL(clipURL);
		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
		
		// set the request method
		connection.setRequestMethod("GET");
		
		// adding request headers
		connection.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
		connection.setRequestProperty("Client-ID", "d7q0pctmj4oe4jwjx0oqley18qb800");
		
		// collecting the JSON result
		Scanner input = new Scanner(connection.getInputStream());
		
		StringBuilder jsonResult = new StringBuilder();
		
		while (input.hasNextLine())
		{
			jsonResult.append(input.nextLine());
		}
		
		input.close();
		
		// getting the JSON data for this clip
		JSONObject result = new JSONObject(jsonResult.toString());
		current.setTitle(result.getString("title"));
		current.setGame(result.getString("game"));
		current.setViewCount(result.getInt("views"));

		// handling the date when the clip was created
		// we only want to get the date stamp
		String date = result.get("created_at").toString().substring(0, 10);
		
		// setting our custom format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		// parsing the result to  a localDate object
		LocalDate dateCreated = LocalDate.parse(date,formatter);
		
		// adding the data to our current clip
		current.setDateCreated(dateCreated);
		
		// creating the embedded URL
		String embedURL = new String("https://clips.twitch.tv/embed?clip=" + clipSlug + "&autoplay=false");
		
		current.setEmbeddedURL(embedURL);
		
		return current;
	}

	// method to get the clip title from the URL provided
	private String getURLTitle(String originalURL)
	{
		StringBuilder clipTitle = new StringBuilder();
		
		// we traverse in reverse until hitting the first /
		for (int x = originalURL.length()-1; x >= 0; x--)
		{
			if (originalURL.charAt(x) == '/')
			{
				return clipTitle.reverse().toString();
			}
			else
			{
				clipTitle.append(originalURL.charAt(x));
			}
		}
		
		return clipTitle.toString();
	}

	
	// method to view the information for the selected clip	
	public String clipInformation(TwitchClip selectedClip)
	{
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		
		Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.put("twitchClip", selectedClip);
		
		return "clipInfo";
	}
	

	// method updating the upvote count for the current clip
	public String clipUpvote()
	{
		// getting the parameter associated with the link
		Map<String, String> param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		int currentID = Integer.parseInt(param.get("currentID"));

		// getting the current clip from our collection
		TwitchClip currentClip = ClipDAO.getClip(currentID, instance);
		
		// updating the upvote and rating
		currentClip.setUpvotes(currentClip.getUpvotes() + 1);
		currentClip.setRating(currentClip.getRating() + 1);

		// update the clip in our database
		ClipDAO.updateClip(currentClip, "upvote", instance);
		
		// setting the model attribute for our target page
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		
		Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.put("twitchClip", currentClip);
		
		return "clipInfo";
	}
	
	// method updating the downvote count for the current clip
	public String clipDownvote()
	{
		// getting the parameter associated with the link
		Map<String, String> param = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		int currentID = Integer.parseInt(param.get("currentID"));

		// getting the current clip from our collection
		TwitchClip currentClip = ClipDAO.getClip(currentID, instance);
		
		// updating the downvote and rating
		currentClip.setDownvotes(currentClip.getDownvotes() - 1);
		currentClip.setRating(currentClip.getRating() - 1);
		
		// update the clip in our database
		ClipDAO.updateClip(currentClip, "downvote", instance);
		
		// setting the model attribute for our target page
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> requestMap = externalContext.getRequestMap();
		requestMap.put("twitchClip", currentClip);
		
		return "clipInfo";
	}
}