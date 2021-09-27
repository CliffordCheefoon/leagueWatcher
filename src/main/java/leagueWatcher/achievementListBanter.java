package leagueWatcher;

import java.util.ArrayList;
import java.util.Random;

public class achievementListBanter {
	
	
	
	
	
	
	public String getPentakillString() {
		
		ArrayList<String> dataSet = new ArrayList<String>();
		
		Random rand = new Random();
		
		
		
		dataSet.add("Cheeky bastard doing big things");
		dataSet.add("Ha, gotem ");
		
		int n = rand.nextInt(dataSet.size());
		
		
		
		return dataSet.get(n);
		
		
		
	}
	
	
public String getQuadrakillString() {
		
		ArrayList<String> dataSet = new ArrayList<String>();
		
		Random rand = new Random();
		
		
		
		dataSet.add("Close, but no cigar");
		dataSet.add("Your not that guy buddy");
		
		int n = rand.nextInt(dataSet.size());
		return dataSet.get(n);
		
		
		
	}



public String getBigCSString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	Random rand = new Random();
	
	
	
	dataSet.add("MMOONNNNEEYYYYYYYY");
	dataSet.add("Last hitting god");
	
	int n = rand.nextInt(dataSet.size());
	return dataSet.get(n);
	
	
}


public String getKillsString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	
	Random rand = new Random();
	
	
	dataSet.add("You are the new kill leader");
	
	int n = rand.nextInt(dataSet.size());
	return dataSet.get(n);
	
	
}



public String getInhibString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	Random rand = new Random();
	
	
	
	dataSet.add("Shinyyyyyyyy :3");
	int n = rand.nextInt(dataSet.size());
	
	return dataSet.get(n);
	
	
}


public String getWardsString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	Random rand = new Random();
	
	
	
	dataSet.add("It's nice to see around corners");
	
	int n = rand.nextInt(dataSet.size());
	return dataSet.get(n);
	
	
}

public String getTurrentString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	Random rand = new Random();
	
	
	
	dataSet.add("SMASH!!!");
	
	int n = rand.nextInt(dataSet.size());
	return dataSet.get(n);
	
	
}

public String getDragonString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	Random rand = new Random();
	
	
	
	dataSet.add("Dragon Genocide");
	
	int n = rand.nextInt(dataSet.size());
	return dataSet.get(n);
	
	
}

public String getBaronString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	Random rand = new Random();
	
	
	
	dataSet.add("Dripped in purple");
	int n = rand.nextInt(dataSet.size());
	
	
	return dataSet.get(n);
	
	
}

public String getPotionString() {
	
	ArrayList<String> dataSet = new ArrayList<String>();
	
	Random rand = new Random();
	
	
	
	dataSet.add("Potion Master");
	int n = rand.nextInt(dataSet.size());
	
	
	return dataSet.get(n);
	
	
}

}
