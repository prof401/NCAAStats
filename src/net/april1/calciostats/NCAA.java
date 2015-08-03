package net.april1.calciostats;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NCAA {

	public Set<Integer> getGames() throws IOException {
		Set<Integer> gameSet = new HashSet<Integer>();
		getYears();
		return gameSet;

	}

	private Set<Integer> getYears() throws IOException {
		Set<Integer> yearSet = new HashSet<Integer>();
		Document doc = Jsoup.connect(
				"http://stats.ncaa.org/team/inst_team_list?sport_code=WSO")
				.get();
		Elements links = doc.select("a[href^=javascript:changeYears]");
		for (Element link : links) {
			System.out.println(link.attr("href").toString());
		}
		return yearSet;
	}

	public static void main(String[] args) throws IOException {
		NCAA ncaa = new NCAA();
		Set<Integer> games = ncaa.getGames();
		for (Integer game : games) {
			System.out.println(game);
		}
	}

}
