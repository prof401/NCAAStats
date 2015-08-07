package net.april1.calciostats;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

abstract public class NCAA {

	protected static final String GAME_FILE = "games.txt";
	protected static final String DATA_FILE = "data.txt";
	protected static final String STAT_FILE = "stat.txt";
	private static final int TIMEOUT_SECONDS = 10; // default is 3, 0 means
	// infinite
	protected static final String BASE_URL = "http://stats.ncaa.org";
	protected static final String TEAMS_URL = BASE_URL
			+ "/team/inst_team_list?sport_code=WSO";

	protected static final String PLAY_URL = BASE_URL + "/game/play_by_play/";

	public static String dateTimeFormat(long millis) {
		return String.format("%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
				TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
	}

	protected Document getDocument(String url) throws IOException {
		Document returnDoc = null;
		int tries = 0;
		do {
			try {
				returnDoc = Jsoup.connect(url).timeout(TIMEOUT_SECONDS * 1000)
						.get();
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
				System.err.println(ste.getMessage());
				if (tries >= 3)
					System.exit(-1);
				else {
					try {
						Thread.sleep(TIMEOUT_SECONDS * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.err.println("trying again");
				}
			} catch (IOException ioe) {
				System.err.println("IO Exception for " + url);
				throw ioe;
			}
		} while (returnDoc == null && tries++ < 3);
		return returnDoc;
	}
}
