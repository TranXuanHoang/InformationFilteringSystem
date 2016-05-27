package infofilter;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import ciagent.CIAgent;
import ciagent.CIAgentEvent;
import ciagent.CIAgentState;
import learn.BackProp;
import learn.DataSet;
import learn.KMapNet;

/**
 * The <code>FilterAgent</code> class implements an agent that is
 * capable of scoring articles based on the list of keywords given
 * by user using three methods:
 * <ul>
 * <li>Keyword counts
 * <li>Back propagation neural network
 * <li>Kohonen map neural network
 * </ul>
 * 
 * @author Tran Xuan Hoang
 */
public class FilterAgent extends CIAgent {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/** A reference to the owning <code>InfoFilterFrame</code>. */
	transient InfoFilterFrame infoFilter;

	/** Holds keywords specified by user. */
	protected String[] keywords;

	/** The Kohonen map neural network for clustering articles. */
	protected KMapNet clusterNet;

	/** The back propagation neural network for rating articles. */
	protected BackProp ratingNet;

	/** Indicates whether the Kohonen map neural network
	 * will be built. */
	protected boolean buildClusterNet;

	/** Indicates whether the Kohonen map neural network
	 * has been trained. */
	protected boolean clusterNetTrained;

	/** Indicates whether the back propagation neural network
	 * will be built. */
	protected boolean buildRatingNet;

	/** Indicates whether the back propagation neural network
	 * has been trained. */
	protected boolean ratingNetTrained;

	/** The name of the file that is used to save the serialized
	 * object of a filter agent. */
	public static final String fileName = "filterAgent.ser";

	public static final int USE_KEYWORDS = 0;
	public static final int USE_CLUSTERS = 1;
	public static final int USE_PREDICTED_RATING = 2;

	public static final String USELESS_RATING = "Useless";
	public static final String NOTVERY_RATING = "Not very useful";
	public static final String NEUTRAL_RATING = "Neutral";
	public static final String MILDLY_RATING = "Mildly interesting";
	public static final String INTERESTING_RATING = "Interesting";
	public static final String[] RATINGS = {
			USELESS_RATING,
			NOTVERY_RATING,
			NEUTRAL_RATING,
			MILDLY_RATING,
			INTERESTING_RATING};

	/**
	 * Creates a <code>FilterAgent</code> with default name
	 * <b>FilterAgent</b>.
	 */
	public FilterAgent() {
		this("FilterAgent");
	}

	/**
	 * Creates a <code>FilterAgent</code> with the given name.
	 * @param name the name of the agent to be created.
	 */
	public FilterAgent(String name) {
		super(name);
		buildClusterNet = false;
		clusterNetTrained = false;
		buildRatingNet = false;
		ratingNetTrained = false;

		// create the default keyword list
		setKeywords(new String[] {"java", "agent", "fuzzy",
				"intelligent", "neural", "network", "genetic",
				"rule", "learning", "inferencing"});
	}

	/**
	 * Retrieves the task description used for display purposes.
	 * @return the task description of the agent.
	 */
	@Override
	public String getTaskDescription() {
		return "Filter article";
	}

