package Commands;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TimerTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;


public class PublishLastVideo extends TimerTask{
	/**
	 * Files name
	 */
	private static final String PROPERTIES_FILENAME = "youtube.properties";
	private static final String CHANNELSID_FILENAME = "youtubechannelDatas.json";
	
	private String apiKey;
	private String lastVideoId;
	private List<String> youtubeChannelDatas = new ArrayList<String>();
	JsonParser parser = new JsonParser();
	protected PropertyChangeSupport propertyChangeSupport;

	public PublishLastVideo() throws JsonIOException, JsonSyntaxException, FileNotFoundException{
		
		/**
		 * Setting up the trigger event
		 */
		propertyChangeSupport = new PropertyChangeSupport(this);
		
		/**
		 * Getting the API key for Youtube Api Request
		 */
		Properties properties = new Properties();
        this.apiKey = properties.getProperty("youtube.apikey");
		/**
		 * Reading the json file containing the channel ID and the last video ID
		 */
        try {
            FileInputStream in = new FileInputStream(PROPERTIES_FILENAME); 
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
        }
        this.getYoutubeChannelDatas(parser.parse(new JsonReader(new FileReader(CHANNELSID_FILENAME))).getAsJsonObject());
 
	}
	
	/**
	 * Setting the new videoId and firing the change event
	 * @param videoId : Id of the new video published
	 */
	public void setVideoId(String videoId) {
        String oldVideoId = this.lastVideoId;
        this.lastVideoId = videoId;
        propertyChangeSupport.firePropertyChange("VideoId",oldVideoId, videoId);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

	@Override
	public void run() {
		/**
		 * Setting an hour var, for console error display
		 */
		String hour = Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		hour+=":"+Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
		hour+=":"+Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
		
		URL url;
		InputStream httpResp;
		
		for(String id : this.youtubeChannelDatas){
			try {
				/**
				 * Getting the datas of the Youtube Http Request
				 */
				url = new URL("https://www.googleapis.com/youtube/v3/search?key="+this.apiKey+"&channelId="+id+"&part=snippet,id&order=date&maxResults=1");
				httpResp = url.openStream();
				/**
				 * Parsing it as JSON
				 */
				JsonElement result = parser.parse(new InputStreamReader(httpResp, "UTF-8"));
				JsonObject json = result.getAsJsonObject();
			
				String tmp  = this.getLastVideoId(json.entrySet());
				
				/**
				 * Si la valeur retournée est différente de la valeur stockée dans le fichier, une nouvelle vidéo est sortie
				 */
				if(!tmp.equals(this.lastVideoId)){
					System.out.println(lastVideoId+", new : "+tmp);
					this.setVideoId(tmp);
					//TODO Ecrire le nouvel ID dans le JSON
				}
			} catch (IOException e) {
				System.out.println(String.format("[%s] [Error] The URL failed to return a result, or your request to the Google Youtupe API get rejected", hour));
			} 
		}		
	}
	
	/**
	 * Sûrement sale. A modifier
	 * Iterate over the JSON to get the wanted data : the last video ID
	 * @param entrySet : set of key=>val n the JSON returned by the HTTP request
	 * @return new videoId
	 */
	private String getLastVideoId(Set<Entry<String, JsonElement>> entrySet){
		String videoId = "";
		
		for(Map.Entry<String, JsonElement> entry : entrySet){
			if(entry.getKey().equals("items")){
				JsonArray json = entry.getValue().getAsJsonArray(); 
				for(JsonElement je : json){
					JsonObject id = je.getAsJsonObject().get("id").getAsJsonObject();
					videoId = id.get("videoId").getAsString();
				}
			}
		}
		
		return videoId;
	}
	/**
	 * Get the channel id and the last video id in the json file
	 * @param ids : Json Object containing the datas
	 */
	private void getYoutubeChannelDatas(JsonObject ids){
		Set<Entry<String, JsonElement>> entrySet = ids.entrySet();
		for(Map.Entry<String, JsonElement> entry : entrySet){
			JsonArray json = entry.getValue().getAsJsonArray(); 
			for(JsonElement je : json){
				this.youtubeChannelDatas.add(je.getAsString());
			}
		}
	}

}
