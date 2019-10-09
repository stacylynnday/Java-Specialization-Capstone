package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import graph.CapEdge;
import graph.CapNode;
import util.GraphLoader;

/**
 * @author Stacy Lynn Day
 * 
 * (For the warm up assignment, you must implement your Graph in a class
 * named CapGraph.  Here is the stub file.)
 * 
 * This is the Capstone Project for Coursera's Object Oriented Java Programming: 
 * Data Structures and Beyond Specialization.
 * 
 * For the capstone I chose to research approximations to the Minimum Dominating Set
 * problem using a sample twitter network. 
 *
 */
public class CapGraph implements Graph {
	
	// each point, i.e. vertice/node id is associated with a node, i.e person
	private HashMap<Integer, CapNode> vertices;
	// all edges, i.e. connections in the CapGraph
	private HashSet<CapEdge> edges;
		
	/** 
	 * Create a new empty CapGraph 
	 */
	public CapGraph() {
		vertices = new HashMap<Integer, CapNode>();
		edges = new HashSet<CapEdge>();
	}
	
	public int getNumVertices() {
		return vertices.size();
	}
	
	public int getNumEdges() {
		return edges.size();
	}

	/* (non-Javadoc)
	 * @see graph.Graph#addVertex(int)
	 */
	@Override
	public void addVertex(int num) {
		// create a new node and add to vertices if not already there
		CapNode node = new CapNode(num);
		if (!vertices.containsKey(num)) {
			this.vertices.put(num, node);
		}
	}

	/* (non-Javadoc)
	 * @see graph.Graph#addEdge(int, int)
	 */
	@Override
	public void addEdge(int from, int to) {
		// check to make sure there exist CapNodes for these points
		CapNode fromNode = vertices.get(from);
		CapNode toNode = vertices.get(to);

		// check these nodes are valid
		if (fromNode == null) {
			throw new NullPointerException("addEdge: fromNode: " + from + "is not in MapGraph");
		}	
		if (toNode == null) {
			throw new NullPointerException("addEdge: toNode: " + to + "is not in MapGraph");
		}	

		// if valid add to graph
		CapEdge edge1 = new CapEdge(fromNode, toNode);
		this.edges.add(edge1);
				
		// since all vertices are added first, we need to add this edge to the node
		// edge it's from point originates
		fromNode.addEdge(edge1);
	}
	
	// get edge if there is one
	public CapEdge getEdge(int from, int to) {
		for (CapEdge edge : this.edges) {
			if (edge.getFromPoint()==from && edge.getToPoint()==to) {
				return edge;
			}
		}
		return null;
	}
	
	public List<Integer> getNeighbors(int v) {
		List<Integer> neighbors = vertices.get(v).getTo();
		return neighbors;
	}
	
	/* (non-Javadoc)
	 * @see graph.Graph#exportGraph()
	 */
	@Override
	public HashMap<Integer, HashSet<Integer>> exportGraph() {
		// TODO Auto-generated method stub
		HashMap<Integer, HashSet<Integer>> hm = new HashMap<Integer, HashSet<Integer>>();
		for (int key : vertices.keySet()) {
			HashSet<Integer> hs = new HashSet<Integer>();
			for (int x : vertices.get(key).getTo()) {
				hs.add(x);
			}
			hm.put(key, hs);
		}
		return hm;
	}

	/* (non-Javadoc)
	 * @see graph.Graph#getEgonet(int)
	 */
	@Override
	public Graph getEgonet(int center) {
		// TODO Auto-generated method stub
		if (!this.vertices.containsKey(center)) {
			//System.out.println("This vertice not in graph");
			return null;
		}
		CapGraph egonet = new CapGraph();
		egonet.addVertex(center);
		
		// add all neighbors of center and edges between them
		for (int num: this.getNeighbors(center)) {
			egonet.addVertex(num);
			egonet.addEdge(center, num);
		}
		
		// add all edges between neighbors
		for (int num2: this.getNeighbors(center)) {
			for (int x : vertices.get(num2).getTo()) {
				if (egonet.vertices.containsKey(x)) {
					egonet.addEdge(num2, x);
				}
			}
		}	
		return egonet;
	}
	
