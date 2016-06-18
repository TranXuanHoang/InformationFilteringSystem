package agent;

import java.io.Serializable;

/**
 * The <code>CIAgentState</code> class defines states of an agent:
 * <ul>
 * <li><code>UNINITIATED</code>
 * <li><code>INITIATED</code>
 * <li><code>ACTIVE</code>
 * <li><code>SUSPENDED</code>
 * <li><code>UNKNOWN</code>
 * </ul>
 * 
 * @author Tran Xuan Hoang
 */
public class AgentState implements Serializable {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	public static final int UNINITIATED = 0;
	public static final int INITIATED = 1;
	public static final int ACTIVE = 2;
	public static final int SUSPENDED = 3;
	public static final int UNKNOWN = 4;
	private int state;

	/**
	 * Create an agent state with the value of <code>UNINITIATED</code>.
	 */
	public AgentState() {
		this.state = UNINITIATED;
	}

	/**
	 * Sets the state of an agent.
	 * @param state the state to be set.
	 */
	public synchronized void setState(int state) {
		this.state = state;
	}

	/**
	 * Retrieves the state of the agent.
	 * @return an integer representing the agent state.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Converts the state of the agent to a printable description.
	 * @return a formatted String that represents the state.
	 */
	public String toString() {
		switch (state) {
		case UNINITIATED:
			return "Uninitiated";
		case INITIATED:
			return "Initiated";
		case ACTIVE:
			return "Active";
		case SUSPENDED:
			return "Suspended";
		case UNKNOWN:
			return "Unknown";
		default:
			return "Unknown";	
		}
	}
} // end class CIAgentState