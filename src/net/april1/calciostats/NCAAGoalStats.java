package net.april1.calciostats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class NCAAGoalStats extends NCAA {
	public int[][][][] retrieveGoalStats(List<String> gameData) {
		int[][][][] stats = new int[22][22][3][112];
		int lastMinute = 91;
		int lasts1 = 0;
		int lasts2 = 0;
		int wlt = -1;
		String game = "";
		for (String data : gameData) {
			if (data.indexOf(',') > 0) {
				String[] split = data.split(",");
				switch (split[3]) {
				case "W":
					wlt = 0;
					break;
				case "T":
					wlt = 1;
					break;
				case "L":
					wlt = 2;
					break;
				}
				int minute = Integer.parseInt(split[0]);
				if (minute>110 || minute < lastMinute) { 
					System.err.println("bad minute " + minute + " in game " + game);
					minute = 110;
				}
				for (int m = lastMinute; m < minute; m++) {
					stats[lasts1][lasts2][wlt][m]++;
					stats[lasts2][lasts1][2 - wlt][m]++;
				}
				lasts1 = Integer.parseInt(split[1]);
				lasts2 = Integer.parseInt(split[2]);
				lastMinute = minute;
			} else {
				game = data;
				if (lastMinute <= 90) {
					for (int m = lastMinute; m < 90; m++) {
						stats[lasts1][lasts2][wlt][m]++;
						stats[lasts2][lasts1][2 - wlt][m]++;
					}
				}
				lastMinute = 0;
				lasts1 = 0;
				lasts2 = 0;
			}
		}

		return stats;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE));
		List<String> gameData = new java.util.ArrayList<String>();
		String line = null;
		int cnt = 0;
		while ((line = reader.readLine()) != null) {
			gameData.add(line);
//			if (cnt++ > 10000)
//				break;
		}
		reader.close();

		NCAAGoalStats stats = new NCAAGoalStats();
		BufferedWriter out = new BufferedWriter(new FileWriter(STAT_FILE));
		// process each line in some way
		int[][][][] s = stats.retrieveGoalStats(gameData);
		for (int s1 = 0; s1<22;s1++) {
			for (int s2=0;s2<22;s2++) {
				out.write(Integer.toString(s1));
				out.write(',');
				out.write(Integer.toString(s2));
				out.write('\n');
				for (int m=0;m<112;m++) {
					for(int r=0;r<3;r++) {
						out.write(Integer.toString(s[s1][s2][r][m]));
						if(r<2) out.write(',');
					}
					out.write('\n');
				}
			}
		}
		out.close();
	}
}
