package models;

public class summoner {
	
	public String id;
	public String accountId;
	public String puuid;
	public String name;
	public int profileIconId;
	public long revisionDate;
	public int summonerLevel;
	
	
	
	
	
	
	public summoner(String id, String accountId, String puuid, String name, int profileIconId, long revisionDate,
			int summonerLevel) {
		super();
		this.id = id;
		this.accountId = accountId;
		this.puuid = puuid;
		this.name = name;
		this.profileIconId = profileIconId;
		this.revisionDate = revisionDate;
		this.summonerLevel = summonerLevel;
	}






	@Override
	public String toString() {
		return "summoner [id=" + id + ", accountId=" + accountId + ", puuid=" + puuid + ", name=" + name
				+ ", profileIconId=" + profileIconId + ", revisionDate=" + revisionDate + ", summonerLevel="
				+ summonerLevel + "]";
	}
	
	
	
	
	
	
	
	

}
