/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.graphtheory;

import edu.colorado.phet.cck.elements.branch.Branch;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 31, 2003
 * Time: 4:38:50 AM
 * Copyright (c) Aug 31, 2003 by Sam Reid
 */
public class Graph {
    private ArrayList vertices = new ArrayList();
    private ArrayList edges = new ArrayList();

    public void addVertex(Object vertex) {
        vertices.add(vertex);
    }

    public boolean containsVertex(Object vertex) {
        return vertices.contains(vertex);
    }

    public String toString() {
        return "vertices=" + vertices + ", edges=" + edges;
    }

    public void addConnection(int i, int j) {
        addConnection(vertexAt(i), vertexAt(j), null);
    }

    public Object vertexAt(int i) {
        return vertices.get(i);
    }

    public int numVertices() {
        return vertices.size();
    }

    public int numEdges() {
        return edges.size();
    }

    public DirectedDataEdge[] getEdges(Object vertex) {
        return getEdges(vertices.indexOf(vertex));
    }

    /**
     * Gets all edges connecting to the ith vertex.
     */
    public DirectedDataEdge[] getEdges(int i) {
        ArrayList all = new ArrayList();
        Object v = vertexAt(i);
        for (int j = 0; j < edges.size(); j++) {
            DirectedEdge edge = (DirectedEdge) edges.get(j);
            if (edge.containsVertex(v)) {
                all.add(edge);
            }
        }
        return (DirectedDataEdge[]) all.toArray(new DirectedDataEdge[0]);
    }

    public Object[] getNeighbors(Object vertex) {
        int index = vertices.indexOf(vertex);
        return getNeighbors(index);
    }

    public Object[] getNeighbors(int index) {
        DirectedEdge[] e = getEdges(index);
        Object[] o = new Object[e.length];
        Object vertex = vertexAt(index);
        for (int i = 0; i < o.length; i++) {
            o[i] = e[i].getOppositeVertex(vertex);
        }
        return o;
    }

    public boolean areConnected(Object vertex1, Object vertex2) {
        for (int i = 0; i < edges.size(); i++) {
            DirectedEdge edge = (DirectedEdge) edges.get(i);
            if (edge.containsVertex(vertex1) && edge.containsVertex(vertex2)) {
                return true;
            }
        }
        return false;
    }

    private DirectedPathElement[] getDirectedPathElements(Object lastVertex) {
        return getDirectedPathElements(vertices.indexOf(lastVertex));
    }

    private DirectedPathElement[] getDirectedPathElements(int index) {
        DirectedDataEdge[] ed = getEdges(index);
        DirectedPathElement[] pe = new DirectedPathElement[ed.length];
        Object vertex = vertexAt(index);
        for (int i = 0; i < ed.length; i++) {
            boolean forward = ed[i].getSource() == vertex;
            pe[i] = (new DirectedPathElement(ed[i], forward));
        }
        return pe;
    }

    public Loop[] getLoops() {
        ArrayList all = new ArrayList();
        for (int i = 0; i < vertices.size(); i++) {
            Loop[] loops = getLoops(i);
            addWithoutRepeats(all, loops);
//            loops.addAll(Arrays.asList(getLoops(i)));
        }
        return (Loop[]) all.toArray(new Loop[0]);
    }

    /**
     * Get all loops passing through vertexindex.
     */
    public Loop[] getLoops(int vertexIndex) {
        DirectedPathElement[] pe = getDirectedPathElements(vertexIndex);
        ArrayList all = new ArrayList();
        for (int i = 0; i < pe.length; i++) {
            DirectedPathElement DirectedPathElement = pe[i];
            Loop[] loops = (getLoops(new DirectedPath(DirectedPathElement)));
//            all.addAll(Arrays.asList(loops));
            addWithoutRepeats(all, loops);
        }
        return (Loop[]) all.toArray(new Loop[0]);
    }

    /**
     * Gets all loops that start with the specified path.
     */
    private Loop[] getLoops(DirectedPath path) {
        if (path.isLoop()) {
            return new Loop[]{new Loop(path)};
        } else {
            ArrayList toAdd = new ArrayList();
            Object lastVertex = path.lastVertex();
            DirectedPathElement[] steps = getDirectedPathElements(lastVertex);
            for (int i = 0; i < steps.length; i++) {
                if (path.isLegalStep(steps[i])) {
                    DirectedPath childPath = new DirectedPath(path, steps[i]);
                    Loop[] loops = getLoops(childPath);
                    addWithoutRepeats(toAdd, loops);
//                    toAdd.addAll(Arrays.asList(loops));
                }
            }
            return (Loop[]) toAdd.toArray(new Loop[0]);
        }
    }

    public void addWithoutRepeats(ArrayList list, Object[] o) {
        for (int i = 0; i < o.length; i++) {
            Object obj = o[i];
            addWithoutRepeats(list, obj);
        }
    }

    public void addWithoutRepeats(ArrayList list, Object o) {
        if (list.contains(o))
            return;
        else
            list.add(o);
    }

    public DirectedDataEdge edgeAt(int i) {
        return (DirectedDataEdge) edges.get(i);
    }

    public void addConnection(Object startJunction, Object endJunction) {
        addConnection(startJunction, endJunction, null);
    }

    public void addConnection(Object start, Object end, Object data) {
//        DirectedEdge e = new DirectedEdge(start,end,data);
        DirectedDataEdge dde = new DirectedDataEdge(start, end, data);
        edges.add(dde);
    }

    public int indexOfEdgeForData(Branch branch) {
        for (int i = 0; i < edges.size(); i++) {
            DirectedDataEdge directedDataEdge = (DirectedDataEdge) edges.get(i);
            if (directedDataEdge.getData() == branch)
                return i;
        }
        return -1;
    }
}