	/**
	 * Initializes the agent by setting up the sleep time and state.
	 */
	@Override
	public void initialize() {
		setSleepTime(5 * 1000); // every 5 seconds
		setState(CIAgentState.INITIATED);
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void process() {
	}

	/**
	 * Performs training neural networks if the agent is requested.
	 * @see {@link ciagent.CIAgent#processTimerPop()}
	 */
	@Override
	public void processTimerPop() {
		if (!buildClusterNet && !buildRatingNet) {
			status("Idle");
			return;
		}

		if (buildClusterNet) {
			try {
				status("Training Kohonen map neural network");
				buildClusterNet = false;
				trainClusterNet();
				clusterNetTrained = true;
				status("Kohonen map neural network was trained");
			} catch (Exception e) {
				status("Kohonen map neural network - No data" + e);
			}
		} else if (buildRatingNet) {
			try {
				status("Training back propagation neural network");
				buildRatingNet = false;
				trainRatingNet();
				infoFilter.ratingNetTrained();
				ratingNetTrained = true;
				status("Back propagation neural network was trained");
			} catch (Exception e) {
				status("Back propagation neural network - No data" + e);
			}
		} else if (clusterNetTrained && ratingNetTrained) {
			status("Neural networks were trained");
		}
	}

	/**
	 * Builds the Kohonen map neural network used by the cluster
	 * filter. The <code>infofilter.dfn</code> and <code>
	 * infofilter.dat</code> files are used as training data.
	 */
	private void trainClusterNet() {
		// create a data set using infofilter.dfn/dat files
		DataSet dataSet = new DataSet("ProfileData", "infofilter");
		dataSet.loadDataFile();

		// create a Kohonen map neural network
		clusterNet = new KMapNet("Kohonen Map Neural Network");
		clusterNet.setDataSet(dataSet); //TODO redundant
		clusterNet.setNumRecs(dataSet.getNumRecords());
		clusterNet.setFieldsPerRec(dataSet.getFieldsPerRec());
		clusterNet.setData(dataSet.getNormalizedData());

		// configure the Kohonen map neural network to have as many
		// inputs as are defined in the infofilter.dat file and to
		// have four (2 rows * 2 columns) outputs or clusters
		clusterNet.createNetwork(clusterNet.getFieldsPerRec(), 2, 2);

		// train the network
		int maxNumPasses = 20;
		int numRecs = clusterNet.getNumRecs();

		for (int i = 0; i < maxNumPasses; i++) {
			for (int j = 0; j < numRecs; j++) {
				clusterNet.cluster(); // train
			}

			// after each training pass, to enable the InfoFilter
			// application to continue its processing, the
			// FilterAgent thread is yielded
			Thread.yield();
		}

		clusterNet.setMode(1); // lock the network weights
		trace("\nKohonen Map Training Completed\n");

		// a single pass is used to check results
		for (int i = 0; i < numRecs; i++) {
			clusterNet.cluster(); // test
			// clusterNet.display_network();
		}
	}

	/**
	 * Build the back propagation neural network used by the rating
	 * filter (feedback filter). The <code>infofilter.dfn</code> and
	 * <code>infofilter.dat</code> files are used as training data.
	 * The trained network uses the article profile (keyword matches
	 * and score) to predict expected usefulness as represented by
	 * the feedback value that is assigned to the article. The
	 * output value ranges from 0.0 to 1.0, so articles are sorted
	 * in descending order with the score that is closest to 1.0 at
	 * the top of the list and the score that is closest to 0.0 at
	 * the bottom.
	 */
	private void trainRatingNet() {
		// create a data set using infofilter.dfn/dat files
		DataSet dataSet = new DataSet("ProfileData", "infofilter");
		dataSet.loadDataFile();

		// create a back propagation neural network
		ratingNet = new BackProp("Back Propagation Neural Network");
		ratingNet.setDataSet(dataSet); //TODO redundant
		ratingNet.setNumRecs(dataSet.getNumRecords());
		ratingNet.setFieldsPerRec(dataSet.getNormFieldsPerRec());
		ratingNet.setData(dataSet.getNormalizedData());

		//TODO understand + comment
		int numOutputs = dataSet.getClassFieldSize();
		int numInputs = ratingNet.getFieldsPerRec() - numOutputs; //TODO understand
		ratingNet.createNetwork(numInputs, 2 * numInputs, numOutputs);

		// train the network
		int maxNumPasses = 2500; // default, could be changed on ap
		int numRecs = ratingNet.getNumRecs();

		for (int i = 0; i < maxNumPasses; i++) {
			for (int j = 0; j < numRecs; j++) {
				ratingNet.process(); // train
			}

			// after each training pass, to enable the InfoFilter
			// application to continue its processing, the
			// FilterAgent thread is yielded
			Thread.yield();
		}

		ratingNet.setMode(1); // lock the network
		trace("\nBack Propagation Passes Completed: " + maxNumPasses +
				",  RMS Error: " + ratingNet.getAveRMSError() + "\n");

		// a single pass is used to check results
		for (int i = 0; i < numRecs; i++) {
			ratingNet.process(); // test
			//ratingNet.display_network();
		}
	}

	/**
	 * Processes agent event.
	 * @param e the agent event to be processed.
	 */
	@Override
	public void processCIAgentEvent(CIAgentEvent e) {
		trace(name + ": CIAgentEvent received by " + name +
				" from " + e.getSource() +
				" with argument " + e.getArgObject());

		String arg = (String) e.getArgObject();

		if (arg.equals("buildClusterNet")) {
			buildClusterNet = true;
		} else if (arg.equals("buildRatingNet")) {
			buildRatingNet = true;
		}
	}

	/**
	 * Calculates the score of interestingness of each article
	 * with respect to the corresponding filter type
	 * (frequencies of keywords, Kohonen map, back propagation net).
	 * @param article the article to be scored.
	 * @param filterType the filter type (or filter method):<br>
	 * <ul>
	 * <li>{@link #USE_KEYWORDS}
	 * <li>{@link #USE_CLUSTERS}
	 * <li>{@link #USE_PREDICTED_RATING}
	 * </ul>
	 */
	protected void score(NewsArticle article, int filterType) {
		article.counts = countWordMultiKeys(keywords, article.body);
		int size = article.counts.length;
		int sum = 0;

		for (int i = 0; i < size; i++) {
			sum += article.counts[i];
		}

		article.setKeywordScore(sum);

		// based on the filter type that the user wants
		// fill in the score slot of each article
		switch (filterType) {
		case USE_KEYWORDS: // frequencies of keywords
			article.setKeywordScore(sum);
			break;

		case USE_CLUSTERS: // Kohonen map
			if (clusterNet != null) {
				double[] inputRec = new double[size + 2];

				for (int i = 0; i < size; i++) {
					inputRec[i] = (double) article.counts[i];
				}

				inputRec[size] = article.getKeywordScore();
				inputRec[size + 1] = article.getRating();

				article.setClusterId(clusterNet.getCluster(inputRec));
			}

			break;

		case USE_PREDICTED_RATING: // back propagation
			if (ratingNet != null) {
				double[] inputRec = new double[size + 2];

				for (int i = 0; i < size; i++) {
					inputRec[i] = article.counts[i];
				}

				inputRec[size] = article.getKeywordScore();
				inputRec[size + 1] = article.getRating();

				article.setPredictedRating(ratingNet.getPrediction(inputRec));
			}

			break;

		default:
			System.out.println("Error: Filter type is not valid");
		}

		// do an automatic feedback pass automatically so that
		// user doesn't have to do it for each article.
		// user can override via feedback menu option
		if (sum == 0) {
			article.setUserRating(USELESS_RATING);
		} else if (sum < 2) {
			article.setUserRating(NOTVERY_RATING);
		} else if (sum < 4) {
			article.setUserRating(NEUTRAL_RATING);
		} else if (sum < 6) {
			article.setUserRating(MILDLY_RATING);
		} else {
			article.setUserRating(INTERESTING_RATING);
		}
	}

	/**
	 * Scores all loaded articles using the given filter type.
	 * If the filter type is using Kohonen map neural network,
	 * the average score of all articles that have the same
	 * <code>clusterID</code> will be calculated.
	 * @param articles a vector of articles to be scored.
	 * @param filterType the filter type (or filter method):<br>
	 * <ul>
	 * <li>{@link #USE_KEYWORDS}
	 * <li>{@link #USE_CLUSTERS}
	 * <li>{@link #USE_PREDICTED_RATING}
	 * </ul>
	 */
	protected void score(Vector<NewsArticle> articles, int filterType) {
		try {
			for (int i = 0; i < articles.size(); i++) {
				NewsArticle article = articles.elementAt(i);

				score(article, filterType);
			}
		} catch (Exception e) {
			trace("Error: Exception occured while scoring articles");
		}

		if (filterType == USE_CLUSTERS) {
			computeClusterAverages(articles);
		}
	}

	/**
	 * Computes the average score for each cluster and sets the
	 * cluster score for each article in each cluster to that
	 * corresponding average value.
	 * @param articles a vector of articles for which the cluster
	 * score is set.
	 */
	protected void computeClusterAverages(Vector<NewsArticle> articles) {
		int numClusters = 4; // we are using 4 for now
		int sum[] = new int[numClusters];
		int numArticles[] = new int[numClusters];
		double avgs[] = new double[numClusters];

		// compute raw match score sum and
		// number of articles in each cluster
		for (int i = 0; i < articles.size(); i++) {
			NewsArticle article = articles.elementAt(i);
			int cluster = article.getClusterId();

			sum[cluster] += article.getKeywordScore(); // sum of counts
			numArticles[cluster]++;
		}

		// compute average score for each cluster
		for (int i = 0; i < numClusters; i++) {
			if (numArticles[i] > 0) {
				avgs[i] = (double) sum[i] / (double) numArticles[i];
			} else {
				avgs[i] = 0.0;
			}

			trace("  Cluster " + i + " avg = " + avgs[i] + "\n");
		}

		// set each article's cluster score to the corresponding value
		for (int i = 0; i < articles.size(); i++) {
			NewsArticle article = articles.elementAt(i);

			article.setClusterScore(avgs[article.clusterId]);
		}
	}

	/**
	 * Counts the occurrence frequencies of keywords in the body
	 * text of an article.
	 * @param keys a list of keywords given by user.
	 * @param body the body text of the article from which the
	 * frequencies of keyword will be calculated.
	 * @return an array that contains occurrence frequencies of
	 * corresponding keywords.
	 */
	private int[] countWordMultiKeys(String[] keys, String body) {
		int[] counts = new int[keys.length];
		StringTokenizer tokens = new StringTokenizer(body, " .?!,\"");
		Hashtable<String, Integer> frequencies = new Hashtable<>();

		// initialize frequencies of keywords as 0 at the beginning
		for (String key : keys) {
			frequencies.put(key, 0);
		}

		// calculate frequencies
		while (tokens.hasMoreElements()) {
			String token = tokens.nextToken().toLowerCase();
			Integer f = frequencies.get(token);

			if (f == null) {
				continue;
			} else {
				frequencies.put(token, f + 1);
			}
		}

		// copy frequencies of keywords from hash table to array
		for (int i = 0; i < keys.length; i++) {
			counts[i] = frequencies.get(keys[i]);
			trace("  key = " + keys[i] +
					", frequency = " + counts[i] + "\n");
		}

		return counts;
	}

	/**
	 * Generates the <code>infofilter.dfn</code> text file, which
	 * defines the layout of the user profile data file. The
	 * {@link DataSet} class reads a <code>.dfn</code> file when
	 * it loads a file for training neural networks or decision
	 * trees. The file contains pairs of data types and field names.
	 */
	protected void writeProfileDataDefinition() {
		try {
			FileWriter writer = new FileWriter("infofilter.dfn");
			BufferedWriter out = new BufferedWriter(writer);

			for (int i = 0; i < keywords.length; i++) {
				out.write("continuous ");
				out.write(keywords[i]);
				out.newLine();
			}

			// user rating value
			out.write("continuous ClassField");
			out.newLine();

			out.flush();
			out.close();
		} catch (IOException e) {
			trace("Error: FilterAgent couldn't create "
					+ "infofilter.dfn file");
		}
	}

	/**
	 * Appends the filter profile record of an article to the
	 * <code>infofilter.dat</code> file.<br>
	 * The {@link NewsArticle#getProfileString()} method formats
	 * this information.
	 * @param currentArt the article to be appended.
	 */
	protected void addArticleToProfile(NewsArticle article) {
		try {
			FileWriter writer = new FileWriter("infofilter.dat", true);
			BufferedWriter out = new BufferedWriter(writer);

			out.write(article.getProfileString());
			out.newLine();
			out.flush();
			out.close();
		} catch (IOException e) {
			trace("Error: FiterAgent couldn't append article to profile\n");
		}
	}

	/**
	 * Appends all the filter profile records of a set of articles
	 * to the <code>infofilter.dat</code> file.<br>
	 * The {@link NewsArticle#getProfileString()} method formats
	 * the appended filter profile records.
	 * @param articles the articles whose profile records are appended.
	 */
	protected void addAllArticlesToProfile(Vector<NewsArticle> articles) {
		try {
			FileWriter writer = new FileWriter("infofilter.dat", true);
			BufferedWriter out = new BufferedWriter(writer);

			for (NewsArticle article : articles) {
				out.write(article.getProfileString());
				out.newLine();
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			trace("Error: FiterAgent couldn't append articles to profile\n");
		}
	}

	/**
	 * Sends a status event to all registered listeners.
	 * @param msg the message incorporated in the event to be sent.
	 */
	public void status(String msg) {
		// create a data event
		CIAgentEvent event = new CIAgentEvent(this, "status", msg);

		// sent the event to all registered listeners
		notifyCIAgentEventListeners(event);
	}

	/**
	 * Retrieves the list of keywords given by user.
	 * @return a String array of keywords.
	 */
	public String[] getKeywords() {
		return keywords;
	}

	/**
	 * Sets the list of keywords given by user.
	 * @param keywords the list of keywords to be set.
	 */
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	/**
	 * Triggers an autonomous build of Kohonen map neural network.
	 */
	public void buildClusterNet() {
		buildClusterNet = true;
	}

	/**
	 * Triggers an autonomous build of back propagation neural network.
	 */
	public void buildRatingNet() {
		buildRatingNet = true;
	}

	/**
	 * Indicates whether the Kohonen map neural network is trained.
	 * @return <code>true</code> if the Kohonen map is trained and
	 * <code>false</code> otherwise.
	 */
	public boolean isClusterNetTrained() {
		return clusterNetTrained;
	}

	/**
	 * Indicates whether the back propagation neural network is trained.
	 * @return <code>true</code> if the back propagation network is
	 * trained, <code>false</code> otherwise.
	 */
	public boolean isRatingNetTrained() {
		return ratingNetTrained;
	}

	/**
	 * Reads a serialized version of <code>FilterAgent</code>
	 * object from the specified file.
	 * @param fileName the name of the file containing the serialized
	 * version of <code>FilterAgent</code> object.
	 * @return the <code>FilterAgent</code> read from file if it
	 * exists, <code>null</code> otherwise.
	 * @throws ClassNotFoundException if the file saving <code>
	 * FilterAgent</code> object cannot be found.
	 * @throws IOException if any error appears while reading object.
	 */
	public static FilterAgent restoreFromFile(String fileName)
			throws ClassNotFoundException, IOException {
		FileInputStream saveFile = new FileInputStream(fileName);
		ObjectInputStream inStream = new ObjectInputStream(saveFile);

		FilterAgent restoreAgent = null;
		restoreAgent = (FilterAgent) inStream.readObject();
		inStream.close();
		System.out.println("Successfully read FilterAgent fom " + fileName);

		return restoreAgent;
	}

	/**
	 * Writes a serialized version of this <code>FilterAgent</code>
	 * object to the specified file.
	 * @param fileName the name of the file into which agent is written.
	 */
	public void saveToFile(String fileName) {
		try {
			FileOutputStream saveFile = new FileOutputStream(fileName);
			ObjectOutputStream outStream = new ObjectOutputStream(saveFile);

			outStream.writeObject(this);
			outStream.flush();
			outStream.close();
			System.out.println(
					"Successfully saved FilterAgent to " + fileName);
		} catch (IOException e) {
			System.out.println(
					"Error at FilterAgent.saveToFile()" + e);
		}
	}
} // end class FilterAgent