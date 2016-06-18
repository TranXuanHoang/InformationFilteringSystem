package agent;

import java.util.Vector;

/**
 * The <code>AgentPlatform</code> interface defines the common
 * behaviors of agent platform.
 * 
 * @author Tran Xuan Hoang
 */
public interface AgentPlatform {
	/**
	 * Returns a list of registered agents.
	 * @return a vector of agents.
	 */
	public Vector<Agent> getAgents();

	/**
	 * Retrieves an agent with the specified name.
	 * @param name the name of agent to be returned.
	 * @return the agent having the given name or
	 * <code>null</code> if not found.
	 */
	public Agent getAgent(String name);
} // end interface AgentPlatform