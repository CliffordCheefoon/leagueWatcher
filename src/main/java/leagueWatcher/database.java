package leagueWatcher;

import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;


public class database {

	final static String projectId = "leaguewatcher-c4400";

	public Firestore getDatabaseConnection() {
		GoogleCredentials credentials;
		try {
			credentials = GoogleCredentials.getApplicationDefault();
			FirestoreOptions options = FirestoreOptions.newBuilder().setCredentials(credentials).setProjectId(projectId)
					.build();

			Firestore db = options.getService();

			return db;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

}
