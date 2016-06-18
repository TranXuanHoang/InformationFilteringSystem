package agent;

import java.util.EventListener;

/**
 * The <code>AgentEventListener</code> interface defines two
 * methods for processing events that <code>Agent</code>s receives.
 * 
 * @author Tran Xuan Hoang
 */
public interface AgentEventListener extends EventListener {
	/**
	 * Processes the event of the caller's thread immediately.
	 * @param e the event to be processed.
	 */
	public void processAgentEvent(AgentEvent e);

	/**
	 * Adds the event on an <code>AgentEventQueue</code> for
	 * later asynchronous processing.
	 * @param e the event to be added to the event queue.
	 */
	public void postAgentEvent(AgentEvent e);
} // end interface AgentEventListener