package infofilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The <code>NewsArticle</code> class defines all of the information
 * about a single article that is saved in personal computer or
 * loaded from a web page source.
 * 
 * @author Tran Xuan Hoang
 */
public class NewsArticle {
	/** ID of each article. The absolute file path if the article
	 * is read from a file (text/PDF/MS Word/HTML) in a computer.
	 * The URL if the article is downloaded from a web page. */
	protected String id;

	/** Indicates what type of source (text file, PDF file, MS
	 * Word file, HTML, or web page) this <code>NewsArticle</code>
	 * object is created from. */
	protected int type;

	/** Indicates the article is read from a text file. */
	public static final int FROM_TEXT_FILE = 0;

	/** Indicates the article is read from a PDF file. */
	public static final int FROM_PDF_FILE = 1;

	/** Indicates the article is read from a word file. */
	public static final int FROM_MS_WORD_FILE = 2;

	/** Indicates the article is read from a power point .pptx file.*/
	public static final int FROM_PPTX_FILE = 3;

	/** Indicates the article is read from a HTML file. */
	public static final int FROM_HTML_FILE = 4;

	/** Indicates the article is read from a web page. */
	public static final int FROM_WEB_PAGE = 5;

	/** Subject of the article that is parsed out of the header
	 * information or the web page URL. */
	protected String subject;

	/** Body text of the article. It contains the entire text
	 * of an article saved in personal computer, or the HTML
	 * contents of the web page. */
	protected String body;

	/** Raw frequencies of keywords in the body text. */
	protected int counts[];

	/** Sum of all element of {@link #counts}. */
	protected int keywordScore;

	/** Predicted rating from back propagation network. */
	protected double predictedRating = 0.5;

	/** User rating. */
	protected String userRating;

	/** Numeric user rating value that is used to describe the
	 * usefulness of the article to the user.<br>
	 * 0.0 is useless and 1.0 is interesting. */
	protected double rating = 0.5;

	/** The article's ranking based on the type of filter
	 * (keywordScore, clusterAvg or feedbackMatch). */
	protected double score;

	/** Holds the cluster into which this article falls. */
	protected int clusterId;

	/** Average score of all articles in the cluster. */
	protected double clusterScore;

	/**
	 * Constructs a new article object with specified ID.
	 * @param id the identifier of the article (file path if the
	 * <code>NewsArticle</code> object is create to load article
	 * in personal computer).
	 */
	public NewsArticle(String id, int type) {
		this.id = id;
		this.type = type;
	}

	/**
	 * Reads an article from a <b>text</b> or <b>HTML</b> file.
	 * @param fileName the name of the file to be read.
	 * @param directory the directory that contains the file to be read.
	 * @return the text contents of the file.
	 */
	public static String readArticle(String filePath) {
		try {
			File f = new File(filePath);
			FileInputStream in = new FileInputStream(f);
			int size = (int) f.length();
			byte[] data = new byte[size];

			in.read(data);
			String body = new String(data);
			in.close();

			return body;
		} catch (IOException e) {
			System.out.println("Error: couldn't read article from " + filePath);
			return "";
		}
	}

	/**
	 * Writes an article to the given file.
	 * @param fileName the name of the file to be written.
	 * @param directory the directory where the file will be
	 * written into.
	 */
	public void writeArticle(String fileName, String directory) {
		String contents = body;
		int size = (int) contents.length();
		byte data[] = new byte[size];

		data = contents.getBytes();

		try {
			File f = new File(directory + fileName);
			FileOutputStream out = new FileOutputStream(f);

			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("Error: couldn't write artice to" + fileName);
		}
	}

