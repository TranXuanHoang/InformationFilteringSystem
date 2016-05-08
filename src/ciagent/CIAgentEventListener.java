package ciagent;

import java.util.EventListener;

/**
 * The <code>CIAgentEventListener</code> interface defines two
 * methods for processing events that <code>CIAgent</code>s receives.
 * 
 * @author Tran Xuan Hoang
 */
public interface CIAgentEventListener extends EventListener {
	/**
	 * Processes the event of the caller's thread immediately.
	 * @param e the event to be processed.
	 */
	public void processCIAgentEvent(CIAgentEvent e);

	/**
	 * Adds the event on an <code>CIAgentEventQueue</code> for
	 * later asynchronous processing.
	 * @param e the event to be added to the event queue.
	 */
	public void postCIAgentEvent(CIAgentEvent e);
} // end interface CIAgentEventListener