package net.april1.calciostats;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NCAA {

	private static final String BASE_URL = "http://stats.ncaa.org";
	private static final String TEAMS_URL = BASE_URL + "/team/inst_team_list?sport_code=WSO";
	private static final String PLAY_URL = BASE_URL + "/game/play_by_play/";

	public void getGameData(String game) {
		try {
			Document doc = getDocument(PLAY_URL + game);
			Elements tableRows = doc.select("tr:has(td.smtext:matches(GOAL))");
			for (Element tableRow : tableRows) {
				System.out.print(tableRow.child(0).text());
				System.out.print(' ');
				System.out.println(tableRow.child(2).text());
				System.out.println(tableRow.child(1).text());
				System.out.println(tableRow.child(3).text());
			}
		} catch (IOException ioe) {
			System.err.println("ERROR gettings game data " + game);
			System.err.println("      " + ioe.getMessage());

		}

	}

	public Set<String> getGames() throws IOException {
		Set<String> gameSet = new HashSet<String>();
		for (String year : getYears()) {
			for (String division : getDivision(year)) {
				for (String teamLink : getTeamLink(year, division)) {
					gameSet.addAll(getTeamGames(teamLink));
					if (gameSet.size() > 100)
						break;
				}
			}
		}
		return gameSet;
	}

	private Set<String> getTeamGames(String teamLink) {
		final Pattern pattern = Pattern.compile("(\\d+)");
		Set<String> gameSet = new HashSet<String>();
		try {
			Document doc = getDocument(BASE_URL + teamLink);
			Elements links = doc.select("a[href^=/game/index/]");
			for (Element link : links) {
				Matcher matcher = pattern.matcher(link.attr("href").toString());
				matcher.find();
				gameSet.add(matcher.group(0));
				// gameSet.add(link.attr("href").toString());
			}
		} catch (IOException ioe) {
			System.err.println("ERROR gettings teamLink " + teamLink);
			System.err.println("      " + ioe.getMessage());

		}
		return gameSet;

	}

	private Set<String> getTeamLink(String year, String division) {
		Set<String> teamSet = new HashSet<String>();
		try {
			Document doc = getDocument(TEAMS_URL + "&academic_year=" + year + "&division=" + division);
			Elements links = doc.select("a[href^=/team/index/]");
			for (Element link : links) {
				teamSet.add(link.attr("href").toString());
			}
		} catch (IOException ioe) {
			System.err.println("ERROR gettings year " + year + " division " + division);
			System.err.println("      " + ioe.getMessage());
		}
		return teamSet;
	}

	private Set<String> getDivision(String year) {
		Set<String> divisionSet = new HashSet<String>();
		final Pattern pattern = Pattern.compile("(\\d+)");
		try {
			Document doc = getDocument(TEAMS_URL + "&academic_year=" + year);
			Elements links = doc.select("a[href^=javascript:changeDivisions]");
			for (Element link : links) {
				Matcher matcher = pattern.matcher(link.attr("href").toString());
				matcher.find();
				divisionSet.add(matcher.group(0));
			}
		} catch (IOException ioe) {
			System.err.println("ERROR gettings year " + year);
			System.err.println("      " + ioe.getMessage());
		}
		return divisionSet;
	}

	private Set<String> getYears() throws IOException {
		final Pattern pattern = Pattern.compile("(\\d+)");
		Set<String> yearSet = new HashSet<String>();
		Document doc = getDocument(TEAMS_URL);
		Elements links = doc.select("a[href^=javascript:changeYears]");
		for (Element link : links) {
			Matcher matcher = pattern.matcher(link.attr("href").toString());
			matcher.find();
			yearSet.add(matcher.group(0));
		}
		return yearSet;
	}

	private Document getDocument(String url) throws IOException {
		Document returnDoc = null;
		int tries = 0;
		do {
			try {
				returnDoc = Jsoup.connect(url).get();
			} catch (MalformedURLException mue) {
				System.err.println("Malformed URL " + url);
				System.exit(-1);
			} catch (HttpStatusException hse) {
				System.err.println("Unexpected HTTP Status getting " + url);
				System.exit(-1);
			} catch (UnsupportedMimeTypeException ume) {
				System.err.println("Unsupported MIME Type from " + url);
				System.exit(-1);
			} catch (SocketTimeoutException ste) {
				System.err.println("Socket Timeout for " + url);
				if (tries >= 3)
					System.exit(-1);
			} catch (IOException ioe) {
				System.err.println("IO Exception for " + url);
				throw ioe;
			}
		} while (returnDoc == null && tries++ < 3);
		return returnDoc;
	}

	public static void main(String[] args) throws IOException {

		long start = System.currentTimeMillis();
		NCAA ncaa = new NCAA();
//		for (String game : ncaa.getGames()) {
//			System.out.println("**" + game + "**");
//			ncaa.getGameData(game);
//		}
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
		System.out.print((System.currentTimeMillis() - start) / 1000);
		System.out.println(" DONE");
	}
}
