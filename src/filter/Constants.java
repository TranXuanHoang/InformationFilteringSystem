package filter;

/**
 * The <code>Constants</code> class defines constants that are
 * commonly used by the information filtering application.
 * 
 * @author Tran Xuan Hoang
 */
public class Constants {
	/** The without-extension name of the files {@value}.[dfn/dat]. */
	public static final String keyworsdFileName = "infofilter";

	/** The name of the file {@value} that defines all the keywords
	 * used to score and rate articles. */
	public static final String keywordDefinitionsFileName =
			keyworsdFileName + ".dfn";

	/** The name of the file {@value} that contains frequencies of
	 *  all the keywords used to score and rate articles. */
	public static final String keywordCountsFileName =
			keyworsdFileName + ".dat";
} // end class Constants