package leagueWatcher;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import models.discordHook;
import models.match;
import models.summoner;
import models.versionObject;

public class helperObject {

	String PENTAKILL_COUNT = "pentaKillCount";
	String QUADRAKILL_COUNT = "quadraKillCount";
	String MAX_CS = "maxCs";
	String KILL_COUNT = "killCount";
	String INHIB_KILL_COUNT = "InhibKillsCount";
	String WARD_COUNT = "wardCount";
	String TURRENT_KILL_COUNT = "turrentKillCount";
	String BARON_KILL_COUNT = "baronKillCount";
	String DRAGON_KILL_COUNT = "dragonKillCount";
	String POTION_COUNT = "potionCount";

	public void loadSummoners(final Firestore db, final CopyOnWriteArrayList<summoner> summonersAL) {

		ApiFuture<QuerySnapshot> query = db.collection(mainFunction.SUMMONER_LIST_DB_NAME).get();

		try {
			QuerySnapshot querySnapshot = query.get();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

			documents.parallelStream().forEach(new Consumer<QueryDocumentSnapshot>() {
				public void accept(QueryDocumentSnapshot c) {

					summoner summonerObj = getSummonerData(c.getId(), db);

					if (summonerObj.name != null) {
						summonersAL.add(summonerObj);
					}

				}
			});

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public CopyOnWriteArrayList<String> getPreviousmatchesFromDatabase(Firestore db) {

		CopyOnWriteArrayList<String> previousMatches = new CopyOnWriteArrayList<String>();

		ApiFuture<QuerySnapshot> query = db.collection(mainFunction.PREVIOUS_MATCH_LIST_DB_NAME).get();
		try {

			QuerySnapshot querySnapshot = query.get();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
			for (QueryDocumentSnapshot document : documents) {

				previousMatches.add(document.getId());

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return previousMatches;

	}

	public void clearPreviousMatchesDatabase(Firestore db) {

		CollectionReference x = db.collection(mainFunction.PREVIOUS_MATCH_LIST_DB_NAME);
		deleteCollection(x, 100);

	}

	private void deleteCollection(CollectionReference collection, int batchSize) {
		try {
			// retrieve a small batch of documents to avoid out-of-memory errors
			ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
			int deleted = 0;
			// future.get() blocks on document retrieval
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			for (QueryDocumentSnapshot document : documents) {

				// System.err.println(document.getReference().getId() + " DELETED");
				document.getReference().delete();
				++deleted;
			}
			if (deleted >= batchSize) {
				// retrieve and delete another batch
				deleteCollection(collection, batchSize);
			}
		} catch (Exception e) {
			System.err.println("Error deleting collection : " + e.getMessage());
		}
	}

	public summoner getSummonerData(String summonerName, Firestore db) {

		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod("https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/"
				+ summonerName + "?api_key=" + mainFunction.API_KEY);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
				foundUserReport(summonerName, db, false);
			} else {
				foundUserReport(summonerName, db, true);
			}

			// Read the response body.

			InputStream responseBody = method.getResponseBodyAsStream();

			String result = IOUtils.toString(responseBody, StandardCharsets.UTF_8);

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data

			String body = new String(result);

			// System.out.println(body);

			summoner x = new Gson().fromJson(body, summoner.class);

			return x;

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			return null;

		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			return null;

		} catch (JsonSyntaxException e) {
			System.err.println("Gson object conversion failed: " + e.getMessage());
			e.printStackTrace();
			return null;

		} finally {

			method.releaseConnection();
		}
	}

	public void foundUserReport(String summonerName, Firestore db, boolean found) {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("API_FOUND", found);
		db.collection(mainFunction.SUMMONER_LIST_DB_NAME).document(summonerName).update(data);

	}

	public ArrayList<String> getMatchesForSummoner(summoner user) {

		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod("https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/"
				+ user.puuid + "/ids?start=0&count=1&api_key=" + mainFunction.API_KEY);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			InputStream responseBody = method.getResponseBodyAsStream();

			String result = IOUtils.toString(responseBody, StandardCharsets.UTF_8);

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data

			String body = new String(result);

			// System.out.println(user.name + " most recent matchId: "+body);

			Type type = new TypeToken<ArrayList<String>>() {
			}.getType();

			ArrayList<String> x = new Gson().fromJson(body, type);
			return x;

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			return null;

		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			return null;

		} finally {

			method.releaseConnection();
		}

	}

	public void setPreviousMatchestoDatabase(final Firestore db, List<String> matchId) {

		final Map<String, Object> emptyData = new HashMap<String, Object>();
		emptyData.put("Date", OffsetDateTime.now(ZoneOffset.UTC).toString());

		matchId.forEach(new Consumer<String>() {
			public void accept(String r) {

				db.collection(mainFunction.PREVIOUS_MATCH_LIST_DB_NAME).document(r).set(emptyData);
				// System.err.println(r + " ADDED");

			}
		});

	}

	public void sendDiscordWebhook(String playerName, String title, String stats, String mainStat, String actionImage,
			String playerIconURL, String championIcon) throws ClientProtocolException, IOException {

		String content = new Gson()
				.toJson(new discordHook(playerName, title, stats, mainStat, actionImage, playerIconURL, championIcon));

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(mainFunction.DISCORD_WEBHOOK_URL);

		// System.out.println(content);

		String json = content;
		StringEntity entity = new StringEntity(json);
		httpPost.setEntity(entity);

		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		CloseableHttpResponse response = client.execute(httpPost);

		System.out.println("Discord webhook response code: " + response.getStatusLine());
		// System.out.println(response.toString());
		// assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
		client.close();

	}

	public void runRecord(Firestore db) {

		DateFormat requiredDateFormat = new SimpleDateFormat("hh:mm a zzz, EEE MMMM dd,yyyy");
		requiredDateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
		String date = requiredDateFormat.format(new Date());

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("EST", date);

		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

		db.collection(mainFunction.RUN_LOG_DB_NAME).document(now.toString()).set(data);

	}

	public match getmatchData(String matchId) {

		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod("https://americas.api.riotgames.com/lol/match/v5/matches/" + matchId
				+ "?api_key=" + mainFunction.API_KEY);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.

			InputStream responseBody = method.getResponseBodyAsStream();

			String result = IOUtils.toString(responseBody, StandardCharsets.UTF_8);

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data

			String body = new String(result);

			// System.out.println(body);

			match x = new Gson().fromJson(body, match.class);
			return x;

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			return null;

		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			return null;

		} finally {

			method.releaseConnection();
		}
	}

