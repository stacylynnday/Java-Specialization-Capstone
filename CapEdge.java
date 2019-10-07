package graph;

/**
 * @author Stacy Lynn Day
 * 
 * Class representing a CapEdge (or connection) between two CapNodes (People)
 *
 */

public class CapEdge {
	private CapNode from;
	private CapNode to;
	
	// default CapEdge constructor
	public CapEdge(CapNode from, CapNode to) {
		this.from = from;
		this.to= to;
	}
	// CapEdge constructor
	public CapEdge(CapEdge ce) {
		this.from = ce.from;
		this.to = ce.to;
	}
	// get from point of CapEdge
	public int getFromPoint() {
		return this.from.getNum();
	}
	// get to point of CapEdge
	public int getToPoint() {
		return this.to.getNum();
	}
	// get to CapNode
	public CapNode getToNode() {
		return this.to;
	}
}
