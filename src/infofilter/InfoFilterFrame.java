package infofilter;

import javax.swing.JFrame;

import ciagent.CIAgentEvent;
import ciagent.CIAgentEventListener;

public class InfoFilterFrame extends JFrame implements CIAgentEventListener {

	@Override
	public void processCIAgentEvent(CIAgentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postCIAgentEvent(CIAgentEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Enables using clusters for filtering after the Kohonen map
	 * neural network was trained.
	 */
	public void clusterNetTrained() {
		//TODO
	}
	
	/**
	 * Enables using feedback for filtering after the back propagation
	 * neural network was trained.
	 */
	public void ratingNetTrained() {
		//TODO
	}
} // end class InfoFilterFrame