	public void setVersioning() {
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(mainFunction.VERSION_LOOKUP_URL);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.

			InputStream responseBody = method.getResponseBodyAsStream();

			String result = IOUtils.toString(responseBody, StandardCharsets.UTF_8);

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary data

			String body = new String(result);

			// System.out.println(body);

			versionObject x = new Gson().fromJson(body, versionObject.class);

			mainFunction.LEAGUE_PROFILE_ENDPOINT = mainFunction.LEAGUE_PROFILE_ENDPOINT
					.replace(mainFunction.PROFILE_IMAGE_PROVIDER_VERSION_PLACEHOLDER, x.n.profileicon);
			mainFunction.LEAGUE_CHAMP_ENDPOINT = mainFunction.LEAGUE_CHAMP_ENDPOINT
					.replace(mainFunction.CHAMPION_IMAGE_PROVIDER_VERSION__PLACEHOLDER, x.n.champion);

		} catch (JsonSyntaxException e) {
			System.err.println("Fatal error when getting versioning data");
			e.printStackTrace();
			System.exit(0);

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);

		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);

		} finally {
			method.releaseConnection();
		}

	}

	public void recordWeeklyStats(Firestore db, String summonerName, int pentakill, int quadrakill, int cs, int Kills,
			int inhibKills, int wards, int turrentKills, int baronKills, int dragonKill, int potions) {

		DocumentReference docRef = db.collection(mainFunction.SUMMONER_LIST_DB_NAME).document(summonerName);

		ApiFuture<DocumentSnapshot> query = docRef.get();

		Map<String, Object> commitData = new HashMap<String, Object>();

		DocumentSnapshot document;
		try {

			document = query.get();

			if (document.exists()) {

				if (document.contains(PENTAKILL_COUNT)) {

					commitData.put(PENTAKILL_COUNT, pentakill + (document.get(PENTAKILL_COUNT, Integer.class)));

				} else {
					commitData.put(PENTAKILL_COUNT, pentakill);
				}

				if (document.contains(QUADRAKILL_COUNT)) {

					commitData.put(QUADRAKILL_COUNT, quadrakill + (document.get(QUADRAKILL_COUNT, Integer.class)));

				} else {
					commitData.put(QUADRAKILL_COUNT, quadrakill);
				}

				if (document.contains(MAX_CS)) {

					if (document.get(MAX_CS, Integer.class) < cs) {
						commitData.put(MAX_CS, cs);
					}

				} else {
					commitData.put(MAX_CS, cs);
				}

				if (document.contains(KILL_COUNT)) {

					commitData.put(KILL_COUNT, Kills + (document.get(KILL_COUNT, Integer.class)));

				} else {
					commitData.put(KILL_COUNT, Kills);
				}

				if (document.contains(INHIB_KILL_COUNT)) {

					commitData.put(INHIB_KILL_COUNT, inhibKills + (document.get(INHIB_KILL_COUNT, Integer.class)));

				} else {
					commitData.put(INHIB_KILL_COUNT, inhibKills);
				}

				if (document.contains(WARD_COUNT)) {

					commitData.put(WARD_COUNT, wards + (document.get(WARD_COUNT, Integer.class)));

				} else {
					commitData.put(WARD_COUNT, wards);
				}

				if (document.contains(TURRENT_KILL_COUNT)) {

					commitData.put(TURRENT_KILL_COUNT,
							turrentKills + (document.get(TURRENT_KILL_COUNT, Integer.class)));

				} else {
					commitData.put(TURRENT_KILL_COUNT, turrentKills);
				}

				if (document.contains(BARON_KILL_COUNT)) {

					commitData.put(BARON_KILL_COUNT, baronKills + (document.get(BARON_KILL_COUNT, Integer.class)));

				} else {
					commitData.put(BARON_KILL_COUNT, baronKills);
				}

				if (document.contains(DRAGON_KILL_COUNT)) {

					commitData.put(DRAGON_KILL_COUNT, dragonKill + (document.get(DRAGON_KILL_COUNT, Integer.class)));

				} else {
					commitData.put(DRAGON_KILL_COUNT, dragonKill);
				}

				if (document.contains(POTION_COUNT)) {

					commitData.put(POTION_COUNT, potions + (document.get(POTION_COUNT, Integer.class)));

				} else {
					commitData.put(POTION_COUNT, potions);
				}

				ApiFuture<WriteResult> result = docRef.update(commitData);

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void resetWeeklyStats(final Firestore db) {

		ApiFuture<QuerySnapshot> query = db.collection(mainFunction.SUMMONER_LIST_DB_NAME).get();

		try {
			QuerySnapshot querySnapshot = query.get();

			List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

			final Map<String, Object> commitData = new HashMap<String, Object>();

			commitData.put(PENTAKILL_COUNT, 0);
			commitData.put(QUADRAKILL_COUNT, 0);
			commitData.put(MAX_CS, 0);
			commitData.put(KILL_COUNT, 0);
			commitData.put(INHIB_KILL_COUNT, 0);
			commitData.put(WARD_COUNT, 0);
			commitData.put(TURRENT_KILL_COUNT, 0);
			commitData.put(BARON_KILL_COUNT, 0);
			commitData.put(DRAGON_KILL_COUNT, 0);
			commitData.put(POTION_COUNT, 0);

			documents.parallelStream().forEach(new Consumer<QueryDocumentSnapshot>() {
				public void accept(QueryDocumentSnapshot c) {

					c.getReference().update(commitData);

				}
			});

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
