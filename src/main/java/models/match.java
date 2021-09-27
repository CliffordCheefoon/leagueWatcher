package models;

import java.util.List;

//import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
//import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */

public class match{
	 public Metadata metadata;
	 public Info info;
	 
	 

public class Metadata{
 public String dataVersion;
 public String matchId;
 public List<String> participants;
}

public class StatPerks{
 public int defense;
 public int flex;
 public int offense;
}

public class Selection{
 public int perk;
 public int var1;
 public int var2;
 public int var3;
}

public class Style{
 public String description;
 public List<Selection> selections;
 public int style;
}

public class Perks{
 public StatPerks statPerks;
 public List<Style> styles;
}

public class Participant{
 public int assists;
 public int baronKills;
 public int bountyLevel;
 public int champExperience;
 public int champLevel;
 public int championId;
 public String championName;
 public int championTransform;
 public int consumablesPurchased;
 public int damageDealtToBuildings;
 public int damageDealtToObjectives;
 public int damageDealtToTurrets;
 public int damageSelfMitigated;
 public int deaths;
 public int detectorWardsPlaced;
 public int doubleKills;
 public int dragonKills;
 public boolean firstBloodAssist;
 public boolean firstBloodKill;
 public boolean firstTowerAssist;
 public boolean firstTowerKill;
 public boolean gameEndedInEarlySurrender;
 public boolean gameEndedInSurrender;
 public int goldEarned;
 public int goldSpent;
 public String individualPosition;
 public int inhibitorKills;
 public int inhibitorTakedowns;
 public int inhibitorsLost;
 public int item0;
 public int item1;
 public int item2;
 public int item3;
 public int item4;
 public int item5;
 public int item6;
 public int itemsPurchased;
 public int killingSprees;
 public int kills;
 public String lane;
 public int largestCriticalStrike;
 public int largestKillingSpree;
 public int largestMultiKill;
 public int longestTimeSpentLiving;
 public int magicDamageDealt;
 public int magicDamageDealtToChampions;
 public int magicDamageTaken;
 public int neutralMinionsKilled;
 public int nexusKills;
 public int nexusLost;
 public int nexusTakedowns;
 public int objectivesStolen;
 public int objectivesStolenAssists;
 public int participantId;
 public int pentaKills;
 public Perks perks;
 public int physicalDamageDealt;
 public int physicalDamageDealtToChampions;
 public int physicalDamageTaken;
 public int profileIcon;
 public String puuid;
 public int quadraKills;
 public String riotIdName;
 public String riotIdTagline;
 public String role;
 public int sightWardsBoughtInGame;
 public int spell1Casts;
 public int spell2Casts;
 public int spell3Casts;
 public int spell4Casts;
 public int summoner1Casts;
 public int summoner1Id;
 public int summoner2Casts;
 public int summoner2Id;
 public String summonerId;
 public int summonerLevel;
 public String summonerName;
 public boolean teamEarlySurrendered;
 public int teamId;
 public String teamPosition;
 public int timeCCingOthers;
 public int timePlayed;
 public int totalDamageDealt;
 public int totalDamageDealtToChampions;
 public int totalDamageShieldedOnTeammates;
 public int totalDamageTaken;
 public int totalHeal;
 public int totalHealsOnTeammates;
 public int totalMinionsKilled;
 public int totalTimeCCDealt;
 public int totalTimeSpentDead;
 public int totalUnitsHealed;
 public int tripleKills;
 public int trueDamageDealt;
 public int trueDamageDealtToChampions;
 public int trueDamageTaken;
 public int turretKills;
 public int turretTakedowns;
 public int turretsLost;
 public int unrealKills;
 public int visionScore;
 public int visionWardsBoughtInGame;
 public int wardsKilled;
 public int wardsPlaced;
 public boolean win;
}

public class Ban{
 public int championId;
 public int pickTurn;
}

public class Baron{
 public boolean first;
 public int kills;
}

public class Champion{
 public boolean first;
 public int kills;
}

public class Dragon{
 public boolean first;
 public int kills;
}

public class Inhibitor{
 public boolean first;
 public int kills;
}

public class RiftHerald{
 public boolean first;
 public int kills;
}

public class Tower{
 public boolean first;
 public int kills;
}

public class Objectives{
 public Baron baron;
 public Champion champion;
 public Dragon dragon;
 public Inhibitor inhibitor;
 public RiftHerald riftHerald;
 public Tower tower;
}

public class Team{
 public List<Ban> bans;
 public Objectives objectives;
 public int teamId;
 public boolean win;
}

public class Info{
 public long gameCreation;
 public int gameDuration;
 public long gameId;
 public String gameMode;
 public String gameName;
 public long gameStartTimestamp;
 public String gameType;
 public String gameVersion;
 public int mapId;
 public List<Participant> participants;
 public String platformId;
 public int queueId;
 public List<Team> teams;
 public String tournamentCode;
}

	}





