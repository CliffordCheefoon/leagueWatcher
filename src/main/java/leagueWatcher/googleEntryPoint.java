package leagueWatcher;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

public class googleEntryPoint implements HttpFunction {
	// Global (instance-wide) scope
	// This computation runs at instance cold-start.
	// Warning: Class variables used in functions code must be thread-safe.

	// google entry point: leagueWatcher.googleEntryPoint

	public void service(HttpRequest request, HttpResponse response) throws IOException {

		Map<String, List<String>> params = request.getQueryParameters();
		

		if (params.containsKey("weeklyStats")) {

			new mainFunction().weeklyReport();
		} else {
			new mainFunction().main();
			
		}

	}

}