	/* (non-Javadoc)
	 * SCCs = Strongly Connected Components
	 * @see graph.Graph#getSCCs()
	 */
	@Override
	public List<Graph> getSCCs() {
		// create Stack vertices
		Stack<Integer> vertices1 = new Stack<Integer>();
		for (int v : this.vertices.keySet()) {
			vertices1.push(v);
		}
		// first depth first search
		Stack<Integer> finished = DFS(this, vertices1);
		// compute transpose of graph
		CapGraph transpose = getTranspose(this);
		
		// get SCCs from finished
		List<Graph> sccs = getSCCsHelper(transpose, finished);
		return sccs;
	}
	
	// get SCCS helper method
	public List<Graph> getSCCsHelper(CapGraph transpose, Stack<Integer> vertices) {
		// Initialize visited and List of CapGraphs
		Set<Integer> visited = new HashSet<Integer>();
		List<Graph> sccs = new ArrayList<Graph>();
		// iterate through visited to explore all CapNodes in graph
		while (!vertices.isEmpty()) {
			int v = vertices.pop();
			// if node hasn't been visited yet, visit it
			if (!visited.contains(v)) {
				//System.out.println("creating new Graph");
				CapGraph g = new CapGraph();
				getSCCsHelper_VISIT(transpose, v, visited, g);
			    // add edges to this graph, then add graph to return list
			    //System.out.println("     Adding Edges");
			    for (int vertice: g.vertices.keySet()) {
			    	for (int x : this.vertices.get(vertice).getTo()) {
			    		if (g.vertices.containsKey(x)) {
			    			g.addEdge(vertice, x);
			    		}
			    	}
			    }
			    // add this graph to sccs (strongly connected components) list
			    //System.out.println("Adding graph to scc list");
			    sccs.add(g);
			}
		}
		
		return sccs;
	}
	
	// getSCCsHelper helper method
	public void getSCCsHelper_VISIT(CapGraph graph, int v, Set<Integer> visited, CapGraph g) {
			// add v to visited and add v to CapGraph g
			visited.add(v);
			//System.out.println("     Adding " + v);
			g.addVertex(v);
			// if neighbor hasn't been visited, visit it
			for (int n: graph.getNeighbors(v)) {
				if (!visited.contains(n)) {
					getSCCsHelper_VISIT(graph, n, visited, g);
				}	
			}
		}
	
	// get the transpose of given graph 
	// helper for SCC
	public CapGraph getTranspose(CapGraph capGraph) {
		CapGraph transpose = new CapGraph();
		// add vertices
		for (int v : capGraph.vertices.keySet()) {
			transpose.addVertex(v);
		}		
		// add edges
		for (CapEdge e : capGraph.edges) {
			int to = e.getFromPoint();
			int from = e.getToPoint();
			transpose.addEdge(from, to);
		}
		return transpose;
	}
	
	// Depth first search helper method for getSCCs
	public Stack<Integer> DFS(CapGraph graph, Stack<Integer> vertices1) {
		// Initialize visited and finished
		Set<Integer> visited = new HashSet<Integer>();
		Stack<Integer> finished = new Stack<Integer>();
		// iterate through visited to explore all CapNodes in graph
		while (!vertices1.isEmpty()) {
			int v = vertices1.pop();
			// if node hasn't been visited yet, visit it
			if (!visited.contains(v)) {
				DFS_VISIT(graph, v, visited, finished);
			}
		}
		return finished;
	}
	
	// DFS helper method
	public void DFS_VISIT(CapGraph graph, int v, Set<Integer> visited, Stack<Integer> finished) {
		// add v to visited
		//System.out.println("adding v=" + v + " to visited stack");
		visited.add(v);
		// if neighbor hasn't been visited, visit it
		for (int n: graph.getNeighbors(v)) {
			if (!visited.contains(n)) {
				DFS_VISIT(graph, n, visited, finished);
			}	
		}
		// push v on finished
		finished.push(v);
	}
	