	/**
	 * Retrieves the ID of the article.
	 * @return
	 * <ul>
	 * <li>The absolute path if the article is read from a file in
	 * personal computer.
	 * <li>The URL if the article is downloaded from a web page.
	 * </ul>
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Sets the id for the article.
	 * @param id the String id to be set as the id of the article.
	 */
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * Finds out the type of the file based on the given file path.
	 * @param filePath the absolute path (directory + file name) or
	 * the name of the file whose type is determined.
	 * @return <ul>
	 * <li><code>{@value #FROM_TEXT_FILE}</code>: text file
	 * <li><code>{@value #FROM_PDF_FILE}</code>: PDF file
	 * <li><code>{@value #FROM_MS_WORD_FILE}</code>: MS Word file
	 * <li><code>{@value #FROM_PPTX_FILE}</code>: MS PowerPoint file
	 * <li><code>{@value #FROM_HTML_FILE}</code>: HTML file
	 * <li><code>-1</code>: none of the above
	 * </ul>
	 */
	public static int typeOfFile(String filePath) {
		if (filePath.endsWith(".txt")) {
			return FROM_TEXT_FILE;
		} else if (filePath.endsWith(".pdf")) {
			return FROM_PDF_FILE;
		} else if (filePath.endsWith(".docx") || filePath.endsWith(".doc")) {
			return FROM_MS_WORD_FILE;
		} else if (filePath.endsWith(".pptx")) {
			return FROM_PPTX_FILE;
		} else if (filePath.endsWith(".html")) {
			return FROM_HTML_FILE;
		} else {
			return -1;
		}
	}

	/**
	 * Retrieves the type of the article (text file, PDF file, MS
	 * Word file, HTML file, or web page).
	 * @return
	 * <ul>
	 * <li>{@link #FROM_TEXT_FILE}
	 * <li>{@link #FROM_PDF_FILE}
	 * <li>{@link #FROM_MS_WORD_FILE}
	 * <li>{@link #FROM_PPTX_FILE}
	 * <li>{@link #FROM_HTML_FILE}
	 * <li>{@link #FROM_WEB_PAGE}
	 * </ul>
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Sets the type of file from which the article is read.
	 * @param type one of the following type:
	 * <ul>
	 * <li>{@link #FROM_TEXT_FILE}
	 * <li>{@link #FROM_PDF_FILE}
	 * <li>{@link #FROM_MS_WORD_FILE}
	 * <li>{@link #FROM_PPTX_FILE}
	 * <li>{@link #FROM_HTML_FILE}
	 * <li>{@link #FROM_WEB_PAGE}
	 * </ul>
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Retrieves the subject of the article.
	 * @return the subject of the article.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject of the article. The file name without the
	 * file extension
	 * @param fileNameOrURL the file name (not the absolute path)
	 * of the file or the URL from which the article is read.
	 * @param type the type of the article (@see {@link #type}).
	 */
	public void setSubject(String fileNameOrURL, int type) {
		int lastIndex = -1;

		switch (type) {
		case FROM_TEXT_FILE:
			lastIndex = fileNameOrURL.lastIndexOf(".txt");
			break;
		case FROM_PDF_FILE:
			lastIndex = fileNameOrURL.lastIndexOf(".pdf");
			break;
		case FROM_MS_WORD_FILE:
			lastIndex = fileNameOrURL.lastIndexOf(".doc");
			break;
		case FROM_PPTX_FILE:
			lastIndex = fileNameOrURL.lastIndexOf(".ppt");
			break;
		case FROM_HTML_FILE:
			lastIndex = fileNameOrURL.lastIndexOf(".html");
			break;
		case FROM_WEB_PAGE:
			lastIndex = fileNameOrURL.length();
			break;
		default:
			lastIndex = -1;
		}

		if (lastIndex == -1) {
			System.out.println("Error: cannot set the article's "
					+ "subject" + fileNameOrURL);
		} else {
			this.subject = fileNameOrURL.substring(0, lastIndex);
		}
	}

	/**
	 * Retrieves the body of the article.
	 * @return the body of the article.
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the body of the article.
	 * @param body the body text to be of the article to be set.
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * Retrieves the keyword score for the article.
	 * @return the keyword score for the article.
	 */
	public int getKeywordScore() {
		return keywordScore;
	}

	/**
	 * Sets the keyword score for this article.
	 * @param keywordScore the score of matching keywords.
	 */
	public void setKeywordScore(int keywordScore) {
		this.keywordScore = keywordScore;
	}

	/**
	 * Retrieves the current score for the given filter type.
	 * @param filterType an integer representing the filter type:<br>
	 * <ul>
	 * <li><code>0</code>: using keywords
	 * <li><code>1</code>: using cluster
	 * <li><code>2</code>: using predicted rating
	 * </ul>
	 * @return the current score of the article.
	 */
	public double getScore(int filterType) {
		switch (filterType) {
		case FilterAgent.USE_KEYWORDS:
			return keywordScore;
		case FilterAgent.USE_CLUSTERS:
			return clusterScore;
		case FilterAgent.USE_PREDICTED_RATING:
			return predictedRating;
		}

		return 0.0;
	}

	/**
	 * Retrieves the user rating for the article.
	 * @return the user rating represented as a String.
	 */
	public String getUserRating() {
		return userRating;
	}

	/**
	 * Sets the user rating and the associated rating for the article.
	 * @param userRating the user rating in String.
	 */
	public void setUserRating(String userRating) {
		this.userRating = userRating;

		switch (userRating) {
		case FilterAgent.USELESS_RATING:
			rating = 0.0;
			break;
		case FilterAgent.NOTVERY_RATING:
			rating = 0.25;
			break;
		case FilterAgent.NEUTRAL_RATING:
			rating = 0.5;
			break;
		case FilterAgent.MILDLY_RATING:
			rating = 0.75;
			break;
		case FilterAgent.INTERESTING_RATING:
			rating = 1.0;
		}
	}

	/**
	 * Retrieves the rating that describes the usefulness of the
	 * article to the user.
	 * @return the rating in between the interval [0.0, 1.0].
	 */
	public double getRating() {
		return rating;
	}

	/**
	 * Retrieves the cluster id of the article.
	 * @return the cluster id of the article.
	 */
	public int getClusterId() {
		return clusterId;
	}

	/**
	 * Sets the cluster id for the article.
	 * @param clusterId the cluster id to be set for the article.
	 */
	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	/**
	 * Retrieves the cluster score of the article.
	 * @return the cluster score of the article.
	 */
	public double getClusterScore() {
		return clusterScore;
	}

	/**
	 * Sets the cluster score for the article.
	 * @param clusterScore the cluster score to be set for the article.
	 */
	public void setClusterScore(double clusterScore) {
		this.clusterScore = clusterScore;
	}

	/**
	 * Retrieves the predicted rating of the article.
	 * @return the predicted rating of the article.
	 */
	public double getPredictedRating() {
		return predictedRating;
	}

	/**
	 * Sets the predicted rating for the article.
	 * @param predictedRating the predicted rating to be set for
	 * the article.
	 */
	public void setPredictedRating(double predictedRating) {
		this.predictedRating = predictedRating;
	}

	/**
	 * Retrieves the profile data, including the raw keyword counts
	 * and the numeric user rating, as a String.
	 * @return the profile data in String.
	 */
	public String getProfileString() {
		StringBuffer profile = new StringBuffer();

		for (int i = 0; i < counts.length; i++) {
			profile.append(counts[i]);
			profile.append(" ");
		}

		profile.append(rating); // numeric user rating value

		return profile.toString();
	}
} // end class NewsArticle