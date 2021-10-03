package leagueWatcher;

import com.google.cloud.firestore.Firestore;
import models.match;
import models.summoner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.apache.http.client.ClientProtocolException;

public class mainFunction {

	static String API_KEY;
	static String DISCORD_WEBHOOK_URL;

	static String PROFILE_IMAGE_PROVIDER_VERSION_PLACEHOLDER = "{PROFILE_IMAGE_PROVIDER_VERSION_PLACEHOLDER}";
	static String CHAMPION_IMAGE_PROVIDER_VERSION__PLACEHOLDER = "{CHAMPION_IMAGE_PROVIDER_VERSION__PLACEHOLDER}";

	static String LEAGUE_PROFILE_ENDPOINT = "https://ddragon.leagueoflegends.com/cdn/"
			+ PROFILE_IMAGE_PROVIDER_VERSION_PLACEHOLDER + "/img/profileicon/";
	static String LEAGUE_CHAMP_ENDPOINT = "http://ddragon.leagueoflegends.com/cdn/"
			+ CHAMPION_IMAGE_PROVIDER_VERSION__PLACEHOLDER + "/img/champion/";

	final static String VERSION_LOOKUP_URL = "https://ddragon.leagueoflegends.com/realms/na.json";

	final static String SUMMONER_LIST_DB_NAME = "summonerList";
	final static String PREVIOUS_MATCH_LIST_DB_NAME = "previousMatchList";
	final static String RUN_LOG_DB_NAME = "runRecord";

	/*
	 * EVENTS TO ACHKNOWLEDGE ARAM and CLASSIC only Pentakill QuadraKill Big CS
	 * goals 250 more than 15 kills with less than 6 deaths Kill participation
	 * higher than 80% Killing 3 or more inhibitors Wards placed 30 turrents killed
	 * 4 dragons killed 4 killed 2 barons potion master : 10 or more potions
	 * champion trolls: master yi, teemo,
	 * 
	 */

	public void main() {
		// TODO Auto-generated method stub

		API_KEY = System.getenv("RIOT_API_KEY");
		DISCORD_WEBHOOK_URL = System.getenv("DISCORD_WEBHOOK_URL");

		if (API_KEY == null) {
			System.err.println("Cannot get RIOT_API_KEY from environment variable");
			return;
		}

		if (DISCORD_WEBHOOK_URL == null) {
			System.err.println("Cannot get DISCORD_WEBHOOK_URL from environment variable");
			return;
		}

		final Firestore db = new database().getDatabaseConnection();

		if (db == null) {
			System.err.println("Database Connection Failed to start");
			return;
		}

		helperObject HELPEROBJECT = new helperObject();

		HELPEROBJECT.setVersioning();
		System.out.println("Versioning data: \n LEAGUE_PROFILE_ENDPOINT: " + LEAGUE_PROFILE_ENDPOINT
				+ "\n LEAGUE_CHAMP_ENDPOINT:" + LEAGUE_CHAMP_ENDPOINT);
		HELPEROBJECT.runRecord(db);

		final CopyOnWriteArrayList<summoner> summonersAL = new CopyOnWriteArrayList<summoner>();

		HELPEROBJECT.loadSummoners(db, summonersAL);

		CopyOnWriteArrayList<String> previousMatches = HELPEROBJECT.getPreviousmatchesFromDatabase(db);

		// List<summoner> summonersList = summonersAL;

		final CopyOnWriteArrayList<String> lastMatches = new CopyOnWriteArrayList<String>();

		System.out.println("summonersList count: " + summonersAL.size());

		List<summoner> ds = summonersAL;

		ds.parallelStream().forEach(new Consumer<summoner>() {
			public void accept(summoner r) {

				lastMatches.addAll(new helperObject().getMatchesForSummoner(r));

			}

		});

		final ArrayList<String> sanitizedLastMatches = new ArrayList<String>();

		lastMatches.forEach(new Consumer<String>() {
			public void accept(String r) {

				if (!sanitizedLastMatches.contains(r)) {

					sanitizedLastMatches.add(r);

				}

			}
		});

		System.out.println("Sanitized Match List:" + sanitizedLastMatches.size());

		final CopyOnWriteArrayList<String> deltaMatches = new CopyOnWriteArrayList<String>();

		for (int sanitizedCount = 0; sanitizedCount < sanitizedLastMatches.size(); sanitizedCount++) {

			int matchingId = 0;

			for (int previousCount = 0; previousCount < previousMatches.size(); previousCount++) {

				if (sanitizedLastMatches.get(sanitizedCount).equals(previousMatches.get(previousCount))) {

					matchingId++;
				}

			}

			if (matchingId == 0) {
				deltaMatches.add(sanitizedLastMatches.get(sanitizedCount));
			}

		}

		System.out.println("Discovered new matches count: " + deltaMatches.size());

		if (deltaMatches.size() == 0) {
			System.out.println("No match changes detected, Exiting...");
			return;
		}

		new helperObject().clearPreviousMatchesDatabase(db);

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final CopyOnWriteArrayList<match> newMatches = new CopyOnWriteArrayList<match>();

		List<String> tempMatch = deltaMatches;

		tempMatch.parallelStream().forEach(new Consumer<String>() {
			public void accept(String matchId) {

				newMatches.add(new helperObject().getmatchData(matchId));

			}
		});

		List<match> newMatchesList = newMatches;

		newMatchesList.parallelStream().forEach(new Consumer<match>() {
			public void accept(match match) {

				for (int x = 0; x < match.info.participants.size(); x++) {

					String qUser = match.info.participants.get(x).puuid;

					for (int sumIndex = 0; sumIndex < summonersAL.size(); sumIndex++) {

						if (summonersAL.get(sumIndex).puuid.equals(qUser)) {

							CopyOnWriteArrayList<String> badges = new CopyOnWriteArrayList<String>();
							String title = "";
							boolean mainFound = false;

							// check pentakill
							if (match.info.participants.get(x).pentaKills > 0) {

								badges.add(match.info.participants.get(x).pentaKills + " PentaKill");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getPentakillString();
								}

							}

							// check quadra

							if (match.info.participants.get(x).quadraKills > 0) {

								badges.add(match.info.participants.get(x).quadraKills + " QuadraKill");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getQuadrakillString();
								}

							}

							// check CS

							if (match.info.participants.get(x).totalMinionsKilled >= 250) {

								badges.add(match.info.participants.get(x).totalMinionsKilled + " CS");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getBigCSString();
								}

							}

							// check CS

							if (match.info.participants.get(x).kills >= 15
									&& match.info.participants.get(x).deaths <= 5) {

								badges.add(match.info.participants.get(x).kills + " - "
										+ match.info.participants.get(x).deaths + " K/D");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getKillsString();
								}

							}

							// check inhib

							if (match.info.participants.get(x).inhibitorKills >= 3) {

								badges.add(match.info.participants.get(x).inhibitorKills + " Inhib Kills");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getInhibString();
								}

							}

							// check wards

							if (match.info.participants.get(x).wardsPlaced >= 30) {

								badges.add(match.info.participants.get(x).wardsPlaced + " Wards Placed");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getWardsString();
								}

							}

							// check turretKills

							if (match.info.participants.get(x).turretKills >= 5) {
								badges.add(match.info.participants.get(x).turretKills + " Turret Kills");

								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getTurrentString();
								}

							}

