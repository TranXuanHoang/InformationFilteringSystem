package agent;

import java.util.EventObject;

/**
 * The <code>CIAgentEvent</code> class defines common events that
 * are sent by intelligent agents.
 * 
 * @author Tran Xuan Hoang
 */
public class AgentEvent extends EventObject {
	/** The serial version ID. */
	private static final long serialVersionUID = 1L;

	private Object argObject;
	private String action;

	/**
	 * Creates a simple <code>CIAgentEvent</code> object.
	 * @param source the reference to the object sending the event
	 * (the source of this event).
	 */
	public AgentEvent(Object source) {
		super(source);
	}

	/**
	 * Creates a more complex <code>CIAgentEvent</code> object.
	 * @param source the reference to the object sending the event
	 * (the source of this event).
	 * @param argObject supplies additional data about this event.
	 */
	public AgentEvent(Object source, Object argObject) {
		this(source);
		this.argObject = argObject;
	}

	/**
	 * Creates a <code>CIAgentEvent</code> object that includes
	 * <i>data</i> and <i>action</i> of the event.
	 * @param source the reference to the object sending the event
	 * (the source of this event).
	 * @param action represents the action if this event is received,
	 * i.e. the method name could be specified as the <code>action</code>.
	 * @param argObject supplies additional data about this event.
	 */
	public AgentEvent(Object source, String action, Object argObject) {
		this(source, argObject);
		this.action = action;
	}

	/**
	 * Retrieves the object relating to this event.
	 * @return the object relating to this event if any,
	 * otherwise <code>null</code>.
	 */
	public Object getArgObject() {
		return this.argObject;
	}

	/**
	 * Retrieves the action relating to this event.
	 * @return the action relating to this event if any,
	 * otherwise <code>null</code>.
	 */
	public String getAction() {
		return this.action;
	}

	/**
	 * Converts the event to a String formatted for display or printing.
	 * @return the formatted String of the event.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("CIAgent ");
		buf.append("source: " + source);
		buf.append("action: " + action);
		buf.append("argObject: " + argObject);

		return buf.toString();
	}
} // end class CIAgentEvent