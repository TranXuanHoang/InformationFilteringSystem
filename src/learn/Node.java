package learn;

import java.io.Serializable;
import java.util.Vector;

/**
 * The <code>Node</code> class contains the label or name and the
 * links for a node in a <code>DecisionTree</code>.
 * 
 * @author Tran Xuan Hoang
 */
public class Node implements Serializable {
	/**
	 * The serial version ID.
	 */
	private static final long serialVersionUID = 1L;

	protected String label;				 // name of the node
	protected Vector<String> linkLabels; // tests on links from parent to child
	protected Node parent;				 // parent node
	protected Vector<Node> children;	 // any children nodes

	/**
	 * Creates a default node.
	 */
	public Node() {
		parent = null;
		children = new Vector<>();
		linkLabels = new Vector<>();
	}

	/**
	 * Creates a node with the given name.
	 * @param label the name of the node.
	 */
	public Node(String label) {
		this.label = label;
		linkLabels = new Vector<>();
		parent = null;
		children = new Vector<>();
	}

	/**
	 * Creates a node with the given name and parent.
	 * @param parent the Node that is the parent of the node being
	 * created.
	 * @param label the String that contains the name of the node.
	 */
	public Node(Node parent, String label) {
		this.label = label;
		linkLabels = new Vector<>();
		this.parent = parent;
		children = new Vector<>();
	}

	/**
	 * Adds a child node and the link name for the link to that child.
	 * @param child the node that is added as a child.
	 * @param linkLabel the name of the link from this node to the
	 * child node.
	 */
	public void addChild(Node child, String linkLabel) {
		children.addElement(child);
		linkLabels.addElement(linkLabel);
	}

	/**
	 * Checks if the node has children nodes linked to it.
	 *
	 * @return  <code>true</code> if the node has children.
	 * Otherwise, returns <code>false</code>.
	 */
	public boolean hasChildren() {
		return (children.size() > 0);
	}

	/**
	 * Sets the name of the node.
	 * @param label the String that contains the name of the node.
	 */
	public void setLabel(String label) {
		this.label = label;
	}
} // end class Node