	/* For Minimum Dominating Set Approximations:
	 * white vertices are uncovered.
	 * grey vertices are covered.
	 * black vertices are covered and part of the MDS.
	 */
	// gets white neighbors and also itself if white
	public List<Integer> getWhiteNeighbors(int v) {
		List<Integer> neighbors = getNeighbors(v);
		List<Integer> whiteNeighbors = new ArrayList<Integer>();
		for (int n : neighbors) {
			if (vertices.get(n).getColor().equals("white")) {
				whiteNeighbors.add(n);
			}
		}
		// add itself if it's white
		if (vertices.get(v).getColor().equals("white")) {
			whiteNeighbors.add(v);
		}
		return whiteNeighbors;
	}
	
	// helper method for Dominating Sets
	public void initializeAllVerticesToWhite() {
		// mark all vertices as "uncovered", i.e. white
		for (int v : vertices.keySet()) {
			CapNode node = vertices.get(v);
			node.setColor("white");
		}
	}
	
	// helper method for Dominating Sets
	// gets IDs of all white nodes
	public Set<Integer> getWhiteSet() {
		Set<Integer> whiteSet = new HashSet<Integer>();
		for (int v : vertices.keySet()) {
			if (vertices.get(v).getColor().equals("white")) {
				whiteSet.add(v);
			}
		}
		return whiteSet;
	}	
	
	// method #1: creates a max priority queue of CapNodes based on # of outgoing edges
	// and for each CapNode popped, if it's white, adds it to the Dom Set
	public List<Integer> getFastGreedyDomSet() {	
		
		// mark all vertices as uncovered, i.e. "white"
		initializeAllVerticesToWhite();
		
		// get list of white vertices
		// (initially all vertices are white)
		Set<Integer> whiteSet = getWhiteSet();
		
		// get greedy dominant set
		List<Integer> fastgreedyDomSet = getFastGreedyDOM(whiteSet);
		return fastgreedyDomSet;
	}
	
	// helper method for getFastGreedyDomSet
	public List<Integer> getFastGreedyDOM(Set<Integer> whiteSet)  {
		// initialize an empty dominant set which will contain black vertices
		List<Integer> greedyDomSet = new ArrayList<Integer>();
			
		// create and initialize priority queue
		PriorityQueue<CapNode> pq=
					new PriorityQueue<CapNode>((a,b) -> b.getTo().size() - a.getTo().size());
		for (int v : whiteSet) {
			pq.add(vertices.get(v));	
		}
				
		while (!pq.isEmpty()) {
			// get top node and if white, change to black,
			// add to greedyDomSet and change all white neighbors to grey
			CapNode node = pq.poll();
				
			if (node.getColor().equals("white")) {
				node.setColor("black");
				greedyDomSet.add(node.getNum());
					
				node.makeWhiteNeighborsGrey(); 
						
			}
		}
		return greedyDomSet;
	}
	
	// method #2: this method constantly recreates the max priority queue of CapNodes 
	// based on number of white neighbors the CapNode has
	public List<Integer> getRegularGreedyDomSet() {	
		//System.out.println("Starting Greedy Dom Set ... ");
		
		// mark all vertices as "uncovered", i.e. white
		initializeAllVerticesToWhite();
		
		// create list of white vertices
		// (initially all vertices are white)
		Set<Integer> whiteSet = getWhiteSet();
		
		// get greedy dominant set
		List<Integer> greedyDomSet = getRegGreedyDOM(whiteSet);
		return greedyDomSet;
	}
	
	// helper method for RegularGreedyDomSet
	public List<Integer> getRegGreedyDOM(Set<Integer> whiteSet)  {
		// initialize an empty dominant set
		List<Integer> greedyDomSet = new ArrayList<Integer>();
		
		// while there are white vertices
		// while (!pq.isEmpty()) {
		while (!whiteSet.isEmpty())	 {
			// find the vertex, v, which would cover the most uncovered, white, vertices, 
			// including itself if it's white 
			
			// create and initialize max priority queue
			PriorityQueue<CapNode> pq=
			                new PriorityQueue<CapNode>((a,b) -> b.getWhites().size() - a.getWhites().size());
			//for (int v : vertices.keySet()) {
			for (int v : whiteSet) {
				//if (!greedyDomSet.contains(v) && (vertices.get(v).getWhites().size() > 0)) {
				pq.add(vertices.get(v));
				//}	
			}
			CapNode node = pq.poll();
			
			// add v to the dominant set
			//System.out.println("adding " + node.getNum() + " to dom set");
			greedyDomSet.add(node.getNum());
			// mark that vertex as black and all of its uncovered white neighbors as covered, i.e. grey
			node.setColor("black");
			node.makeWhiteNeighborsGrey();
			
			// remove CapNode ID from whiteSet
			whiteSet.remove(node.getNum());
			
			// remove neighbors from whiteSet
			for (int v: getNeighbors(node.getNum())) {
				whiteSet.remove(v);
			}	
		}
		return greedyDomSet;
	}
	
