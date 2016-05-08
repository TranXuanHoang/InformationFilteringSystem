package infofilter;

import ciagent.CIAgent;
import learn.KMapNet;

public class FilterAgent extends CIAgent {
	//TODO
	protected String[] keywords;
	//protected KMapNet
	
	public static final int USE_KEYWORDS = 0;
	public static final int USE_CLUSTERS = 1;
	public static final int USE_PREDICTED_RATING = 2;
	
	public static final String USELESS_RATING = "Useless";
	public static final String NOTVERY_RATING = "Not very useful";
	public static final String NEUTRAL_RATING = "Neutral";
	public static final String MILDLY_RATING = "Mildly interesting";
	public static final String INTERESTING_RATING = "Interesting";

	@Override
	public String getTaskDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void process() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processTimerPop() {
		// TODO Auto-generated method stub

	}

} // end class FilterAgent