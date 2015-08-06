package net.april1.calciostats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NCAASport extends NCAA {

	public Set<String> getGames() throws IOException {
		long start = System.currentTimeMillis();

		int count = 0;
		Set<String> gameSet = new HashSet<String>();
		for (String year : getYears()) {
			for (String division : getDivision(year)) {
				for (String teamLink : getTeamLink(year, division)) {
					gameSet.addAll(getTeamGames(teamLink));
					// if (gameSet.size() > 100)
					// break;
					if (gameSet.size() / 2500 > count) {
						System.out.print(dateTimeFormat(System
								.currentTimeMillis() - start));
						System.out.print(' ');
						System.out.println(gameSet.size());
						count++;
					}
				}
			}
		}
		System.out.println(dateTimeFormat(System.currentTimeMillis() - start));
		return gameSet;
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

	private Set<String> getTeamLink(String year, String division) {
		Set<String> teamSet = new HashSet<String>();
		try {
			Document doc = getDocument(TEAMS_URL + "&academic_year=" + year
					+ "&division=" + division);
			Elements links = doc.select("a[href^=/team/index/]");
			for (Element link : links) {
				teamSet.add(link.attr("href").toString());
			}
		} catch (IOException ioe) {
			System.err.println("ERROR gettings year " + year + " division "
					+ division);
			System.err.println("      " + ioe.getMessage());
		}
		return teamSet;
	}

	public static void main(String[] args) throws IOException {

		NCAASport ncaa = new NCAASport();
		BufferedWriter out = new BufferedWriter(new FileWriter(GAME_FILE));
		for (String game : ncaa.getGames()) {
			out.write(game);
			out.write('\n');
		}
		out.close();
	}

}