	// TODO:
	// method3: find largest degree CapNode
	// mark that node black and white neighbors grey
	// add neighbors' white neighbors to new max priority queue
	// based on number of white neighbors
	// repeat once queue is empty
	public List<Integer> getVRegularGreedyDomSet() {	
		//System.out.println("Starting VRegular Greedy Dom Set ... ");
		
		// mark all vertices as "uncovered", i.e. white
		initializeAllVerticesToWhite();
			
		// create list of white vertices
		// (initially all vertices are white)
		Set<Integer> whiteSet = getWhiteSet();
		
		List<Integer> greedyDomSet = new ArrayList<Integer>();
		
		// create and initialize a max heap
		PriorityQueue<CapNode> pq=
				new PriorityQueue<CapNode>((a,b) -> b.getWhites().size() - a.getWhites().size());
		for (int v : whiteSet) {
			pq.add(vertices.get(v));
		}
		
			
			
		// get greedy dominant set
		//List<Integer> greedyDomSet2 = getRegGreedyDOM(whiteSet);
		//greedyDomSet.addAll(greedyDomSet2);
		
		return greedyDomSet;
	}
	
	// TODO: 
	// method4: use SCCs to approximate Min Dom Set
	public List<Integer> getGreedyDomThruSccs() {
		// initialize an empty dominant set
		List<Integer> greedyDomSet = new ArrayList<Integer>();
		
		return greedyDomSet;
	}
	
	// helper method for Dom Sets
	// gets list of IDs of all nodes
	public List<Integer> getNodeIDList() {
		List<Integer> IDList = new ArrayList<Integer>();
		for (int v : vertices.keySet()) {
			IDList.add(v);
		}
		return IDList;
	}
	
	// TODO: fix this or delete
	// helper method for random1GreedyDomSet
	public List<Integer> getRandom1GreedyDOM(Set<Integer> whiteSet, List<Integer> randomList)  {
		// initialize an empty dominant set
		List<Integer> greedyDomSet = new ArrayList<Integer>();
		
		// random used for randomly choosing vertices
		Random randomGenerator = new Random();
			
		// while there are white vertices
		while (!whiteSet.isEmpty())	 {
			// randomly choose a vertex and then choose neighbor that covers most vertices
			// to add to dominant set
			
			//randomly pick a vertex/node
			int index = randomGenerator.nextInt(randomList.size());
			
			int vertex = randomList.get(index);
			//System.out.println("looking at vertex: " + vertex);
			
			// create and initialize priority queue
			// of neighbors of index vertex
			PriorityQueue<CapNode> pq=
				       new PriorityQueue<CapNode>((a,b) -> b.getWhites().size() - a.getWhites().size());
			if (!getNeighbors(vertex).isEmpty()) {
				for (int v : getNeighbors(vertex)) {
					//System.out.println("Looking at neighbor: " + v);
					if (!greedyDomSet.contains(v) && (vertices.get(v).getWhites().size() > 0)) {
						pq.add(vertices.get(v));
					}	
				}
			}
			
			// add vertex itself
			pq.add(vertices.get(vertex));
				
			// chose neighbor with highest span
			CapNode node = pq.poll();
				
			// add v to the dominant set
			//System.out.println("adding " + node.getNum() + " to dom set");
			greedyDomSet.add(node.getNum());
				
			// mark that vertex as black and all of its uncovered neighbors as covered, i.e. grey
			node.setColor("black");
			node.makeWhiteNeighborsGrey();
				
			// remove vertex from whiteSet and randomSet
			whiteSet.remove(node.getNum());
			randomList.remove(index);
				
			// remove neighbors from whiteSet
			for (int v: getNeighbors(node.getNum())) {
				whiteSet.remove(v);
			}	
		}
		return greedyDomSet;
	}
	
