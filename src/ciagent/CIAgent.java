package ciagent;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;

public abstract class CIAgent implements CIAgentEventListener, Serializable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_SLEEPTIME = 15000; // 15 seconds
	public static final int DEFAULT_ASYNCTIME = 1000; // 1 second
	protected String name;
	private CIAgentState state;
	private CIAgentTimer timer;
	transient private Vector<CIAgentEventListener> listeners;
	transient private CIAgentEventQueue eventQueue;
	transient private PropertyChangeSupport changes;
	protected AgentPlatform agentPlatform; // agent platform

	/** A vector containing all child agents of this agent */
	protected Vector<CIAgent> children;

	/** A vector containing all parent agents of this agent */
	protected Vector<CIAgent> parents;

	protected int traceLevel = 0;

	/**
	 * Creates an agent with default name <code>Agent</code>.
	 * This no-argument constructor allows easy instantiation
	 * within editing and activation frameworks of JavaBeans.
	 */
	public CIAgent() {
		this("Agent");
	}

	/**
	 * Creates an agent with specified name.
	 * @param name the name of the agent.
	 */
	public CIAgent(String name) {
		this.name = name;
		state = new CIAgentState();
		timer = new CIAgentTimer(this);
		timer.setSleepTime(DEFAULT_SLEEPTIME);
		timer.setAsyncTime(DEFAULT_ASYNCTIME);
		listeners = new Vector<>();
		eventQueue = new CIAgentEventQueue();
		changes = new PropertyChangeSupport(this);

		agentPlatform = null;
		children = new Vector<>();
		parents = new Vector<>();
	}

	/**
	 * Retrieves a formatted String for displaying the agent's
	 * current task.
	 * @return a String describing the current task of the agent.
	 */
	public abstract String getTaskDescription();

	/**
	 * Initializes this agent for processing.
	 */
	public abstract void initialize();

	/**
	 * Starts the agent timer and asynchronous event processing.
	 * The agent state will be set to <code>ACTIVE</code>.
	 */
	public synchronized void startAgentProcessing() {
		timer.startTimer();
		setState(CIAgentState.ACTIVE);
	}

	/**
	 * Stops the agent timer and asynchronous event processing.
	 * The agent state will be set to <code>UNKNOWN</code>.
	 */
	public synchronized void stopAgentProcessing() {
		timer.quitTimer();
		setState(CIAgentState.UNKNOWN);
	}

	/**
	 * Temporarily stops the agent timer so that the autonomous
	 * behavior is suspended.
	 * The agent state will be set to <code>SUSPENDED</code>.
	 */
	public void suspendAgentProcessing() {
		timer.stopTimer();
		setState(CIAgentState.SUSPENDED);
	}

	/**
	 * Resumes agent processing of the timer and asynchronous
	 * events. The agent state will be set to <code>ACTIVE</code>.
	 */
	public void resumeAgentProcessing() {
		timer.startTimer();
		setState(CIAgentState.ACTIVE);
	}

	/**
	 * Provides the synchronous processing done by this agent.
	 */
	public abstract void process();

	/**
	 * Provides the asynchronous, autonomous behavior of this agent
	 * that occurs periodically, depending on the sleep time of
	 * this agent.
	 */
	public abstract void processTimerPop();

	/**
	 * Processes all events on the asynchronous event queue
	 * periodically, depending on the asynchronous event time of
	 * this agent.
	 */
	public void processAsynchronousEvents() {
		CIAgentEvent event = null;

		while ((event = eventQueue.getNextEvent()) != null) {
			processCIAgentEvent(event);
		}
	}

	/**
	 * Performs synchronous event processing for this agent.
	 * @param event the event to be processed.
	 */
	@Override
	public void processCIAgentEvent(CIAgentEvent event) {
		// currently this method is leaved as doing nothing
	}

	/**
	 * Adds (enqueue) an event to this agent's event queue.
	 * @param event the event to be added.
	 */
	@Override
	public void postCIAgentEvent(CIAgentEvent event) {
		eventQueue.addEvent(event);
	}

	/**
	 * Adds a listener for events received by the agent.
	 * @param listener the listener to be added.
	 */
	public synchronized void addCIAgentEventListener(
			CIAgentEventListener listener) {
		listeners.addElement(listener);
	}

	/**
	 * Removes a listener for events received by the agent.
	 * @param listener the listener to be removed.
	 */
	public synchronized void removeCIAgentEventListener(
			CIAgentEventListener listener) {
		listeners.removeElement(listener);
	}

	/**
	 * Delivers the event received to all register listeners.
	 * @param e the event to be sent to all listeners.
	 */
	@SuppressWarnings("unchecked")
	protected void notifyCIAgentEventListeners(CIAgentEvent e) {
		Vector<CIAgentEventListener> l;

		synchronized (this) {
			l = (Vector<CIAgentEventListener>) listeners.clone();
		}

		for (int i = 0; i < l.size(); i++) {
			// deliver the event
			l.elementAt(i).processCIAgentEvent(e);
		}
	}

	/**
	 * Adds a listener for  property change events.
	 * @param listener the listener to be added.
	 */
	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		changes.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a listener for property change events.
	 * @param listener the listener to be removed.
	 */
	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		changes.removePropertyChangeListener(listener);
	}

	/**
	 * Sends a trace event to all register listeners.
	 * @param msg the message portion of the trace event.
	 */
	public void trace(String msg) {
		// create a data event
		CIAgentEvent event = new CIAgentEvent(this, "trace", msg);

		// send it ot any registered listeners
		notifyCIAgentEventListeners(event);
	}

	/**
	 * Adds an agent as a child of this agent.
	 * @param child the agent to be added as a child of this agent.
	 */
	public void addAgent(CIAgent child) {
		children.addElement(child);
		child.insertParent(this);
	}

	/**
	 * Removes a child agent of this agent.
	 * @param child the child agent to be removed.
	 */
	public void removeAgent(CIAgent child) {
		children.removeElement(child);
	}

	/**
	 * Sets the name of the agent.
	 * @param newName the new name to be set for the agent.
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		changes.firePropertyChange("name", oldName, newName);
	}

	/**
	 * Retrieves the name of the agent.
	 * @return the name of the agent.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the state of the agent to the new state.
	 * @param newState the new state of the agent.
	 */
	public void setState(int newState) {
		int oldState = state.getState();
		state.setState(newState);
		changes.firePropertyChange("state", oldState, newState);
	}

	/**
	 * Retrieves the current state of the agent (one of the following):
	 * <ul>
	 * <li><code>UNINITIATED</code>
	 * <li><code>INITIATED</code>
	 * <li><code>ACTIVE</code>
	 * <li><code>SUSPENDED</code>
	 * <li><code>UNKNOWN</code>
	 * </ul>
	 * @return the current state of the agent.
	 */
	public CIAgentState getState() {
		return state;
	}

	/**
	 * Sets the agent sleep time (in milliseconds) for autonomous
	 * processing.<br>
	 * <i>Note that this has no effect on running agent.</i>
	 * @param sleepTime the sleep time interval of the agent.
	 */
	public void setSleepTime(int sleepTime) {
		timer.setSleepTime(sleepTime);
	}

	/**
	 * Retrieves the agent sleep time (in milliseconds).
	 * @return the sleep time of the agent.
	 */
	public int getSleepTime() {
		return timer.getSleepTime();
	}

	/**
	 * Sets the time (in milliseconds) for asynchronous event
	 * processing.<br>
	 * <i>Note that this has no effect on running agent.</i>
	 * @param asyncTime the time interval between asynchronous
	 * event processing sessions.
	 */
	public void setAsyncTime(int asyncTime) {
		timer.setAsyncTime(asyncTime);
	}

	/**
	 * Retrieves the time (in milliseconds) for asynchronous event
	 * processing.
	 * @return the asynchronous time of the agent.
	 */
	public int getAsyncTime() {
		return timer.getAsyncTime();
	}

	/**
	 * Sets the trace level.
	 * @param traceLevel the integer representing trace level.
	 */
	public void setTraceLevel(int traceLevel) {
		this.traceLevel = traceLevel;
	}

	/**
	 * Retrieves the trace level.
	 * @return the trace level of the agent.
	 */
	public int getTraceLevel() {
		return traceLevel;
	}

	/**
	 * Sets the platform for the agent.
	 * @param platform the platform to be set.
	 */
	public void setAgentPlatform(AgentPlatform platform) {
		this.agentPlatform = platform;
	}

	/**
	 * Retrieves the agent platform.
	 * @return the platform of the agent.
	 */
	public AgentPlatform getAgentPlatform() {
		return agentPlatform;
	}

	/**
	 * Retrieves all agents running on the same platform with this
	 * agent.
	 * @return a vector of agents running on the same platform with
	 * this agent if there exists. Otherwise, <code>null</code>.
	 */
	public Vector<CIAgent> getAgents() {
		return (agentPlatform == null) ?
				null : agentPlatform.getAgents();
	}

	/**
	 * Retrieves an agent running on the same platform with this
	 * agent and having the given <code>name</code>.
	 * @param name the name of the agent to be sought and returned.
	 * @return the agent in the same platform with this agent if it
	 * exists. Otherwise, <code>null</code> is returned.
	 */
	public CIAgent getAgent(String name) {
		return (agentPlatform == null) ?
				null : agentPlatform.getAgent(name);
	}

	/**
	 * Appends a collection of child agents to the end of the
	 * vector {@link #children} of this agent.
	 * @param children the child agents to be appended.
	 */
	public void insertChildren(Vector<CIAgent> children) {
		this.children.addAll(children);
	}

	/**
	 * Appends a child agent to the end of the vector
	 * {@link #children} of this agent.
	 * @param child the child agent to be appended.
	 */
	public void insertChild(CIAgent child) {
		this.children.addElement(child);
	}

	/**
	 * Retrieves all child agents of this agent.
	 * @return a copy of vector of agents contained by this agent.
	 */
	@SuppressWarnings("unchecked")
	public Vector<CIAgent> getChildren() {
		return (Vector<CIAgent>) children.clone();
	}

	/**
	 * Appends a collection of parent agents to the end of the
	 * vector {@link #parents} of this agent.
	 * @param panrents the parent agents to be appended.
	 */
	public void insertParents(Vector<CIAgent> panrents) {
		this.parents.addAll(panrents);
	}

	/**
	 * Appends a parent agent to the end of the vector
	 * {@link #parents} of this agent.
	 * @param parent the parent agent to be appended.
	 */
	public void insertParent(CIAgent parent) {
		this.parents.addElement(parent);
	}

	/**
	 * Retrieves all parents of this agent.
	 * @return the reference to the vector of parent agents.
	 */
	public Vector<CIAgent> getParents() {
		return parents;
	}

	/**
	 * Uses introspection on this bean to get the customizer class.
	 * @return the customizer class for this agent bean if any,
	 * <code>null</code> otherwise.
	 */
	public Class<?> getCustomizerClass() {
		Class<?> customizerClass = null;

		try {
			BeanInfo info = Introspector.getBeanInfo(this.getClass());
			BeanDescriptor descriptor = info.getBeanDescriptor();

			customizerClass = descriptor.getCustomizerClass();
		} catch (IntrospectionException e) {
			System.out.println("Can't find customizer bean property " + e);
		}

		return customizerClass;
	}

	/**
	 * Uses introspection on this bean to get the display name
	 * (default to the class name).
	 * @return the display name for this agent bean.
	 */
	public String getDisplayName() {
		String name = null;

		try {
			BeanInfo info = Introspector.getBeanInfo(this.getClass());
			BeanDescriptor descriptor = info.getBeanDescriptor();

			name = (String) descriptor.getValue("DisplayName");
		} catch (IntrospectionException e) {
			System.out.println("Can't find display name bean property " + e);
		}

		if (name == null) {
			name = this.getClass().getName(); // default to class name
		}

		return name;
	}

	/**
	 * Deserializes the object from a specified input stream.
	 * @param inputStream the stream from which the object's
	 * information is recovered.
	 * @throws IOException if any I/O error occurs.
	 * @throws ClassNotFoundException if any class file is not found.
	 */
	private void readObject(ObjectInputStream inputStream)
			throws IOException, ClassNotFoundException {
		// restore transient variables
		changes = new PropertyChangeSupport(this);
		listeners = new Vector<>();
		eventQueue = new CIAgentEventQueue();

		// restore the remaining variables
		inputStream.defaultReadObject();
	}
} // end class CIAgent