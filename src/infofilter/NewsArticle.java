package infofilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The <code>NewsArticle</code> class defines all of the information
 * about a single article save in personal computer or web page
 * source.
 * 
 * @author Tran Xuan Hoang
 */
public class NewsArticle {
	/** ID of each article. */
	protected String id;

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
	 * @param id the identifier of the article.
	 */
	public NewsArticle(String id) {
		this.id = id;
	}

	/**
	 * Reads an article from the given file.
	 * @param fileName the name of the file to be read.
	 */
	public void readArticle(String fileName) {
		try {
			File f = new File(fileName);
			FileInputStream in = new FileInputStream(f);
			int size = (int) f.length();
			byte[] data = new byte[size];

			in.read(data);
			subject = "Subject: " + fileName;
			body = new String(data);
			id = fileName;
			in.close();
		} catch (IOException e) {
			System.out.println("Error: couldn't read article from " + fileName);
		}
	}

	/**
	 * Writes an article to the given file.
	 * @param fileName the name of the file to be written.
	 */
	public void writeArticle(String fileName) {
		String contents = subject + " " + body;
		int size = (int) contents.length();
		byte data[] = new byte[size];

		data = contents.getBytes();

		try {
			File f = new File(fileName);
			FileOutputStream out = new FileOutputStream(f);

			out.write(data);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("Error: couldn't write artice to" + fileName);
		}
	}

	/**
	 * Retrieves the subject of the article.
	 * @return the subject of the article.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject of the article.
	 * @param subject the subject of the article to be set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
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