	// TODO: fix this or delete it
	public List<Integer> getRandom1GreedyDomSet() {	
		System.out.println("starting Random 1 Greedy Dom Set");
		
		// mark all vertices as "uncovered", i.e. white
		initializeAllVerticesToWhite();
		
		// create set of white vertices
		// (initially all vertices are white)
		Set<Integer> whiteSet = getWhiteSet();
		// create list to get random vertices, white or gray
		List<Integer> randomList = getNodeIDList();
		
		// get random greedy dominant set
		List<Integer> greedyDomSet = getRandom1GreedyDOM(whiteSet, randomList);
		return greedyDomSet;
	}
	
	// TODO: fix or delete this
	// helper method for random1GreedyDomSet
	public List<Integer> getRandom2GreedyDOM(Set<Integer> whiteSet, List<Integer> randomList)  {
		// initialize an empty dominant set
		List<Integer> greedyDomSet = new ArrayList<Integer>();
			
		// random used for randomly choosing vertices
		Random randomGenerator = new Random();
				
		// while there are white vertices
		// while (!pq.isEmpty()) {
		while (!whiteSet.isEmpty())	 {
			// randomly choose a vertex and then choose neighbor that covers most vertices
			// to add to dominant set
				
			//randomly pick a vertex/node
			int index = randomGenerator.nextInt(randomList.size());
				
			int vertex = randomList.get(index);
			//System.out.println("looking at vertex: " + vertex);
				
			// create and initialize priority queue
			// of neighbors of index vertex
			PriorityQueue<CapNode> pq=
				       new PriorityQueue<CapNode>((a,b) -> b.getWhites().size() - a.getWhites().size());
			if (!getNeighbors(vertex).isEmpty()) {
				for (int v1 : getNeighbors(vertex)) {
					//System.out.println("Looking at neighbor: " + v);
					if (!greedyDomSet.contains(v1) && (vertices.get(v1).getWhites().size() > 0)) {
						pq.add(vertices.get(v1));
					}
					/*for (int v2 : getNeighbors(v1)) {
						//System.out.println("Looking at neighbor: " + v);
						if (!greedyDomSet.contains(v2) && (vertices.get(v2).getWhites().size() > 0) && 
								!pq.contains(vertices.get(v2))) {
							pq.add(vertices.get(v2));
						}
					}	*/
				}
			}		
			// add vertex itself
			pq.add(vertices.get(vertex));
					
			// chose neighbor with highest span
			CapNode node = pq.poll();
					
			// add v to the dominant set
			//System.out.println("adding " + node.getNum() + " to dom set");
			greedyDomSet.add(node.getNum());
					
			// mark that vertex as black and all of its uncovered neighbors as covered, i.e. grey
			node.setColor("black");
			node.makeWhiteNeighborsGrey();
					
			// remove vertex from whiteSet and randomSet
			whiteSet.remove(node.getNum());
			randomList.remove(index);
					
			// remove neighbors from whiteSet
			for (int v: getNeighbors(node.getNum())) {
				whiteSet.remove(v);
			}	
		}
		return greedyDomSet;
	}
	
	// TODO: fix or delete this
	public List<Integer> getRandom2GreedyDomSet() {	
		System.out.println("starting Random 2 Greedy Dom Set");
		
		// mark all vertices as "uncovered", i.e. white
		initializeAllVerticesToWhite();
		
		// create set of white vertices
		// (initially all vertices are white)
		Set<Integer> whiteSet = getWhiteSet();
		// create list to get random vertices, white or gray
		List<Integer> randomList = getNodeIDList();
		
		// get random greedy dominant set
		List<Integer> greedyDomSet = getRandom2GreedyDOM(whiteSet, randomList);
		return greedyDomSet;
	}
	
