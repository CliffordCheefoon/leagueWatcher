package models;

import java.util.ArrayList;
import java.util.List;

//import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
//import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */


//import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
//import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */


public class discordHook{

 public List<Embed> embeds;
 
 
 
 
 public discordHook(String playerName, String title, String stats,String mainStat,  String actionImage,  String playerIconURL,  String championIcon) {
	super();
	
	this.embeds = new ArrayList<Embed>();
	
	Embed injectedEmbed = new Embed();
	injectedEmbed.title = title;
	injectedEmbed.color = 15258703;
	injectedEmbed.author = new Author(playerName, playerIconURL);
	injectedEmbed.thumbnail = new Thumbnail(championIcon);
	injectedEmbed.footer = new Footer("[Clifford Cheefoon] \n[Eridani Digital Technology]");
	
	ArrayList<Field> fields = new ArrayList<Field>();
	
	Field injectedField = new Field(mainStat, stats);
	fields.add(injectedField);
	
	injectedEmbed.fields = fields;
	
	
	this.embeds.add(injectedEmbed);
	
	
	
}

public class Field{
	 public String name;
	 public String value;
	public Field(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	 
	 
	}



	public class Author{
	 public String name;
	 public String icon_url;
	public Author(String name, String icon_url) {
		super();
		this.name = name;
		this.icon_url = icon_url;
	}
	 
	 
	 
	}

	public class Thumbnail{
	 public String url;

	public Thumbnail(String url) {
		super();
		this.url = url;
	}
	 
	}

	public class Embed{
	 public String title;
	 public int color;
	 public List<Field> fields;
	 public Author author;
	 public Thumbnail thumbnail;
	 public Footer footer;
	}
	
	
	public class Footer{
		public String text;
		
		public Footer(String text) {
			this.text = text;
		}
	}
	
	
}


