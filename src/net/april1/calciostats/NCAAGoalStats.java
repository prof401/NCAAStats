package net.april1.calciostats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NCAAGoalStats extends NCAA {
	public List<String> retrieveGoalStats(List<String> gameData) {

		return null;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE));
		List<String> gameData = new java.util.ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			gameData.add(line);
		}
		reader.close();

		NCAAGoalStats stats = new NCAAGoalStats();
		BufferedWriter out = new BufferedWriter(new FileWriter(STAT_FILE));
		// process each line in some way
		for (String data : stats.retrieveGoalStats(gameData)) {
			out.write(data);
			out.write('\n');
		}
		out.close();
	}
}