	public static void main(String[] args) {
		System.out.print("Creating a new CapGraph (Capstone Graph) using twitter_combined.txt ... ");
		long start = System.currentTimeMillis();
		CapGraph firstCap = new CapGraph();
		GraphLoader.loadGraph(firstCap, "data/twitter_combined.txt");
		// below are datafiles used for testing:
		//GraphLoader.loadGraph(firstCap, "data/scc/test_1.txt");
		//GraphLoader.loadGraph(firstCap, "data/example_test_graph.txt");
		//GraphLoader.loadGraph(firstCap, "data/small_test_graph.txt");
		//GraphLoader.loadGraph(firstCap, "data/facebook_ucsd.txt");
		long end = System.currentTimeMillis();
		System.out.println("DONE.");
	    System.out.println("Time: " + (end-start) + " milliseconds");
		System.out.println("Number of vertices: " + firstCap.getNumVertices());
		System.out.println("Number of edges: " + firstCap.getNumEdges());
		System.out.println();
		
		
		/*System.out.println("Creating SCCs (Strongly Connected Components of graph ... ");
		start = System.currentTimeMillis();
		List<Graph> sccs = firstCap.getSCCs();
		end = System.currentTimeMillis();
		System.out.println("DONE");
		System.out.println("Time: " + (end-start) + " milliseconds");
		System.out.println("There are " + sccs.size() + " sccs in the graph");
		/*for (Graph g : sccs) {
			System.out.println("new scc graph:");
			HashMap<Integer, HashSet<Integer>> hs1 = g.exportGraph();
			for (int key: hs1.keySet()) {
				System.out.print(key + ": ");
				for (int x: hs1.get(key)) {
					System.out.print(x + " ");
				}
				System.out.println();
			}	
		}*/
		
		
		System.out.print("Starting Fast Greedy Dom. Set ... ");
		start = System.currentTimeMillis();
		List<Integer> FGDS = firstCap.getFastGreedyDomSet();
		end = System.currentTimeMillis();
		System.out.println("DONE.");
		System.out.println("FastGreedy time: "  + (end-start) + " milliseconds");
		System.out.println("FastGreedy size: " + FGDS.size());
		System.out.println();
		
		System.out.print("Starting Regular Greedy Dom. Set ... ");
		start = System.currentTimeMillis();
		List<Integer> GDS = firstCap.getRegularGreedyDomSet();
		end = System.currentTimeMillis();
		System.out.println("DONE.");
		System.out.println("RegularGreedy time: "  + (end-start) + " milliseconds");
		System.out.println("RegularGreedy size: " + GDS.size());
		System.out.println();
		
		/*System.out.print("Starting VRegular Greedy Dom. Set ... ");
		start = System.currentTimeMillis();
		List<Integer> VGDS = firstCap.getVRegularGreedyDomSet();
		end = System.currentTimeMillis();
		System.out.println("DONE.");
		System.out.println("VRegularGreedy time: "  + (end-start) + " milliseconds");
		System.out.println("VRegularGreedy size: " + VGDS.size());
		System.out.println();*/
		
		/*start = System.currentTimeMillis();
		List<Integer> RG1DS = firstCap.getRandom1GreedyDomSet();
		end = System.currentTimeMillis();
		System.out.println("RandomGreedy time:"  + (end-start)/1000);
		System.out.println("RandomGreedy size: " + RG1DS.size());

		start = System.currentTimeMillis();
		List<Integer> RG2DS = firstCap.getRandom2GreedyDomSet();
		end = System.currentTimeMillis();
		System.out.println("RandomGreedy time:"  + (end-start)/1000);
		System.out.println("RandomGreedy size: " + RG2DS.size());*/
		
		
		/*for (int i: GDS) {
			System.out.print(i + " ");
		}
		System.out.println();*/
		
		
		// egonet and SCC (plan to use for future MDS approx. 
		/*System.out.println("egonet");
		start = System.currentTimeMillis();
		Graph egonet = firstCap.getEgonet(23);
		end = System.currentTimeMillis();
		System.out.println((end-start)/1000);
		System.out.println("exporting egonet");
		start = System.currentTimeMillis();
		HashMap<Integer, HashSet<Integer>> egoneths = egonet.exportGraph();
		for (int key: egoneths.keySet()) {
			System.out.print(key + ": ");
			for (int x: egoneths.get(key)) {
				System.out.print(x + " ");
			}
			System.out.println();
		}
		end = System.currentTimeMillis();
		System.out.println((end-start)/1000);*/
	}
}