							// check dragon kills

							if (match.info.participants.get(x).dragonKills >= 5) {

								badges.add(match.info.participants.get(x).dragonKills + " Dragons Slaughtered");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getDragonString();
								}

							}

							// check baron

							if (match.info.participants.get(x).baronKills >= 3) {

								badges.add(match.info.participants.get(x).baronKills + " Baron Kills");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getBaronString();
								}

							}

							// check potions

							if (match.info.participants.get(x).consumablesPurchased >= 15) {

								badges.add(match.info.participants.get(x).consumablesPurchased + " Potions Bought");
								if (mainFound) {

								} else {
									mainFound = true;
									title = new achievementListBanter().getPotionString();
								}

							}

							// FINAL CHECK

							if (mainFound) {

								badges.add(" .  ");
								String mainStat = badges.get(0);
								badges.remove(0);

								String stats = String.join("\n", badges);
								try {
									new helperObject().sendDiscordWebhook(
											match.info.participants.get(x).summonerName + "(Lv."
													+ summonersAL.get(sumIndex).summonerLevel + ")",
											title, stats, mainStat, "",
											LEAGUE_PROFILE_ENDPOINT + summonersAL.get(sumIndex).profileIconId + ".png",
											LEAGUE_CHAMP_ENDPOINT + match.info.participants.get(x).championName
													+ ".png");

								} catch (ClientProtocolException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							new helperObject().recordWeeklyStats(db, 
									match.info.participants.get(x).summonerName,
									LEAGUE_PROFILE_ENDPOINT + summonersAL.get(sumIndex).profileIconId + ".png",
									match.info.participants.get(x).pentaKills,
									match.info.participants.get(x).quadraKills,
									match.info.participants.get(x).totalMinionsKilled,
									match.info.participants.get(x).kills, match.info.participants.get(x).inhibitorKills,
									match.info.participants.get(x).wardsPlaced,
									match.info.participants.get(x).turretKills,
									match.info.participants.get(x).baronKills,
									match.info.participants.get(x).dragonKills,
									match.info.participants.get(x).consumablesPurchased);

						}

					}

				}

			}
		});

		new helperObject().setPreviousMatchestoDatabase(db, sanitizedLastMatches);

		try {
			db.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void weeklyReport() {

		
		API_KEY = System.getenv("RIOT_API_KEY");
		DISCORD_WEBHOOK_URL = System.getenv("DISCORD_WEBHOOK_URL");

		if (API_KEY == null) {
			System.err.println("Cannot get RIOT_API_KEY from environment variable");
			return;
		}

		if (DISCORD_WEBHOOK_URL == null) {
			System.err.println("Cannot get DISCORD_WEBHOOK_URL from environment variable");
			return;
		}

		final Firestore db = new database().getDatabaseConnection();

		if (db == null) {
			System.err.println("Database Connection Failed to start");
			return;
		}

		
		new helperObject().processWeekly(db);
		new helperObject().resetWeeklyStats(db);

	}
}
