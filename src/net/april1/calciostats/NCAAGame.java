package net.april1.calciostats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NCAAGame extends NCAA {

	public static void main(String[] args) throws IOException {
		NCAAGame ncaa = new NCAAGame();
		long start = System.currentTimeMillis();
		if (args.length >= 1) {
			System.out.println("##### 4-3 #####");
			ncaa.getGameData("350871"); // 4-3 game
			System.out.println("##### 0-0 #####");
			ncaa.getGameData("3453824"); // 0-0 tie
			System.out.println("##### 3-3 #####");
			ncaa.getGameData("3472071"); // 3-3 tie
			System.out.println("##### 0-0 OT goal #####");
			ncaa.getGameData("446670"); // 0-0 regulation OT goal
			System.out.println("##### 1-1 OT goal #####");
			ncaa.getGameData("334030"); // 1-1 regulation OT goal
		} else {
			int count = 0;
			BufferedReader reader = new BufferedReader(
					new FileReader(GAME_FILE));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// process each line in some way
				ncaa.getGameData(line);
				if (++count % 1000 == 0) {
					System.out.print(dateTimeFormat(System.currentTimeMillis()
							- start));
					System.out.print(' ');
					System.out.println(count);
				}
			}
			reader.close();
		}
		System.out.println(dateTimeFormat(System.currentTimeMillis() - start));
	}

	public void getGameData(String game) {
		List<GameData> gameData = new java.util.ArrayList<GameData>();
		int homeScore = 0;
		int awayScore = 0;
		try {
			Document doc = getDocument(PLAY_URL + game);
			Elements tableRows = doc.select("tr:has(td.smtext:matches(GOAL))");
			for (Element tableRow : tableRows) {
				if (tableRow.toString().contains("Shootout")) {
					System.out.println(tableRow);
					continue;
				}
				boolean away = false; // validity check
				String firstField = tableRow.child(0).text();
				String minute = "111";
				try {
					minute = firstField.substring(0, firstField.indexOf(':'));
				} catch (Exception e) {
					try {
						String first = tableRow.toString().substring(
								tableRow.toString().indexOf('['),
								tableRow.toString().length());
						minute = first.substring(1, first.indexOf(':'));
						System.out.println("---> " + minute + " <---");
					} catch (Exception e2) {
						System.err.print("Bad minute in game " + game + " -> "
								+ firstField);
						System.err.print(" *** ");
						System.err.print(tableRow.child(1).text());
						System.err.print("<-->");
						System.err.println(tableRow.child(3).text());
					}
				}

				if (tableRow.child(1).text().length() > 0) {
					away = true; // validity check
					awayScore++;
				}
				if (tableRow.child(3).text().length() > 0) {
					if (away)
						System.err.println("Dual score in game " + game); // validity
																			// check
					homeScore++;
				}
				gameData.add(new GameData(minute, homeScore, awayScore));
			}
		} catch (IOException ioe) {
			System.err.println("ERROR gettings game data " + game);
			System.err.println("      " + ioe.getMessage());
		}
		if (homeScore == 0 && awayScore == 0) {
			GameData zeroTie = new GameData("110", 0, 0);
			gameData.add(zeroTie);
		}
		char homeResult = 'T';
		if (homeScore > awayScore)
			homeResult = 'W';
		if (homeScore < awayScore)
			homeResult = 'L';
		for (GameData gd : gameData) {
			gd.setHomeResult(homeResult);
			// System.out.println(gd);
		}
	}

	private class GameData {
		String _minute;
		int _homeScore;
		int _awayScore;
		char _homeResult;

		public GameData(String minute, int homeScore, int awayScore) {
			_minute = minute;
			_homeScore = homeScore;
			_awayScore = awayScore;
		}

		public int getAwayScore() {
			return _awayScore;
		}

		public char getHomeResult() {
			return _homeResult;
		}

		public int getHomeScore() {
			return _homeScore;
		}

		public String getMinute() {
			return _minute;
		}

		public void setHomeResult(char homeResult) {
			_homeResult = homeResult;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_minute);
			sb.append(',');
			sb.append(_homeScore);
			sb.append(',');
			sb.append(_awayScore);
			sb.append(',');
			sb.append(_homeResult);
			return sb.toString();
		}

	}

}
