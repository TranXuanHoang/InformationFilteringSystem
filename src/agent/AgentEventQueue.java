package agent;

import java.io.Serializable;
import java.util.Vector;

/**
 * The <code>AgentEventQueue</code> class implements a queue of
 * intelligent agent events.
 * 
 * @author Tran Xuan Hoang
 */
public class AgentEventQueue implements Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	/** Queue of agent events. */
	private Vector<AgentEvent> eventQueue;

	/**
	 * Creates an empty queue of events.
	 */
	public AgentEventQueue() {
		eventQueue = new Vector<>();
	}

	/**
	 * Adds an event to the end of the queue.
	 * @param event the event to be added to the queue.
	 */
	public synchronized void addEvent(AgentEvent event) {
		eventQueue.addElement(event);
	}

	/**
	 * Retrieves the first element from the queue (if any) after
	 * removing it from the queue.
	 * @return the first event on the queue. If there are not any
	 * elements in the queue, <code>null</code> will be returned.
	 */
	public synchronized AgentEvent getNextEvent() {
		if (eventQueue.size() == 0) {
			return null;
		} else {
			return eventQueue.remove(0);
		}
	}

	/**
	 * Retrieves the first element from the queue (if any) without
	 * removing it.
	 * @return the first event on the queue. If there are not any
	 * elements in the queue, <code>null</code> will be returned.
	 */
	public synchronized AgentEvent peekEvent() {
		if (eventQueue.size() == 0) {
			return null;
		} else {
			return eventQueue.elementAt(0);
		}
	}
} // end class AgentEventQueue