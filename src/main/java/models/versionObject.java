package models;

//import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
//import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */

public class versionObject{
 public N n;
 public String v;
 public String l;
 public String cdn;
 public String dd;
 public String lg;
 public String css;
 public int profileiconmax;
 public Object store;
 
 
 public class N{
	 public String item;
	 public String rune;
	 public String mastery;
	 public String summoner;
	 public String champion;
	 public String profileicon;
	 public String map;
	 public String language;
	 public String sticker;
	}

}

