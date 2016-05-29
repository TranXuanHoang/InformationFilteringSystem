package infofilter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ciagent.CIAgent;
import ciagent.CIAgentEvent;
import ciagent.CIAgentState;

/**
 * The <code>URLReaderAgent</code> class implements an agent that
 * is capable of downloading data from web pages, encapsulating
 * the data to send to other receivers.
 * 
 * @author Tran Xuan Hoang
 */
public class URLReaderAgent extends CIAgent {
	/** Serial version. */
	private static final long serialVersionUID = 1L;

	/** URL of the web page where the article will be downloaded. */
	protected URL url;

	/** Contains optional parameter for submission to <b>Common
	 * Gateway Interface (CGI)</b> programs. */
	protected String paramString;

	/** Represents data that is read from the URL or that is
	 * returned by the CGI-BIN at that URL. */
	String contents;

	/**
	 * Creates a <code>URLReaderAgent</code> with the default name
	 * <code>URLReaderAgent</code>.
	 */
	public URLReaderAgent() {
		this("URLReaderAgent");
	}

	/**
	 * Creates a <code>URLReaderAgent</code> with the given name.
	 * @param name the name of the agent to be created.
	 */
	public URLReaderAgent(String name) {
		super(name);
	}

	/**
	 * Retrieves the task description used for display purposes.
	 * @return the task description of the agent.
	 */
	@Override
	public String getTaskDescription() {
		return "Read a URL";
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
	 * Does nothing.
	 */
	@Override
	public void processTimerPop() {
	}

	/**
	 * Handles two kinds of action events:
	 * <ul>
	 * <li>trace: show out event's information
	 * <li>getURLText: download web page's contents
	 * </ul>
	 * In case the action is <code>getURLText</code>, web page's
	 * contents will be downloaded and encapsulated as a <code>
	 * NewsArticle</code> which is then sent to event listeners
	 * via {@link #sendArticleToListeners(NewsArticle)} method.
	 * @param event the event received by this
	 * <code>URLReaderAgent</code>.
	 */
	@Override
	public void processCIAgentEvent(CIAgentEvent event) {
		Object source = event.getSource();
		Object arg = event.getArgObject();
		Object action = event.getAction();

		trace("\n" + name + ": Event received by " + name +
				" from " + source.getClass());

		if (action != null) {
			if (action.equals("trace")) {
				if ((arg != null) && (arg instanceof String)) {
					trace((String) arg);
				}
			} else if (action.equals("getURLText")) {
				// read the web page at the given URL
				String text = getURLText();

				// send text back to the event
				if (text != null) {
					NewsArticle article = new NewsArticle(
							url.toString(),
							NewsArticle.FROM_WEB_PAGE);

					article.setSubject(url.toString(), NewsArticle.FROM_WEB_PAGE);
					article.setBody(text);
					sendArticleToListeners(article);
				}
			}
		}
	}

	/**
	 * Sends downloaded article to any listener listening for it.
	 * @param article the article to be sent.
	 */
	protected void sendArticleToListeners(NewsArticle article) {
		System.out.println("URLReaderAgent is sending "
				+ "download article to listeners.");
		CIAgentEvent event =
				new CIAgentEvent(this, "addArticle", article);
		notifyCIAgentEventListeners(event);
	}

	/**
	 * Reads the web page at a single URL to obtain its contents
	 * in the form of HTML code.
	 * @return a String of HTML code containing the web page's
	 * contents at the given URL.
	 */
	protected String getURLText() {
		System.out.println("URLReaderAgent is starting to read URL.");

		try {
			HttpURLConnection connection =
					(HttpURLConnection) url.openConnection();
			System.out.println("Connection is opened.");

			// process parameters if any
			if (paramString != null && paramString.length() > 0) {
				connection.setDoOutput(true);
				PrintWriter out =
						new PrintWriter(connection.getOutputStream());
				out.println(paramString);
				out.close();
			}

			StringBuffer body = new StringBuffer();
			String line;
			BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));

			while ((line = in.readLine()) != null) {
				body.append(line + "\n");
			}

			in.close();

			contents = body.toString(); // save as string in agent
			return contents;
		} catch (Exception e) {
			trace("Error: Could not connect to URL: " + e);
			return null;
		}
	}

	/**
	 * Retrieves the URL that is currently used to download article.
	 * @return the URL of the web page.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Sets the URL of the web page from which data is downloaded.
	 * @param url the URL of the web page.
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/**
	 * Retrieves parameters sent to the web page.
	 * @return parameters sent to the web page.
	 */
	public String getParamString() {
		return paramString;
	}

	/**
	 * Sets parameters sent to the web page.
	 * @param paramString parameters sent to the web page.
	 */
	public void setParamString(String paramString) {
		this.paramString = paramString;
	}

	/**
	 * Retrieves the contents of the web page in the form of HTML.
	 * @return HTML code of the web page's contents.
	 */
	public String getContents() {
		return contents;
	}
} // end class URLReaderAgent