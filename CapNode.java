package graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graph.CapEdge;

/**
 * CapNode.java
 * 
 * @author Stacy Lynn Day
 * 
 * A class to represent a vertice (or Node or Intersection) in CapGraph
 */

public class CapNode {
	private int num;  //node ID
	private HashSet<CapEdge> nodeedges;
	private String color;  // "white" = uncovered, "grey" = covered, "black" = member of dom. set
	
	// default CapNode constructor
	public CapNode(int num) {
		this.num = num;
		nodeedges = new HashSet<CapEdge>();
		this.color = "white";
	}
	// get num (i.e. node id)
	public int getNum() {
		return num;
	}
	// add edge
	public boolean addEdge(CapEdge edge) {
		if (nodeedges.contains(edge) || edge == null) {
			return false;
		}
		else {
			nodeedges.add(edge);
			return true;
		}
	}
	// set the color of the node (for minimum dominating set)
	public void setColor(String color) {
		this.color = color;
	}
	// get the color of the node (for minimum dominating set)
	public String getColor() {
		return this.color;
	}
	
	// get the edges out of this node
	public Set<CapEdge> getEdges() {
		return nodeedges;
	}	
	// get to points from edges of this node
	public List<Integer> getTo() {
		List<Integer> s = new ArrayList<Integer>();
		for (CapEdge ce : getEdges()) {
			s.add(ce.getToPoint());
		}
		return s;
	}
	// get white to points from edges of this node
	// (and itself if it's white)
	public List<Integer> getWhites() {
		List<Integer> whites = new ArrayList<Integer>();
		for (CapEdge ce : getEdges()) {
			if (ce.getToNode().getColor().equals("white")) {
				whites.add(ce.getToPoint());
			}	
		}
		if (this.getColor().equals("white")) {
			whites.add(this.num);
		}
		return whites;
	}
	// get white to points from edges of this node
	// within 2 nodes and itself if it's white
	/*public List<Integer> getWithin2Whites() {
		List<Integer> whites = new ArrayList<Integer>();
		for (CapEdge ce : getEdges()) {
			if (ce.getToNode().getColor().equals("white")) {
				whites.add(ce.getToPoint());
			}
			for (CapEdge ce2 : ce.getToNode().getEdges()) {
				if (ce2.getToNode().getColor().equals("white") && !whites.contains(ce2.getToNode().getNum())) {
					whites.add(ce2.getToPoint());
				}
			}
		}
		// I don't think I need this as this' neighbors' neighbor contains this
		if (this.getColor().equals("white") && !whites.contains(this.num)) {
			whites.add(this.num);
		}
		return whites;
	}*/
	// make white neighbors grey, i.e. covered
	public void makeWhiteNeighborsGrey() {
		for (CapEdge ce : getEdges()) {
			CapNode node = ce.getToNode();
			if (node.getColor().equals("white")) {
				node.setColor("grey");
			}
		}
	}
}
